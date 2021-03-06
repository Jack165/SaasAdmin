package com.feng.boot.admin.commons.lang.date;

/**
 * 时间毫秒数
 *
 * @author bing_huang
 * @since 1.0.1
 */
public enum DateMsUnit {
    /**
     * 一毫米
     */
    MS(1),
    /**
     * 一秒的毫秒数
     */
    SECOND(MS.getMillis() * 1000),
    /**
     * 一分钟的毫秒数
     */
    MINUTE(SECOND.getMillis() * 60),
    /**
     * 一小时的毫秒数
     */
    HOUR(MINUTE.getMillis() * 60),
    /**
     * 一天的毫秒数
     */
    DAY(HOUR.getMillis() * 24),
    /**
     * 一周的毫秒数
     */
    WEEK(DAY.getMillis() * 7);
    private final long millis;

    DateMsUnit(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }
}
