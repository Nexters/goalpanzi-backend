package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static org.assertj.core.api.BDDAssertions.then;

class DeviceRepositoryTest extends IntegrationTest {

    @Autowired
    private DeviceRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        sut.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 특정_디바이스를_멤버와_함께_조회한다() {
        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.GOOGLE));
        final Device device = sut.save(new Device(member, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));

        final List<Device> actual = sut.findAllWithMemberByDeviceIdentifier(device.getDeviceIdentifier());

        then(actual.getFirst().getMember()).isEqualTo(member);
    }
}