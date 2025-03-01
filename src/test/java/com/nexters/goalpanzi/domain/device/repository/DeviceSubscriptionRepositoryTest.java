package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.common.support.IntegrationTest;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.BDDAssertions.then;

class DeviceSubscriptionRepositoryTest extends IntegrationTest {

    @Autowired
    private DeviceSubscriptionRepository sut;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    @AfterEach
    void tearDown() {
        sut.deleteAllInBatch();
        deviceRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 특정_디바이스의_구독_현황을_미션과_디바이스와_함께_조회한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission mission = missionRepository.save(
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
        sut.save(new DeviceSubscription(device, mission));

        final List<DeviceSubscription> actual = sut.findAllWithMissionAndDeviceByDeviceId(device.getId());

        then(actual.getFirst())
                .extracting("device", "mission")
                .containsExactly(device, mission);
    }

    @Test
    void 특정_미션과_관련된_디바이스_구독_현황을_디바이스와_미션과_함께_조회한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission mission = missionRepository.save(
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
        sut.save(new DeviceSubscription(device, mission));

        List<DeviceSubscription> actual = sut.findAllWithDeviceAndMissionByMissionId(mission.getId());

        then(actual.getFirst())
                .extracting("device", "mission")
                .containsExactly(device, mission);
    }

    @Transactional
    @Test
    void 특정_미션과_관련된_디바이스_구독_현황을_삭제한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
        final Mission mission = missionRepository.save(
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
        sut.save(new DeviceSubscription(device, mission));

        sut.deleteAllByMissionId(mission.getId());

        final List<DeviceSubscription> actual = sut.findAllWithDeviceAndMissionByMissionId(mission.getId());

        then(actual.size()).isEqualTo(0);
    }

    @Test
    void 특정_디바이스들이_구독한_특정_미션_구독_현황을_디바이스와_함께_조회한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Mission mission = missionRepository.save(
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
        final Device device1 = deviceRepository.save(new Device(member, "deviceIdentifier1", "deviceToken1", OsType.AOS));
        final Device device2 = deviceRepository.save(new Device(member, "deviceIdentifier2", "deviceToken2", OsType.AOS));
        sut.save(new DeviceSubscription(device1, mission));
        sut.save(new DeviceSubscription(device2, mission));

        final List<DeviceSubscription> actual = sut.findAllWithDeviceByMissionIdAndDeviceIds(
                mission.getId(),
                List.of(device1.getId(), device2.getId())
        );

        then(actual)
                .hasSize(2)
                .extracting("device")
                .containsExactlyInAnyOrder(device1, device2);
    }
}