package com.catangame.catan.utils;

import com.badlogic.gdx.utils.Timer;

public class Time {
    long milliseconds;

    Time(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long asMilliseconds() {
        return milliseconds;
    }

    public float asSeconds() {
        return (float) milliseconds / 1000.f;
    }

    public float asMinutes() {
        return (float) milliseconds / 1000.f / 60.f;
    }

    public float asHours() {
        return (float) milliseconds / 1000.f / 60.f / 60.f;
    }
}