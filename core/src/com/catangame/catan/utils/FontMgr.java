package com.catangame.catan.utils;

import java.util.Map.Entry;
import java.util.HashMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontMgr {
    static private HashMap<Type, FreeTypeFontGenerator> fontGenerator = new HashMap<>();
    static private HashMap<FontSpec, BitmapFont> fontMap = new HashMap<>();

    public enum Type {
        DEFAULT, ROBOTO_LIGHT, OPEN_SANS_REGULAR, Amatic, Quicksand
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
        fontGenerator.put(Type.DEFAULT, new FreeTypeFontGenerator(Gdx.files.local("assets/fonts/Canterbury.ttf")));
        fontGenerator.put(Type.ROBOTO_LIGHT, new FreeTypeFontGenerator(Gdx.files.local("assets/fonts/Roboto-Light.ttf")));
        fontGenerator.put(Type.OPEN_SANS_REGULAR, new FreeTypeFontGenerator(Gdx.files.local("assets/fonts/OpenSans-Regular.ttf")));
        fontGenerator.put(Type.Amatic, new FreeTypeFontGenerator(Gdx.files.local("assets/fonts/Quicksand-Bold.otf")));
        fontGenerator.put(Type.Quicksand, new FreeTypeFontGenerator(Gdx.files.local("assets/fonts/AmaticSC-Regular.ttf")));

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
            parameter.genMipMaps = true;
            parameter.magFilter = TextureFilter.Linear;
            parameter.minFilter = TextureFilter.MipMapLinearLinear;
            fontMap.put(spec, fontGenerator.get(type).generateFont(parameter));
        }
        return fontMap.get(spec);
    }

    public static BitmapFont getFont(int size) {
        return getFont(Type.DEFAULT, size);
    }
}