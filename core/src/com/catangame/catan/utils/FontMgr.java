package com.catangame.catan.utils;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontMgr {
    static private HashMap<Type, FreeTypeFontGenerator> fontGenerator = new HashMap<>();
    static private HashMap<FontSpec, BitmapFont> fontMap = new HashMap<>();
    public static boolean finishedLoading = false;

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
    	fontGenerator.put(Type.DEFAULT, new FreeTypeFontGenerator(Gdx.files.internal("fonts/Canterbury.ttf")));
        fontGenerator.put(Type.ROBOTO_LIGHT, new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Light.ttf")));
        fontGenerator.put(Type.OPEN_SANS_REGULAR, new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf")));
        fontGenerator.put(Type.Amatic, new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.otf")));
        fontGenerator.put(Type.Quicksand, new FreeTypeFontGenerator(Gdx.files.internal("fonts/AmaticSC-Regular.ttf")));
        FontMgr.finishedLoading = true;
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
        BitmapFont foundBitmap = null;
        Iterator<Entry<FontSpec, BitmapFont>> fms = fontMap.entrySet().iterator();
        while( fms.hasNext() ) {
            Entry<FontSpec, BitmapFont> tmpBmF = fms.next();
            if( tmpBmF.getKey().type == type && tmpBmF.getKey().size == size ) {
                foundBitmap = tmpBmF.getValue();
                break;
            }
        }
        if (foundBitmap == null) {// create a new font
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = size;
            parameter.flip = true;
            parameter.genMipMaps = true;
            parameter.magFilter = TextureFilter.Linear;
            parameter.minFilter = TextureFilter.MipMapLinearLinear;
            foundBitmap = fontGenerator.get(type).generateFont(parameter);
            fontMap.put(new FontSpec(type, size), foundBitmap);
        }
        return foundBitmap;
    }

    public static BitmapFont getFont(int size) {
        return getFont(Type.DEFAULT, size);
    }
}