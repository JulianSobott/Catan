package com.catangame.catan.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Textures {
	public static Texture btnLoadGame;
	
	public static void init() {
		btnLoadGame =  new Texture(Gdx.files.local("assets/res/ButtonTest.png"), true);
	}
	 
}
