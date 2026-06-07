package com.funalarm.util;

public final class AppConstants {
    public static final LocalTimeRange ALARM_TIME_RANGE = new LocalTimeRange(
            java.time.LocalTime.of(5, 0),
            java.time.LocalTime.of(9, 0)
    );

    public static final int WRONG_BEFORE_SWITCH = 3;
    public static final int EARLY_WAKE_MINUTES = 20;

    public static final String[] MOTIVATION_MESSAGES = {
            "今天又是美好的一天！",
            "美好的一天开始啦！",
            "早起的你，真的超棒！",
            "新的一天，新的希望！",
            "棒极了，向目标前进吧！"
    };

    public static final String[] DAY_LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private AppConstants() {
    }

    public record LocalTimeRange(java.time.LocalTime start, java.time.LocalTime end) {
    }
}
