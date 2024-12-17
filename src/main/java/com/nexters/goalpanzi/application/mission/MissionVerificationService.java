package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.ViewMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.application.mission.event.CompleteMissionEvent;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.common.annotation.RedissonLock;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.firebase.Devices;
import com.nexters.goalpanzi.domain.firebase.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationViewRepository missionVerificationViewRepository;
    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;

    private final ObjectStorageClient objectStorageClient;
    private final PushMessageSender pushMessageSender;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final MissionVerificationValidator missionVerificationValidator;
    private final MissionVerificationResponseSorter missionVerificationResponseSorter;

    public MissionVerificationsResponse getVerifications(final MissionVerificationQuery query) {
        LocalDate date = query.date() == null ? LocalDate.now() : query.date();

        Member member = memberRepository.getMember(query.memberId());
        MissionMembers missionMembers = new MissionMembers(missionMemberRepository.findAllByMissionId(query.missionId()));
        missionMembers.verifyMissionMember(member);
        List<MissionVerification> missionVerifications = missionVerificationRepository.findAllByMissionIdAndDate(query.missionId(), date);

        return new MissionVerificationsResponse(missionVerificationResponseSorter.sort(member, query.sortType(), query.direction(), missionVerifications, missionMembers.getMissionMembers()));
    }

    public MissionVerificationResponse getMyVerification(final MyMissionVerificationQuery query) {
        MissionVerification verification = missionVerificationRepository.getMyVerification(query.memberId(), query.missionId(), query.number());

        return MissionVerificationResponse.verified(verification.getMember(), verification, null);
    }

    @RedissonLock("MissionVerification")
    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        MissionMember missionMember = missionMemberRepository.getMissionMemberWithMemberAndMission(command.memberId(), command.missionId());

        missionVerificationValidator.validate(missionMember);

        String imageUrl = objectStorageClient.uploadFile(command.imageFile());
        missionMember.verify();
        missionVerificationRepository.save(new MissionVerification(missionMember.getMember(), missionMember.getMission(), imageUrl, missionMember.getVerificationCount()));

        Mission mission = missionMember.getMission();
        if (mission.isEndDate(LocalDate.now()) && isFirstPlace(command.missionId(), missionMember.getMember())) {
            applicationEventPublisher.publishEvent(
                    new CompleteMissionEvent(command.missionId())
            );
        }
    }

    private boolean isFirstPlace(final Long missionId, final Member member) {
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(missionId);
        int rank = MemberRanks.from(missionMembers).getRankByMember(member).rank();
        return rank == 1;
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

        MissionVerificationView missionVerificationView = missionVerificationViewRepository.getMissionVerificationView(command.missionVerificationId(), command.memberId());
        if (missionVerificationView == null) {
            missionVerificationViewRepository.save(new MissionVerificationView(missionVerification, member));
        }
    }

    @Transactional
    public void sendVerificationPushMessage() {
        LocalDate today = LocalDate.now();
        int hour = LocalDateTime.now().getHour();
        List<Mission> missions = missionRepository.getInProgressMissions();

        missions.forEach(mission -> {
            if (mission.isMissionDay() && mission.isVerificationStatusPushTime(hour)) {
                List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(mission.getId(), today);
                int verificationCount = verifications.size();

                if (verificationCount == 0) {
                    sendNoOneVerifiedPushMessage(mission.getId());
                } else {
                    sendVerifiedPushMessage(mission.getId(), verificationCount);
                }
            }
        });
    }

    private void sendVerifiedPushMessage(final Long missionId, final int verificationCount) {
        String topic = TopicGenerator.getTopic(missionId);
        pushMessageSender.sendGroupData(
                MISSION_VERIFIED.getTitle(verificationCount),
                MISSION_VERIFIED.getBody(),
                topic,
                missionId
        );
    }

    private void sendNoOneVerifiedPushMessage(final Long missionId) {
        String topic = TopicGenerator.getTopic(missionId);
        pushMessageSender.sendGroupData(
                MISSION_NO_ONE_VERIFIED.getTitle(),
                MISSION_NO_ONE_VERIFIED.getBody(),
                topic,
                missionId
        );
    }

    @Transactional
    public void sendVerificationWarningPushMessage() {
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.now();
        List<Mission> missions = missionRepository.getInProgressMissions();

        missions.forEach(mission -> {
            if (mission.isMissionDay() && mission.isVerificationWarningPushTime(time)) {
                List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(mission.getId());

                missionMembers.forEach(missionMember -> {
                    Member member = missionMember.getMember();
                    Optional<MissionVerification> verification = missionVerificationRepository.findByMemberIdAndMissionIdAndDate(member.getId(), mission.getId(), today);
                    if (verification.isEmpty()) {
                        sendVerificationWarningMessageForMissionMember(member.getId(), mission.getId());
                    }
                });
            }
        });
    }

    private void sendVerificationWarningMessageForMissionMember(final Long memberId, final Long missionId) {
        Devices devices = new Devices(
                deviceRepository.findAllByMemberId(memberId)
        );
        
        devices.getActivatedDeviceTokens()
                .forEach(deviceToken ->
                        pushMessageSender.sendIndividualData(
                                MISSION_VERIFICATION_WARNING.getTitle(),
                                MISSION_VERIFICATION_WARNING.getBody(),
                                deviceToken,
                                missionId
                        )
                );
    }
}
