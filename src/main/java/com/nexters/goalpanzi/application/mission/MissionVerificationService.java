package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerifications;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionMemberRepository missionMemberRepository;

    private final ObjectStorageClient objectStorageClient;

    @Transactional(readOnly = true)
    public MissionVerificationsResponse getVerifications(final MissionVerificationQuery query) {
        LocalDate date = query.date() != null ? query.date() : LocalDate.now();

        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(query.missionId());
        MissionVerifications missionVerifications = new MissionVerifications(missionVerificationRepository.findAllByMissionIdAndDate(query.missionId(), date));

        return MissionVerificationsResponse.from(
                missionVerifications.sortMissionVerifications(query.memberId(), query.sortType(), query.direction(), missionMembers)
        );
    }

    public MissionVerificationResponse getMyVerification(final MyMissionVerificationQuery query) {
        MissionVerification verification = missionVerificationRepository.getMyVerification(query.memberId(), query.missionId(), query.number());

        return MissionVerificationResponse.verified(verification.getMember(), verification);
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        MissionMember missionMember = missionMemberRepository.getMissionMember(command.memberId(), command.missionId());
        Mission mission = missionMember.getMission();

        checkVerificationValidation(command.memberId(), mission, missionMember);

        String imageUrl = objectStorageClient.uploadFile(command.imageFile());
        missionMember.verify();
        missionVerificationRepository.save(new MissionVerification(missionMember.getMember(), mission, imageUrl, missionMember.getVerificationCount()));
    }

    private void checkVerificationValidation(final Long memberId, final Mission mission, final MissionMember missionMember) {
        if (isCompletedMission(mission, missionMember)) {
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

    private boolean isCompletedMission(final Mission mission, final MissionMember missionMember) {
        return missionMember.getVerificationCount() >= mission.getBoardCount();
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
}
