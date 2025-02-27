package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.application.mission.dto.response.MemberRankResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.application.mission.event.CancelMissionRetryPushMessageEvent;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.application.mission.event.SubscribeToMissionEvent;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.device.Devices;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.exception.AlreadyExistsException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_CANCELLATION_WARNING;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_READY;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionMemberService {

    private final MissionValidator missionValidator;

    private final MissionMemberRepository missionMemberRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PushMessageProxy pushMessageProxy;

    public MissionDetailResponse getJoinableMission(final InvitationCode invitationCode) {
        missionValidator.validateJoinableMission(invitationCode);
        return MissionDetailResponse.from(getMissionByCode(invitationCode));
    }

    @Transactional
    public void joinMission(final Long memberId, final InvitationCode invitationCode) {
        Member member = memberRepository.getMember(memberId);
        Mission mission = getMissionByCode(invitationCode);
        validateAlreadyJoin(member, mission);
        missionValidator.validateMaxPersonnel(mission);
        missionMemberRepository.save(MissionMember.join(member, mission));

        sendJoinPushMessage(member, mission);

        eventPublisher.publishEvent(
                new CancelMissionRetryPushMessageEvent(memberId)
        );
        eventPublisher.publishEvent(
                new SubscribeToMissionEvent(memberId, mission)
        );
    }

    private Mission getMissionByCode(final InvitationCode invitationCode) {
        return missionRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION, invitationCode.getCode()));
    }

    private void validateAlreadyJoin(final Member member, final Mission mission) {
        missionMemberRepository.findByMemberIdAndMissionId(member.getId(), mission.getId())
                .ifPresent(missionMember -> {
                    throw new AlreadyExistsException(ErrorCode.ALREADY_EXISTS_MISSION_MEMBER);
                });
    }

    private void sendJoinPushMessage(final Member member, final Mission mission) {
        if (mission.isHostMember(member.getId())) {
            return;
        }
        Member host = memberRepository.getMember(mission.getHostMemberId());
        Devices devices = new Devices(
                deviceRepository.findAllByMemberId(host.getId())
        );

        devices.getActivatedDeviceTokens()
                .forEach(deviceToken ->
                        eventPublisher.publishEvent(
                                new JoinMissionEvent(mission.getId(), deviceToken, member.getNickname())
                        )
                );
    }

    public MissionsResponse findAllByMemberId(final Long memberId, final List<MissionStatus> filter) {
        Member member = memberRepository.getMember(memberId);
        List<MissionMember> missionMembers = missionMemberRepository.findAllWithMissionByMemberId(memberId);
        if (filter == null || filter.isEmpty()) {
            return MissionsResponse.of(member, missionMembers);
        }

        List<MissionMember> filteredMissionMembers = missionMembers
                .stream()
                .filter(it -> isMissionStatusMatching(filter, it))
                .toList();
        return MissionsResponse.of(member, filteredMissionMembers);
    }

    private boolean isMissionStatusMatching(final List<MissionStatus> filters, final MissionMember missionMember) {
        return filters.contains(missionMember.getMissionStatus());
    }

    @Transactional
    public void deleteAllByMemberId(final Long memberId) {
        missionMemberRepository.findAllWithMissionByMemberId(memberId)
                .forEach(BaseEntity::delete);
    }

    @Transactional
    public void deleteAllByMissionId(final Long missionId) {
        missionMemberRepository.findAllByMissionId(missionId)
                .forEach(BaseEntity::delete);
    }

    public MemberRankResponse getMissionRank(final Long missionId, final Long memberId) {
        Member member = memberRepository.getMember(memberId);
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(missionId);

        MemberRanks memberRanks = MemberRanks.from(missionMembers);

        return MemberRankResponse.from(memberRanks.getRankByMember(member));
    }

    @Transactional
    public void batchUpdateStatus() {
        List<Mission> missions = missionRepository.findAll();
        missions.forEach(mission -> {
            List<MissionMember> missionMembers = missionMemberRepository.findAllWithMemberByMissionId(mission.getId());
            int memberCount = missionMembers.size();
            missionMembers.forEach(missionMember ->
                    missionMember.updateMissionStatus(mission, memberCount)
            );
        });
    }

    @Transactional
    public void viewMissionRank(final Long missionId, final Long memberId) {
        MissionMember missionMember = missionMemberRepository.getMissionMember(memberId, missionId);
        missionMember.checkCompleted();
    }

    @Transactional
    public void sendReadyPushMessage() {
        LocalDateTime now = LocalDateTime.now();
        List<Mission> missions = missionRepository.getReadyMissions();
        missions.forEach(mission -> {
            if (mission.isReadyTime(now) && missionValidator.hasEnoughMember(mission.getId())) {
                String topic = Topic.generate(mission.getId());
                Map<String, String> data = new HashMap<>();
                data.put("missionId", mission.getId().toString());

                pushMessageProxy.sendGroupNotificationWithData(
                        MISSION_READY.getTitle(),
                        MISSION_READY.getBody(),
                        data,
                        topic
                );
            }
        });
    }

    @Transactional
    public void sendCancellationWarningPushMessage() {
        LocalDateTime now = LocalDateTime.now();
        List<Mission> missions = missionRepository.getReadyMissions();
        missions.forEach(mission -> {
            if (mission.isReadyTime(now) && !missionValidator.hasEnoughMember(mission.getId())) {
                String topic = Topic.generate(mission.getId());
                Map<String, String> data = new HashMap<>();
                data.put("missionId", mission.getId().toString());

                pushMessageProxy.sendGroupNotificationWithData(
                        MISSION_CANCELLATION_WARNING.getTitle(),
                        MISSION_CANCELLATION_WARNING.getBody(),
                        data,
                        topic
                );
            }
        });
    }
}
