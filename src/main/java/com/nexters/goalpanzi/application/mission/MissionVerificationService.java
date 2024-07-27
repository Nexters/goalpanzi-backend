package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationUploadRequest;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MissionVerificationService {

    private final MissionRepository missionRepository;
    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionStatusRepository missionStatusRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MissionVerificationResponse getVerificationImage(final Long userId, final Long missionId, final Integer number) {
        MissionVerification verification = missionVerificationRepository.findByMemberIdAndMissionIdAndBoardNumber(userId, missionId, number)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        return new MissionVerificationResponse(verification.getImageUrl());
    }

    @Transactional
    public void uploadVerificationImage(final Long userId, final Long missionId, final MissionVerificationUploadRequest uploadRequest) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        Mission mission = missionRepository.findById(userId).orElseThrow(() -> new NotFoundException("TODO"));
        MissionStatus status = missionStatusRepository.findByMemberIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new NotFoundException("TODO"));

        checkVerificationValidation(userId, missionId, mission, status);

        missionVerificationRepository.save(new MissionVerification(member, mission, uploadRequest));
        status.verify();
        missionStatusRepository.save(status);
    }

    private void checkVerificationValidation(final Long userId, final Long missionId, final Mission mission, final MissionStatus status) {
        if (isCompletedMission(mission, status)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
        if (isDuplicatedVerification(userId, missionId)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
    }

    private boolean isCompletedMission(final Mission mission, final MissionStatus status) {
        return status.getVerificationCount() >= mission.getBoardCount();
    }

    private boolean isDuplicatedVerification(final Long userId, final Long missionId) {
        LocalDate today = LocalDateTime.now().toLocalDate();
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(userId, missionId, today).isPresent();
    }
}
