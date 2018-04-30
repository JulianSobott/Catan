package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.catangame.catan.local.Framework;
import com.catangame.catan.utils.Color;
import com.catangame.catan.utils.TextureMgr;

public class LobbyBackground extends Background {
	Texture txtrHorizon;
	Texture txtrSun;
	float sunX = 0;	
	final int sunSpeed = 1;
	int sunXDirection = sunSpeed;
	Sprite sun;
	Sprite horizonBot;
	Sprite horizonTop;
	
	Vector3[][] vertices;
	final int TRIANGLE_WIDTH = 50;
	

	
	float yBack = -1;
	public LobbyBackground(Rectangle bounds) {
		super(bounds);
		sun = new Sprite(TextureMgr.getTexture("sun"));
		sun.flip(true, false);
		horizonBot = new Sprite(TextureMgr.getTexture("horizonBot"));
		horizonBot.flip(false, true);
		horizonTop = new Sprite(TextureMgr.getTexture("horizonTop"));
		horizonTop.flip(false, true);
		createVertices();
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		super.render(sr, sb);
		float y = sin(sunX);
		if(sunXDirection < 0) {
			if(yBack == -1)
				yBack = y+20;
			y = new Float(yBack);
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//Sky
		//Colors
		//0 = top
		//500 = bot
		//x = 0 left ; x = windowsizex right
	
		sb.begin();
		float brightness = (float) (.5 * Math.sin((Math.PI*2)/Framework.windowSize.y/2*(y + Framework.windowSize.y/2)) + .3);
		sb.setColor(brightness, brightness, brightness, 1f);
		sb.draw(horizonTop, 0, 0, Framework.windowSize.x, (Framework.windowSize.y/3)*2);
		sb.setColor(1,0.0f+brightness,0.0f+brightness,1);
		if(sunXDirection > 0)
			sb.draw(sun, sunX-100, y, 200, 200);
		else {
			
		}
		sb.setColor(brightness, brightness, brightness, 1f);
		sb.draw(horizonBot, 0, (Framework.windowSize.y/3)*2, Framework.windowSize.x, (Framework.windowSize.y/3));
		sb.end();
		if(sunX > Framework.windowSize.x) {
			sunXDirection = -sunSpeed;
		}else if(sunX < 0) {
			sunXDirection = sunSpeed;
		}
		sunX += sunXDirection;
		
		//Draw water
		
		//moveWater();
		for(int i = 0; i < vertices.length-1; i++) {
			for(int j = 0; j < vertices[i].length-1; j++) {
				//float w = waterSin();
				sr.begin(ShapeType.Filled);
				float z = vertices[i][j].z;
				Color cLeftTop = new Color(z/255 , z/255, 255/255, 0f);
				Color cLeftBot = new Color(z/250 , z/225, 255/255, 0f);
				Color cRight = new Color(z/250 , z/255, 255/255, 0f);
				sr.setColor(new Color(z/255 , z/255, 255/255, 0f).gdx());
				sr.triangle(vertices[i][j].x, vertices[i][j].y, vertices[i+1][j].x , vertices[i+1][j].y, vertices[i+1][j+1].x, vertices[i+1][j+1].y,
						cLeftTop.gdx(), cLeftBot.gdx(), cRight.gdx());
				//sr.triangle(vertices[i][j].x, vertices[i][j].y, vertices[i][j+1].x , vertices[i][j+1].y, vertices[i+1][j+1].x, vertices[i+1][j+1].y);
				sr.end();
			}
		}
		
	}
	
	float sin(float x) {
		return (float) (-300 * Math.sin((Math.PI*2)/Framework.windowSize.y/2*(x - Framework.windowSize.y/4)) + 300);
	}
	
	float waterSin() {
		return (float) (15*Math.sin((Math.PI*2)/900*this.tick));
	}
	
	private void createVertices() {
		int numX = Gdx.graphics.getWidth() / TRIANGLE_WIDTH + 1;
		int numY = Gdx.graphics.getHeight() / TRIANGLE_WIDTH;
		vertices = new Vector3[numY][numX];
		for(int row = 0; row < vertices.length; row++) {
			for(int col = 0; col < vertices[row].length; col++ ) {
				vertices[row][col] = new Vector3(col*TRIANGLE_WIDTH, (Framework.windowSize.y/3)*2 + row*TRIANGLE_WIDTH, (float) (Math.random()*50));
			}
		}
	}
	
	private void moveWater() {
		for(int row = 1; row < vertices.length-2; row++) {
			for(int col = 1; col < vertices[row].length-2; col++ ) {
				float z = (vertices[row-1][col-1].z + vertices[row-1][col].z + vertices[row-1][col+1].z 
						+ vertices[row][col-1].z  + vertices[row][col+1].z
						+ vertices[row+1][col-1].z + vertices[row+1][col].z + vertices[row+1][col+1].z)/8;
				vertices[row][col] = new Vector3(col*TRIANGLE_WIDTH+waterSin(), (Framework.windowSize.y/3)*2 + row*TRIANGLE_WIDTH+ +waterSin(), vertices[row+ (int)(Math.random()*3)-1][col+(int)(Math.random()*3)-1].z);
				System.out.println((int)(Math.random()*3)-1);
			}
		}
	}
}
