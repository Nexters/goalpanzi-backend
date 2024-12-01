package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.dto.response.MemberRankResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import com.nexters.goalpanzi.exception.AlreadyExistsException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.*;
import static com.nexters.goalpanzi.domain.mission.MissionStatus.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionMemberService {

    private final MissionValidator missionValidator;

    private final MissionMemberRepository missionMemberRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    private final MissionRetryMessageRepository missionRetryMessageRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PushMessageSender pushMessageSender;
    private final TopicSubscriber topicSubscriber;

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

        if (member.isPushActivated()) {
            eventPublisher.publishEvent(new JoinMissionEvent(mission.getId(), member.getDeviceToken(), member.getNickname()));
        } else {
            cancelRetryPushMessage(member.getId());
        }
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
            missionMembers.forEach(missionMember -> {
                missionMember.updateMissionStatus(mission, memberCount);
                Member member = missionMember.getMember();
                if (missionMember.isCompleted() && member.isPushActivated()) {
                    reserveRetryPushMessage(member.getId(), member.getDeviceToken());
                }
            });
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
                String topic = TopicGenerator.getTopic(mission.getId());
                pushMessageSender.sendGroupData(
                        MISSION_READY.getTitle(),
                        MISSION_READY.getBody(),
                        topic,
                        mission.getId()
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
                String topic = TopicGenerator.getTopic(mission.getId());
                pushMessageSender.sendGroupData(
                        MISSION_CANCELLATION_WARNING.getTitle(),
                        MISSION_CANCELLATION_WARNING.getBody(),
                        topic,
                        mission.getId()
                );
            }
        });
    }

    @Transactional
    public void sendRetryPushMessage() {
        Set<String> keys = missionRetryMessageRepository.keys(LocalDate.now());
        keys.forEach(key -> {
            String deviceToken = missionRetryMessageRepository.find(key);
            if (deviceToken != null) {
                pushMessageSender.sendIndividualNotification(
                        MISSION_RETRY.getTitle(),
                        MISSION_RETRY.getBody(),
                        deviceToken
                );
            }
        });
    }

    @Transactional
    public void subscribeToMyMissions(final Long memberId, final String deviceToken) {
        List<String> topics = findMySubscribableTopic(memberId);
        topics.forEach(topic ->
                topicSubscriber.subscribeToTopic(List.of(deviceToken), topic)
        );
        updateRetryPushMessage(memberId, deviceToken);
    }

    @Transactional
    public void unsubscribeFromMyMissions(final Long memberId, final String deviceToken) {
        List<String> topics = findMySubscribableTopic(memberId);
        topics.forEach(topic ->
                topicSubscriber.unsubscribeFromTopic(List.of(deviceToken), topic)
        );
        cancelRetryPushMessage(memberId);
    }

    private List<String> findMySubscribableTopic(final Long memberId) {
        List<MissionStatus> filter = List.of(CREATED, IN_PROGRESS, PENDING_COMPLETION);
        List<MissionMember> missionMembers = missionMemberRepository.findAllWithMissionByMemberId(memberId);
        List<MissionMember> filteredMissionMembers = missionMembers.stream()
                .filter(it -> isMissionStatusMatching(filter, it))
                .toList();

        return filteredMissionMembers.stream()
                .map(missionMember -> TopicGenerator.getTopic(missionMember.getMission().getId()))
                .collect(Collectors.toList());
    }

    private void reserveRetryPushMessage(final Long memberId, final String deviceToken) {
        long ttl = TimeoutUtils.toMillis(8, TimeUnit.DAYS);
        missionRetryMessageRepository.save(memberId.toString(), deviceToken, ttl);
    }

    private void cancelRetryPushMessage(final Long memberId) {
        missionRetryMessageRepository.delete(memberId.toString());
    }

    private void updateRetryPushMessage(final Long memberId, final String deviceToken) {
        missionRetryMessageRepository.update(memberId.toString(), deviceToken);
    }
}
