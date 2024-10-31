package com.nexters.goalpanzi.application.firebase;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum PushNotificationMessage {
    // 미션 시작 전
    MISSION_READY("미션 시작까지 1시간! 준비 갈 완료?\uD83C\uDF40", "이제 꾸준함을 향한 첫 발을 내딛을 시간!"),
    MISSION_JOINED("엇? 누가 미션에 합류했어요! \uD83D\uDE4C", "이제 메이트 %s님과 미션 경쟁을 할 수 있어요."),
    MISSION_CANCELLATION_WARNING("미션 폭파까지 30분 남았어요!\uD83E\uDDE8", "똑딱..똑딱.. 미션을 지키려면 인원이 필요해요!"),
    MISSION_CANCELED("으악!\uD83D\uDCA5 인원이 부족해 미션이 삭제됐어요\uD83D\uDE35", "인원을 채워서 바로 다시 시작해봐요!"),

    // 미션 진행 중
    MISSION_VERIFICATION_WARNING("\u23F0 마감임박! 1시간 남았어요!\uD83E\uDDE8\uD83D\uDCA5", "지금 인증 안 하면 오늘은 인증 실패!ㅠㅠ"),
    MISSION_VERIFIED("˗ˋˏ 와 ˎˊ˗ %s명이 벌써 인증 완료 ˗ˋˏ 와 ˎˊ˗ ", "지금 누가 앞서가는지 확인해볼까요?"),
    MISSION_NO_ONE_VERIFIED("잊었니?..\uD83C\uDF42", "아직 아무도 인증 안 했어요! 1빠로 인증해 모두를 앞서갈 타이밍!"),
    MISSION_COMPLETED("아니 글쎄..걔가 결국 1등 했다고?! \uD83D\uDDEF\uFE0F", "첫 번째 미션 완수자 등장! 빠르게 확인해 보세요!"),
    MISSION_DELETED("뭐? 미션 끝났다고? 너 누군데? \uD83D\uDC40", "방장이 미션을 끝냈어요! 다음 미션에서 새롭게 만나요!"),

    // 미션 종료 후
    MISSION_RETRY("이제 다시 갓생 살 때가 된 것 같은데요? \uD83D\uDE0C", "미션이 끝난 지 7일째! 새로운 미션이 필요할 때죠?");

    private String title;
    private String body;

    public String getTitle(final Object... args) {
        return String.format(title, args);
    }

    public String getBody(final Object... args) {
        return String.format(body, args);
    }
}
