package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.catangame.catan.local.Framework;
import com.catangame.catan.utils.TextureMgr;

public class LobbyBackground extends Background {
	Texture txtrHorizon;
	Texture txtrSun;
	float sunX = 0;
	int sunXDirection = 1;
	Sprite sun;
	Sprite horizonBot;
	Sprite horizonTop;
	public LobbyBackground(Rectangle bounds) {
		super(bounds);
		sun = new Sprite(TextureMgr.getTexture("sun"));
		sun.flip(true, false);
		horizonBot = new Sprite(TextureMgr.getTexture("horizonBot"));
		horizonBot.flip(false, true);
		horizonTop = new Sprite(TextureMgr.getTexture("horizonTop"));
		horizonTop.flip(false, true);
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		
		
		float y = sin(sunX);
		if(sunXDirection < 0) {
			y = Framework.windowSize.y;
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sr.begin(ShapeType.Filled);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//Sky
		//Colors
		//0 = top
		//500 = bot
		//x = 0 left ; x = windowsizex right
		
		Color leftTop = new Color(10, 20, 200, 250);
		Color leftBot = new Color(0, 0, 255, 250);
		Color rightTop = new Color(0, 0, 255, 2500);
		Color rightBot = new Color(0, 10, 250, 250);
		sr.rect(0, 0, Framework.windowSize.x, Framework.windowSize.y, leftTop, rightTop, rightBot, leftBot);
		sr.end();
		sb.begin();	
		sb.draw(horizonTop, 0, 0, Framework.windowSize.x, (Framework.windowSize.y/3)*2);
		sb.draw(sun, sunX-100, y, 200, 200);
		sb.draw(horizonBot, 0, (Framework.windowSize.y/3)*2, Framework.windowSize.x, (Framework.windowSize.y/3));
		sb.end();
		if(sunX > Framework.windowSize.x) {
			sunXDirection = -2;
		}else if(sunX < 0) {
			sunXDirection = 2;
		}
		sunX += sunXDirection;
	}
	
	float sin(float x) {
		return (float) (-300 * Math.sin((Math.PI*2)/Framework.windowSize.y/2*(x - Framework.windowSize.y/4)) + 300);
	}
}
