package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.ViewMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.common.annotation.RedissonLock;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.firebase.PushNotificationMessage;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationViewRepository missionVerificationViewRepository;
    private final MemberRepository memberRepository;

    private final ObjectStorageClient objectStorageClient;
    private final PushNotificationSender pushNotificationSender;

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
        MissionMember missionMember = missionMemberRepository.getMissionMember(command.memberId(), command.missionId());

        missionVerificationValidator.validate(missionMember);

        String imageUrl = objectStorageClient.uploadFile(command.imageFile());
        missionMember.verify();
        missionVerificationRepository.save(new MissionVerification(missionMember.getMember(), missionMember.getMission(), imageUrl, missionMember.getVerificationCount()));
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
            if (mission.isMissionDay() && mission.isPushTime(hour)) {
                List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(mission.getId(), today);
                int verificationCount = verifications.size();
                String topic = TopicGenerator.getTopic(mission.getId());

                if (verificationCount == 0) {
                    sendNoOneVerifiedPushMessage(MISSION_NO_ONE_VERIFIED, topic);
                } else {
                    sendVerifiedPushMessage(MISSION_VERIFIED, topic, verificationCount);
                }
            }
        });
    }

    private void sendVerifiedPushMessage(final PushNotificationMessage message, final String topic, final int verificationCount) {
        pushNotificationSender.sendGroupMessage(
                message.getTitle(verificationCount),
                message.getBody(),
                topic
        );
    }

    private void sendNoOneVerifiedPushMessage(final PushNotificationMessage message, final String topic) {
        pushNotificationSender.sendGroupMessage(
                message.getTitle(),
                message.getBody(),
                topic
        );
    }

    @Transactional
    public void sendVerificationWarningPushMessage() {
        LocalDate today = LocalDate.now();
        int hour = LocalDateTime.now().getHour();
        List<Mission> missions = missionRepository.getInProgressMissions();

        missions.forEach(mission -> {
            if (mission.isMissionDay() && mission.isPushTime(hour)) {
                List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(mission.getId());

                missionMembers.forEach(missionMember -> {
                    Member member = missionMember.getMember();
                    Optional<MissionVerification> verification = missionVerificationRepository.findByMemberIdAndMissionIdAndDate(member.getId(), mission.getId(), today);
                    if (verification.isEmpty() && member.getDeviceToken() != null) {
                        pushNotificationSender.sendIndividualMessage(
                                MISSION_VERIFICATION_WARNING.getTitle(),
                                MISSION_VERIFICATION_WARNING.getBody(),
                                member.getDeviceToken()
                        );
                    }
                });
            }
        });
    }
}
