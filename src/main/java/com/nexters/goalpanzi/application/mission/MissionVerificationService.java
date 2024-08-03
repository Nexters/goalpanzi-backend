package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.ncp.ObjectStorageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionRepository missionRepository;
    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MemberRepository memberRepository;

    private final ObjectStorageManager objectStorageManager;

    @Transactional(readOnly = true)
    public List<MissionVerificationResponse> getVerifications(final MissionVerificationQuery query) {
        LocalDate date = query.date() != null ? query.date() : LocalDate.now();
        Member member = memberRepository.getMember(query.memberId());
        List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(query.missionId(), date);

        Map<Long, MissionVerification> verificationMap = verifications.stream()
                .collect(Collectors.toMap(v -> v.getMember().getId(), v -> v));

        return getMissionMembers(query.missionId()).stream()
                .map(m -> convertToVerificationResponse(m, verificationMap.get(m.getMember().getId())))
                .sorted(Comparator.comparing((MissionVerificationResponse r) -> r.nickname().equals(member.getNickname())).reversed()
                        .thenComparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public MissionVerificationResponse getMyVerification(final MyMissionVerificationQuery query) {
        MissionVerification verification = missionVerificationRepository
                .findByMemberIdAndMissionIdAndBoardNumber(query.memberId(), query.missionId(), query.number())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        return MissionVerificationResponse.verified(verification.getMember(), verification);
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        MissionMember missionMember = missionMemberRepository
                .findByMemberIdAndMissionId(command.memberId(), command.missionId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_JOINED_MISSION_MEMBER));
        Mission mission = missionRepository.getMission(command.missionId());

        checkVerificationValidation(command.memberId(), mission, missionMember);

        String imageUrl = objectStorageManager.uploadFile(command.imageFile());
        missionVerificationRepository.save(new MissionVerification(missionMember.getMember(), mission, imageUrl));
        missionMember.verify();
    }

    private MissionVerificationResponse convertToVerificationResponse(final MissionMember missionMember, final MissionVerification verification) {
        return verification != null
                ? MissionVerificationResponse.verified(missionMember.getMember(), verification)
                : MissionVerificationResponse.notVerified(missionMember.getMember());
    }

    private void checkVerificationValidation(final Long memberId, final Mission mission, final MissionMember missionMember) {
        if (isCompletedMission(mission, missionMember)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
        LocalDate today = LocalDate.now();
        if (isDuplicatedVerification(memberId, mission.getId(), today)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
        if (!isVerificationDay(mission, today)) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_DAY);
        }
    }

    private boolean isCompletedMission(final Mission mission, final MissionMember missionMember) {
        return missionMember.getVerificationCount() >= mission.getBoardCount();
    }

    private boolean isVerificationDay(final Mission mission, final LocalDate today) {
        return mission.getMissionDays().contains(today.getDayOfWeek());
    }

    private boolean isDuplicatedVerification(final Long memberId, final Long missionId, final LocalDate today) {
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, today).isPresent();
    }

    private List<MissionMember> getMissionMembers(final Long missionId) {
        return missionMemberRepository.findAllByMissionId(missionId);
    }
}
