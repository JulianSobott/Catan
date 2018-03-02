package com.catangame.catan.utils;

import java.io.Serializable;

public class Color implements Serializable {
    public float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b, int a) {
        this.r = (float) r / 255.f;
        this.g = (float) g / 255.f;
        this.b = (float) b / 255.f;
        this.a = (float) a / 255.f;
    }

    public Color(int rgba32) {// TODO test
        r = (rgba32 & 0xff000000) >> 24;
        g = (rgba32 & 0x00ff0000) >> 16;
        b = (rgba32 & 0x0000ff00) >> 8;
        a = (rgba32 & 0x000000ff);
    }

    public com.badlogic.gdx.graphics.Color gdx() {
        return new com.badlogic.gdx.graphics.Color(r, g, b, a);
    }

    public org.jsfml.graphics.Color sfml() {
        return new org.jsfml.graphics.Color((int)(r * 255.f), (int)(g * 255.f), (int)(b * 255.f), (int)(a * 255.f));
    }

    public static final Color TRANSPARENT = new Color(0.f, 0.f, 0.f, 0.f);
    public static final Color BLACK = new Color(0.f, 0.f, 0.f, 1.f);
    public static final Color WHITE = new Color(1.f, 1.f, 1.f, 1.f);
    public static final Color BLUE = new Color(0.f, 0.f, 1.f, 1.f);
    public static final Color RED = new Color(1.f, 0.f, 0.f, 1.f);
    public static final Color GREEN = new Color(0.f, 1.f, 0.f, 1.f);
}