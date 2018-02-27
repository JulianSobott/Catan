package com.catangame.catan.utils;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

public class Clock {
    long milliseconds;

    public Clock() {
        milliseconds = TimeUtils.millis();
    }

    public Time restart() {
        long now = TimeUtils.millis();
        Time temp = new Time(now - milliseconds);
        milliseconds = now;
        return temp;
    }

    public Time getElapsedTime() {
        return new Time(TimeUtils.millis() - milliseconds);
    }
}