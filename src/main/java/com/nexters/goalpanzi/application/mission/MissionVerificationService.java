package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.ViewMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationViewRepository missionVerificationViewRepository;
    private final MemberRepository memberRepository;

    private final ObjectStorageClient objectStorageClient;

    @Transactional(readOnly = true)
    public MissionVerificationsResponse getVerifications(final MissionVerificationQuery query) {
        LocalDate date = query.date() == null ? LocalDate.now() : query.date();

        Member member = memberRepository.getMember(query.memberId());
        MissionMembers missionMembers = new MissionMembers(missionMemberRepository.findAllByMissionId(query.missionId()));
        missionMembers.verifyMissionMember(member);
        List<MissionVerification> missionVerifications = missionVerificationRepository.findAllByMissionIdAndDate(query.missionId(), date);

        return new MissionVerificationsResponse(sortMissionVerifications(member, query.sortType(), query.direction(), missionVerifications, missionMembers.getMissionMembers()));
    }

    private List<MissionVerificationResponse> sortMissionVerifications(final Member member, final MissionVerificationQuery.SortType sortType, final Sort.Direction direction, final List<MissionVerification> missionVerifications, final List<MissionMember> missionMembers) {
        List<MissionVerificationResponse> response = new ArrayList<>();
        Map<Long, MissionVerification> map = missionVerifications.stream()
                .collect(Collectors.toMap(missionVerification -> missionVerification.getMember().getId(), missionVerification -> missionVerification));

        missionMembers.forEach(missionMember -> {
            Member member1 = missionMember.getMember();
            MissionVerification missionVerification = map.get(member1.getId());
            if (missionVerification == null) {
                MissionVerificationResponse missionVerificationResponse = MissionVerificationResponse.of(member1, Optional.empty(), Optional.empty());
                response.add(missionVerificationResponse);
            } else {
                MissionVerificationView missionVerificationView = missionVerificationViewRepository.getMissionVerificationView(missionVerification.getId(), member1.getId());
                MissionVerificationResponse missionVerificationResponse = MissionVerificationResponse.of(member1, Optional.of(missionVerification), Optional.of(missionVerificationView));
                response.add(missionVerificationResponse);
            }
        });

        response.sort(compareMissionVerificationResponses(member.getNickname(), sortType, direction));
        return response;
    }

    private static Comparator<MissionVerificationResponse> compareMissionVerificationResponses(final String nickname, final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        return Comparator.comparing((MissionVerificationResponse missionVerificationResponse) -> missionVerificationResponse.nickname().equals(nickname)).reversed()
                .thenComparing((MissionVerificationResponse missionVerificationResponse) -> missionVerificationResponse.viewedAt() == null, Comparator.reverseOrder())
                .thenComparing(compareMissionVerificationResponsesByOrder(sortType, direction));
    }

    private static Comparator<MissionVerificationResponse> compareMissionVerificationResponsesByOrder(final MissionVerificationQuery.SortType sortType, final Sort.Direction direction) {
        switch (sortType) {
            case MissionVerificationQuery.SortType.VERIFIED_AT:
            default:
                if (direction.isAscending()) {
                    return Comparator.comparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.naturalOrder()));
                }
                return Comparator.comparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }
    }

    public MissionVerificationResponse getMyVerification(final MyMissionVerificationQuery query) {
        MissionVerification verification = missionVerificationRepository.getMyVerification(query.memberId(), query.missionId(), query.number());

        return MissionVerificationResponse.verified(verification.getMember(), verification, null);
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        MissionMember missionMember = missionMemberRepository.getMissionMember(command.memberId(), command.missionId());
        Mission mission = missionMember.getMission();

        checkVerificationValidation(command.memberId(), mission, missionMember.getVerificationCount());

        String imageUrl = objectStorageClient.uploadFile(command.imageFile());
        missionMember.verify();
        missionVerificationRepository.save(new MissionVerification(missionMember.getMember(), mission, imageUrl, missionMember.getVerificationCount()));
    }

    private void checkVerificationValidation(final Long memberId, final Mission mission, final Integer verificationCount) {
        if (isCompletedMission(mission, verificationCount)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
        if (isDuplicatedVerification(memberId, mission.getId())) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
        if (!mission.isMissionPeriod()) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_PERIOD);
        }
        if (!mission.isMissionDay()) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_DAY);
        }
        if (!mission.isMissionTime()) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_TIME);
        }
    }

    private boolean isCompletedMission(final Mission mission, final Integer verificationCount) {
        return verificationCount >= mission.getBoardCount();
    }

    private boolean isDuplicatedVerification(final Long memberId, final Long missionId) {
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, LocalDate.now()).isPresent();
    }

    @Transactional
    public void deleteAllByMemberId(final Long memberId) {
        missionVerificationRepository.findAllByMemberId(memberId)
                .forEach(BaseEntity::delete);
    }

    @Transactional
    public void deleteAllByMissionId(final Long missionId) {
        missionVerificationRepository.findAllByMissionId(missionId)
                .forEach(BaseEntity::delete);
    }

    @Transactional
    public void viewMissionVerification(final ViewMissionVerificationCommand command) {
        Member member = memberRepository.getMember(command.memberId());
        MissionVerification missionVerification = missionVerificationRepository.findById(command.missionVerificationId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        missionVerificationViewRepository.save(new MissionVerificationView(missionVerification, member));
    }
}
