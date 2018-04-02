package com.catangame.catan.local.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Background extends Widget{
	public long tick = 0;
	
	public Background(Rectangle bounds) {
		super(bounds);
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		super.render(sr, sb);
		tick ++;
	}

	@Override
	public void set_position(Vector2 pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		// TODO Auto-generated method stub
		
	}
}
