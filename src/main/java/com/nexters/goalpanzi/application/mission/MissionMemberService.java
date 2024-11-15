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
import com.nexters.goalpanzi.exception.AlreadyExistsException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_CANCELLATION_WARNING;
import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_READY;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionMemberService {

    private final MissionValidator missionValidator;

    private final MissionMemberRepository missionMemberRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PushNotificationSender pushNotificationSender;

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

        if (member.getDeviceToken() != null) {
            eventPublisher.publishEvent(new JoinMissionEvent(mission.getId(), member.getDeviceToken(), member.getNickname()));
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
            List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(mission.getId());
            int memberCount = missionMembers.size();
            missionMembers
                    .forEach(missionMember -> {
                        missionMember.updateMissionStatus(mission, memberCount);
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
        List<Mission> missions = missionRepository.getReadyMissions();
        missions.forEach(mission -> {
            if (mission.isReadyTime() && missionValidator.hasEnoughMember(mission.getId())) {
                String topic = TopicGenerator.getTopic(mission.getId());
                pushNotificationSender.sendGroupMessage(
                        MISSION_READY.getTitle(),
                        MISSION_READY.getBody(),
                        topic
                );
            }
        });
    }

    @Transactional
    public void sendCancellationWarningPushMessage() {
        List<Mission> missions = missionRepository.getReadyMissions();
        missions.forEach(mission -> {
            if (mission.isReadyTime() && !missionValidator.hasEnoughMember(mission.getId())) {
                String topic = TopicGenerator.getTopic(mission.getId());
                pushNotificationSender.sendGroupMessage(
                        MISSION_CANCELLATION_WARNING.getTitle(),
                        MISSION_CANCELLATION_WARNING.getBody(),
                        topic
                );
            }
        });
    }
}
