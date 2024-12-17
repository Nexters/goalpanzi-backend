package com.nexters.goalpanzi.application.auth.event;

// FIXME: 한 계정에 여러 개의 디바이스가 있을 수 있음, 다른 기기에 영향을 주지 않도록 기기 식별자 필요
public record LoginEvent(
        Long memberId
) {
}
