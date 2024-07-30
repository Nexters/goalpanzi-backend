package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.MyMissionVerificationCommand;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionStatus;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionStatusRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionRepository missionRepository;
    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionStatusRepository missionStatusRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MissionVerificationResponse> getTodayVerification(final MissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(command.missionId(), LocalDateTime.now().toLocalDate());

        return verifications.stream()
                .sorted(Comparator.comparing((MissionVerification v) -> v.getMember().getId().equals(member.getId())).reversed()
                        .thenComparing(MissionVerification::getCreatedAt))
                .map(this::toMissionVerificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MissionVerificationResponse getMyVerification(final MyMissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        MissionVerification verification =
                missionVerificationRepository.findByMemberIdAndMissionIdAndBoardNumber(command.memberId(), command.missionId(), command.number())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        return new MissionVerificationResponse(member.getNickname(), verification.getImageUrl());
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        Mission mission =
                missionRepository.findById(command.missionId())
                        .orElseThrow(() -> new NotFoundException("TODO"));
        MissionStatus status =
                missionStatusRepository.findByMemberIdAndMissionId(command.memberId(), command.missionId())
                        .orElseThrow(() -> new NotFoundException("TODO"));

        checkVerificationValidation(command.memberId(), command.missionId(), mission, status);

        missionVerificationRepository.save(new MissionVerification(member, mission, command.imageUrl()));
        status.verify();
    }

    private MissionVerificationResponse toMissionVerificationResponse(MissionVerification verification) {
        return new MissionVerificationResponse(
                verification.getMember().getNickname(),
                verification.getImageUrl()
        );
    }

    private void checkVerificationValidation(final Long memberId, final Long missionId, final Mission mission, final MissionStatus status) {
        if (isCompletedMission(mission, status)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
        if (isDuplicatedVerification(memberId, missionId)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
    }

    private boolean isCompletedMission(final Mission mission, final MissionStatus status) {
        return status.getVerificationCount() >= mission.getBoardCount();
    }

    private boolean isDuplicatedVerification(final Long memberId, final Long missionId) {
        LocalDate today = LocalDateTime.now().toLocalDate();
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, today).isPresent();
    }
}
