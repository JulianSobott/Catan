package com.catangame.catan.utils;

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
		String folderPath = "/desktop/assets/res";
		File folder = new File(new File(Paths.get("").toAbsolutePath().toString()).getParent() + folderPath);
		for(final File file : folder.listFiles()) {
			if(!file.isDirectory()) {	
				if(getNameWithoutExtension(file.getName()).length() > 1 && file.getName().substring(file.getName().lastIndexOf('.')).equals(".png") || file.getName().substring(file.getName().lastIndexOf('.')).equals(".jpg")) {
					FileHandle gdxFile = Gdx.files.absolute(file.getAbsolutePath());
					TextureMgr.textures.put(getNameWithoutExtension(file.getName()), new Texture(gdxFile, true));
					TextureMgr.textures.get(getNameWithoutExtension(file.getName())).setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
				}		
			}
		}		
	}
	
	public static Texture getTexture(String texture) {
		return TextureMgr.textures.get(texture.toLowerCase());
	}
	
	private static String getNameWithoutExtension(String name) {
		return name.substring(0, name.lastIndexOf('.'));
	}
}
