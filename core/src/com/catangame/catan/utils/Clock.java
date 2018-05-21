package com.catangame.catan.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class Clock {
    Time mTime;

    public Clock() {
        mTime = new Time(TimeUtils.millis());
    }

    public Time restart() {
        long now = TimeUtils.millis();
        Time temp = new Time(now - mTime.asMilliseconds());
        mTime = new Time(now);
        return temp;
    }

    public Time getElapsedTime() {
        return new Time(TimeUtils.millis() - mTime.asMilliseconds());
    }
}