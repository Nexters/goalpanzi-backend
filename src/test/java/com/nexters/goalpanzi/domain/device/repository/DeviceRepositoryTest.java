package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 특정_디바이스를_멤버와_함께_조회한다() {
        Member member = memberRepository.save(
                Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE)
        );
        Device device = deviceRepository.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));

        List<Device> devices = deviceRepository.findAllWithMemberByDeviceIdentifier(device.getDeviceIdentifier());
        assertThat(devices.getFirst().getMember()).isEqualTo(member);
    }
}