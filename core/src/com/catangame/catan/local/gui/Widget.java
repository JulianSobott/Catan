package com.catangame.catan.local.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.catangame.catan.utils.BoxShadow;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Widget {
	protected Rectangle bounds;

	protected static BitmapFont default_font;
	protected static Color default_back_color;
	protected static Color default_text_color;
	protected static Color default_outline_color;
	protected static Color default_outline_highlight_color;
	protected static Color default_disabled_outline_color;
	protected static Color default_disabled_background_color;
	protected static Color default_checkbox_color;
	protected List<Runnable> hoverEnter = new ArrayList<Runnable>();
	protected List<Runnable> hoverLeave = new ArrayList<Runnable>();
	public boolean hasHover = false;
	public boolean hovered = false;
	protected boolean isVisible = true;
	
	protected Texture texture = null;
	
	public Widget(Rectangle bounds) {
		update_bounds(bounds);
	}

	protected void update_bounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public boolean contains_cursor(Vector2 cursor_position) {
		return bounds.contains(cursor_position);
	}
	
	public void addHover(Runnable enter, Runnable leave) {
		this.hoverEnter.add(enter);
		this.hoverLeave.add(leave);
		this.hasHover = true;
	}
	
	public void enter() {
		this.hovered = true;
		for(Runnable r : this.hoverEnter) {
			r.run();
		}
	}
	public void leave() {
		this.hovered = false;
		for(Runnable r : this.hoverLeave) {
			r.run();
		}
	}
	// default setter

	public static void set_default_font(BitmapFont BitmapFont) {
		default_font = BitmapFont;
	}

	public static void set_default_back_color(Color color) {
		default_back_color = color;
	}

	public static void set_default_text_color(Color color) {
		default_text_color = color;
	}

	public static void set_default_outline_color(Color color) {
		default_outline_color = color;
	}

	public static void set_default_outline_highlight_color(Color color) {
		default_outline_highlight_color = color;
	}

	public static void set_default_disabled_outline_color(Color color) {
		default_disabled_outline_color = color;
	}

	public static void set_default_disabled_background_color(Color color) {
		default_disabled_background_color = color;
	}

	public static void set_default_checkbox_color(Color color) {
		default_checkbox_color = color;
	}

	public abstract void set_position(Vector2 pos);

	// getter

	public Vector2 get_position() {
		return new Vector2(bounds.x, bounds.y);
	}

	public Vector2 get_size() {
		return new Vector2(bounds.width, bounds.height);
	}
	public static Color getDefaultFillColor() {
		return default_back_color;
	}
	public static Color getDefaultOutlineColor() {
		return default_outline_color;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}
	
	public boolean isVisible() {
		return this.isVisible;
	}
	// abstract methods

	public void render(ShapeRenderer sr, SpriteBatch sb) {
		if(isVisible) {
			if(this.texture != null) {
				Sprite sprite = new Sprite(texture);
				sprite.flip(false, true);
				sb.begin();
				sb.setColor(sprite.getColor());
				sb.draw(sprite, bounds.x, bounds.y, bounds.width, bounds.height);
				sb.end();
			}
		}	
	}

	public abstract void do_mouse_click(Vector2 pos);
	
	
	//Hover Effects
	public void addHoverEffect1() {
		if(this instanceof Button) {
			((Button)Widget.this).addHover(new Runnable() {	
				@Override
				public void run() {
					((Button)Widget.this).addBoxShadow(new BoxShadow(new Color(126, 71, 20, 120), 0, 2, 2 , new BoxShadow(new Color(0, 0, 20, 90), 1, 0, 0 )));
				}
			}, new Runnable() {
				@Override
				public void run() {
					((Button)Widget.this).addBoxShadow(new BoxShadow(new Color(126, 71, 20, 90), 0, 2, 2));
				}
			});
		}	
	}
}
