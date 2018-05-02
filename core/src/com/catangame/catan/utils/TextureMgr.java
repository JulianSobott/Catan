package com.catangame.catan.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.catangame.catan.data.SavedGame;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class TextureMgr {
	private static HashMap<String, Texture> textures = new HashMap<>();
	
	public static void init() {
		//Load all resources 
		String folderPath = "assets/res/";
		File folder = new File(Gdx.files.getLocalStoragePath() + folderPath);
		FileHandle[] images = Gdx.files.local("assets/res/").list();
		for(final FileHandle img : images) {
			TextureMgr.textures.put(getNameWithoutExtension(img.name()), new Texture(img, true));
			TextureMgr.textures.get(getNameWithoutExtension(img.name())).setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		}		
	}
	
	public static Texture getTexture(String texture) {
		return TextureMgr.textures.get(texture.toLowerCase());
	}
	
	private static String getNameWithoutExtension(String name) {
		return name.substring(0, name.lastIndexOf('.'));
	}
}
