package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class DeviceSubscriptionRepositoryTest {

    @Autowired
    private DeviceSubscriptionRepository deviceSubscriptionRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    private Member member;

    private Mission mission;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(
                Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE)
        );
        mission = missionRepository.save(
                Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(31),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                )
        );
    }

    @Test
    void 특정_디바이스의_구독_현황을_미션과_디바이스와_함께_조회한다() {
        Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        deviceSubscriptionRepository.save(new DeviceSubscription(device, mission));

        List<DeviceSubscription> subscriptions = deviceSubscriptionRepository.findAllWithMissionAndDeviceByDeviceId(device.getId());
        assertAll(
                () -> assertThat(subscriptions.getFirst().getDevice()).isEqualTo(device),
                () -> assertThat(subscriptions.getFirst().getMission()).isEqualTo(mission)
        );
    }

    @Test
    void 특정_미션과_관련된_디바이스_구독_현황을_디바이스와_미션과_함께_조회한다() {
        Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        deviceSubscriptionRepository.save(new DeviceSubscription(device, mission));

        List<DeviceSubscription> subscriptions = deviceSubscriptionRepository.findAllWithDeviceAndMissionByMissionId(mission.getId());
        assertAll(
                () -> assertThat(subscriptions.getFirst().getDevice()).isEqualTo(device),
                () -> assertThat(subscriptions.getFirst().getMission()).isEqualTo(mission)
        );
    }

    @Test
    void 특정_미션과_관련된_디바이스_구독_현황을_삭제한다() {
        Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        deviceSubscriptionRepository.save(new DeviceSubscription(device, mission));

        deviceSubscriptionRepository.deleteAllByMissionId(mission.getId());

        List<DeviceSubscription> subscriptions = deviceSubscriptionRepository.findAllWithDeviceAndMissionByMissionId(mission.getId());
        assertThat(subscriptions.size()).isEqualTo(0);
    }
}