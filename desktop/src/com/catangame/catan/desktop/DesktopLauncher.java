package com.catangame.catan.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.local.Framework;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1200;
		config.height = 800;
		config.addIcon("assets/res/icon.png", FileType.Internal);
		config.samples = 4;
		new LwjglApplication(new Framework(new Vector2(config.width, config.height)), config);
	}
}
