package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationCommand;
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
import java.time.LocalDateTime;
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
    public List<MissionVerificationResponse> getVerifications(final MissionVerificationCommand command) {
        LocalDate date = command.date() != null ? command.date() : LocalDate.now();
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(command.missionId());
        List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(command.missionId(), date);

        Map<Long, MissionVerification> verificationMap = verifications.stream()
                .collect(Collectors.toMap(v -> v.getMember().getId(), v -> v));

        return missionMembers.stream()
                .map(m -> {
                    MissionVerification v = verificationMap.get(m.getMember().getId());
                    return v != null
                            ? MissionVerificationResponse.verified(m.getMember(), v)
                            : MissionVerificationResponse.notVerified(m.getMember());
                })
                .sorted(Comparator.comparing((MissionVerificationResponse r) -> r.nickname().equals(member.getNickname())).reversed()
                        .thenComparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public MissionVerificationResponse getMyVerification(final MyMissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        MissionVerification verification =
                missionVerificationRepository.findByMemberIdAndMissionIdAndBoardNumber(command.memberId(), command.missionId(), command.number())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        return MissionVerificationResponse.verified(member, verification);
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        Mission mission =
                missionRepository.findById(command.missionId())
                        .orElseThrow(() -> new NotFoundException("TODO"));
        MissionMember missionMember =
                missionMemberRepository.findByMemberIdAndMissionId(command.memberId(), command.missionId())
                        .orElseThrow(() -> new NotFoundException("TODO"));

        checkVerificationValidation(command.memberId(), command.missionId(), mission, missionMember);

        String imageUrl = objectStorageManager.uploadFile(command.imageFile());
        missionVerificationRepository.save(new MissionVerification(member, mission, imageUrl));
        missionMember.verify();
    }

    private void checkVerificationValidation(final Long memberId, final Long missionId, final Mission mission, final MissionMember missionMember) {
        if (isCompletedMission(mission, missionMember)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
        if (isDuplicatedVerification(memberId, missionId)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
    }

    private boolean isCompletedMission(final Mission mission, final MissionMember missionMember) {
        return missionMember.getVerificationCount() >= mission.getBoardCount();
    }

    private boolean isDuplicatedVerification(final Long memberId, final Long missionId) {
        LocalDate today = LocalDateTime.now().toLocalDate();
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, today).isPresent();
    }
}
