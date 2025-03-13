package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_RETRY;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionRetryPushMessageService {

    private final MissionRetryMessageRepository missionRetryMessageRepository;

    private final PushMessageProxy pushMessageProxy;

    @Transactional
    public void sendRetryPushMessage() {
        Set<String> keys = missionRetryMessageRepository.keys(LocalDate.now());
        keys.forEach(key -> {
            String deviceToken = missionRetryMessageRepository.find(key);
            if (deviceToken != null) {
                pushMessageProxy.sendIndividualNotification(
                        MISSION_RETRY.getTitle(),
                        MISSION_RETRY.getBody(),
                        deviceToken
                );
            }
        });
    }

    @Transactional
    public void reserveRetryPushMessage(final Long memberId, final String deviceToken) {
        long ttl = TimeoutUtils.toMillis(8, TimeUnit.DAYS);
        missionRetryMessageRepository.save(memberId.toString(), deviceToken, ttl);
    }

    @Transactional
    public void cancelRetryPushMessage(final Long memberId) {
        missionRetryMessageRepository.deleteAllByMemberId(memberId.toString());
    }

    @Transactional
    public void updateRetryPushMessage(final Long memberId, final String deviceToken) {
        missionRetryMessageRepository.update(memberId.toString(), deviceToken);
    }
}
