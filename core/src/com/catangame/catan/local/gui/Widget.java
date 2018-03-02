package com.catangame.catan.local.gui;

import com.badlogic.gdx.graphics.Camera;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

	public Widget(Rectangle bounds) {
		update_bounds(bounds);
	}

	protected void update_bounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public boolean contains_cursor(Vector2 cursor_position) {
		return bounds.contains(cursor_position);
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

	// abstract methods

	public abstract void render(ShapeRenderer sr, SpriteBatch sb);

	public abstract void do_mouse_click(Vector2 pos);
}
