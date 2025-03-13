package com.nexters.goalpanzi.application.device;

import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.device.repository.DeviceSubscriptionRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.never;

class DeviceSubscriptionServiceTest extends IntegrationTest {

    @Autowired
    private DeviceSubscriptionService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceSubscriptionRepository deviceSubscriptionRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private PushMessageProxy pushMessageProxy;

    @AfterEach
    void tearDown() {
        missionMemberRepository.deleteAllInBatch();
        deviceSubscriptionRepository.deleteAllInBatch();
        deviceRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class subscribeToMission {

        @Nested
        @DisplayName("알림이 활성화된 디바이스라면")
        class whenPushActivated {

            @Test
            void 미션을_구독한다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));

                sut.subscribeToMission(member.getId(), mission);

                assertAll(
                        () -> BDDAssertions.then(deviceSubscriptionRepository.findAll()).hasSize(1),
                        () -> BDDMockito.then(pushMessageProxy)
                                .should()
                                .subscribeToTopic(List.of(device.getDeviceToken()), Topic.generate(mission.getId()))
                );
            }
        }

        @Nested
        @DisplayName("알림이 비활성화된 디바이스라면")
        class whenPushDeactivated {

            @Transactional
            @Test
            void 미션을_구독하지_않는다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
                device.updatePushActivationStatus(false);
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));

                sut.subscribeToMission(member.getId(), mission);

                assertAll(
                        () -> BDDAssertions.then(deviceSubscriptionRepository.findAll()).hasSize(0),
                        () -> BDDMockito.then(pushMessageProxy)
                                .should(never())
                                .subscribeToTopic(List.of(device.getDeviceToken()), Topic.generate(mission.getId()))
                );
            }
        }
    }

    @Test
    void 특정_미션을_구독한_디바이스를_모두_찾아_구독을_해지한다() {
        final Member member1 = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
        final Member member2 = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
        final Device device1 = deviceRepository.save(new Device(member1, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Device device2 = deviceRepository.save(new Device(member2, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission mission = missionRepository.save(Mission.create(
                member1.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        deviceSubscriptionRepository.saveAll(List.of(
                new DeviceSubscription(device1, mission),
                new DeviceSubscription(device2, mission)
        ));

        sut.unsubscribeFromMission(mission.getId());

        assertAll(
                () -> BDDAssertions.then(deviceSubscriptionRepository.findAll()).hasSize(0),
                () -> BDDMockito.then(pushMessageProxy)
                        .should()
                        .unsubscribeFromTopic(
                                List.of(device1.getDeviceToken(), device2.getDeviceToken()),
                                Topic.generate(mission.getId()))
        );
    }

    @Transactional
    @Test
    void 미션_호스트는_삭제한_미션에_대해_구독을_해지한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission mission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        deviceSubscriptionRepository.save(new DeviceSubscription(device, mission));
        mission.delete();

        sut.unsubscribeFromDeletedMissionForHost(member.getId(), mission.getId());

        assertAll(
                () -> BDDAssertions.then(deviceSubscriptionRepository.findAll()).hasSize(0),
                () -> BDDMockito.then(pushMessageProxy)
                        .should()
                        .unsubscribeFromTopic(
                                List.of(device.getDeviceToken()),
                                Topic.generate(mission.getId()))
        );
    }

    @Test
    void 구독_가능한_미션을_찾아_구독을_시작한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission subscribedMission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now().minusDays(15),
                LocalDateTime.now().plusDays(15),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        deviceSubscriptionRepository.save(new DeviceSubscription(device, subscribedMission));
        final Mission unsubscribedMission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        missionMemberRepository.saveAll(List.of(
                new MissionMember(member, subscribedMission, 0),
                new MissionMember(member, unsubscribedMission, 0)
        ));

        sut.subscribeToMyMissions(member.getId(), device.getDeviceIdentifier());

        assertAll(
                () -> BDDAssertions.then(deviceSubscriptionRepository.findAll()).hasSize(2),
                () -> BDDMockito.then(pushMessageProxy)
                        .should()
                        .subscribeToTopic(List.of(device.getDeviceToken()), Topic.generate(subscribedMission.getId())),
                () -> BDDMockito.then(pushMessageProxy)
                        .should()
                        .subscribeToTopic(List.of(device.getDeviceToken()), Topic.generate(unsubscribedMission.getId()))
        );
    }

    @Test
    void 구독한_미션을_모두_구독_해제한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission mission = missionRepository.save(Mission.create(
                member.getId(),
                DESCRIPTION,
                LocalDateTime.now().minusDays(15),
                LocalDateTime.now().plusDays(15),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        ));
        deviceSubscriptionRepository.save(new DeviceSubscription(device, mission));

        sut.unsubscribeFromMyMissions(member.getId(), device.getDeviceIdentifier());

        assertAll(
                () -> BDDAssertions.then(deviceSubscriptionRepository.findAll()).hasSize(0),
                () -> BDDMockito.then(pushMessageProxy)
                        .should()
                        .unsubscribeFromTopic(List.of(device.getDeviceToken()), Topic.generate(mission.getId()))
        );
    }
}