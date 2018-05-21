package com.catangame.catan.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class SoundMgr {
	private static Map<String, Music> music = new HashMap<String, Music>();
	private static Music currMusic;
	public static void init() {
		//Load all resources 
		String folderPath = "assets/sounds/";
		File folder = new File(Gdx.files.getLocalStoragePath() + folderPath);
		FileHandle[] sounds = Gdx.files.local(folderPath).list();
		
		for(final FileHandle sound : sounds) {
			music.put(getNameWithoutExtension(sound.name()), Gdx.audio.newMusic(sound));
		}
	}
	
	public static Music getSound(String name) {
		return music.get(name);
	}
	
	public static void shuffleMusic() {
		Thread musicThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				while(true) {
					if(currMusic == null) {
						Random generator = new Random();
						Object[] values =  music.values().toArray();
						currMusic = (Music) values[generator.nextInt(values.length)];
						currMusic.play();
					}else {
						if(!currMusic.isPlaying()) {
							Random generator = new Random();
							Object[] values = music.values().toArray();
							currMusic = (Music) values[generator.nextInt(values.length)];
							currMusic.play();
						}
					}
					
				}
			}
		});
		musicThread.start();
	}
	
	private static String getNameWithoutExtension(String name) {
		return name.substring(0, name.lastIndexOf('.'));
	}
}
