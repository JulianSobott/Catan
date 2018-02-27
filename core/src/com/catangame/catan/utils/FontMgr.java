package com.catangame.catan.utils;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontMgr {
    static private HashMap<Type, FreeTypeFontGenerator> fontGenerator = new HashMap<>();
    static private HashMap<FontSpec, BitmapFont> fontMap = new HashMap<>();

    public enum Type {
        DEFAULT,
    }

    static class FontSpec {
        Type type;
        int size;

        FontSpec(Type type, int size) {
            this.type = type;
            this.size = size;
        }
    }

    public static void init() {
        fontGenerator.put(Type.DEFAULT, new FreeTypeFontGenerator(Gdx.files.local("assets/res/Canterbury.ttf")));

    }

    public static void dispose() {
        for (Entry<FontSpec, BitmapFont> bmf : fontMap.entrySet()) {
            try {
                bmf.getValue().dispose();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        for (Entry<Type, FreeTypeFontGenerator> fg : fontGenerator.entrySet()) {
            fg.getValue().dispose();
        }
    }

    public static BitmapFont getFont(Type type, int size) {
        FontSpec spec = new FontSpec(type, size);
        if (!fontMap.containsKey(spec)) {// create a new font
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = size;
            parameter.flip = true;
            fontMap.put(spec, fontGenerator.get(type).generateFont(parameter));
        }
        return fontMap.get(spec);
    }

    public static BitmapFont getFont(int size) {
        return getFont(Type.DEFAULT, size);
    }
}