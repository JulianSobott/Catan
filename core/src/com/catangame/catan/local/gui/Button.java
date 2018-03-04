package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.catangame.catan.utils.BoxShadow;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Button extends Widget {
	private Color backColor;
	private Color disabledBackColor;
	private Color outlineColor;
	private float outlineThickness = 2;
	private String text;
	private BitmapFont font;
	private Color textColor;
	private Vector2 textPosition;
	private Runnable click_event;
	private boolean enabled = true;
	private boolean selected = false; //For the trading window
	private BoxShadow bs = null;
	
	private Texture texture = null;

	public Button(String text, Rectangle bounds) {
		super(bounds);
		
		this.text = text;
		backColor = default_back_color;
		disabledBackColor = default_disabled_background_color;
		outlineColor = default_outline_color;
		textColor = default_text_color;
		font = default_font;
		set_position(new Vector2(bounds.x, bounds.y));
	}
	public Button(String text, Rectangle bounds, Texture texture) {
		super(bounds);
		
		this.text = text;
		backColor = default_back_color;
		disabledBackColor = default_disabled_background_color;
		outlineColor = default_outline_color;
		textColor = default_text_color;
		font = default_font;
		set_position(new Vector2(bounds.x, bounds.y));
		this.texture = texture;
	}

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		if(bs != null) {
			drawBoxShadow(sr, bs);
			sr.end();
		}
		if (outlineThickness > 0) {
			sr.begin(ShapeType.Line);
			sr.setColor(outlineColor.gdx());
			Gdx.gl.glLineWidth(outlineThickness);
			sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			sr.end();
		}
		if(texture == null) {
			sr.begin(ShapeType.Filled);
			if (enabled)
				sr.setColor(backColor.gdx());
			else
				sr.setColor(disabledBackColor.gdx());
			sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			sr.end();
		}else {
			Sprite sprite = new Sprite(texture);
			sb.begin();
			sb.draw(sprite, bounds.x, bounds.y, bounds.width, bounds.height);
			sb.end();
		}
	
		sb.begin();
		font.setColor(textColor.gdx());
		font.draw(sb, text, textPosition.x, textPosition.y);
		sb.end();
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		if (click_event != null && enabled)
			click_event.run();
	}

	// setter

	public void set_font(BitmapFont bitmapFont) {
		font = bitmapFont;
	}

	public void set_text(String text) {
		this.text = text;
	}

	public String get_text() {
		return text;
	}

	public void set_text_color(Color color) {
		textColor = color;
	}

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));

		GlyphLayout layout = new GlyphLayout(font, text);
		textPosition = new Vector2(bounds.x + (bounds.width - layout.width) * 0.5f,
				bounds.y + (bounds.height - layout.height) * 0.5f);
	}

	public void set_click_callback(Runnable click_event) {
		this.click_event = click_event;
	}

	public void set_fill_color(Color color) {
		backColor = color;
	}

	public Color getFillColor() {
		return textColor;
	}

	public void set_enabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void set_selected(boolean selected) {
		this.selected = selected;
	}

	public boolean get_selected() {
		return this.selected;
	}

	public void set_outline(Color color, float thickness) {
		outlineColor = color;
		outlineThickness = thickness;
	}

	// adjusts width automatically based on text size
	public void adjustWidth(float padding) {
		GlyphLayout layout = new GlyphLayout(font, text);
		update_bounds(new Rectangle(bounds.x, bounds.y, layout.width + padding * 2, bounds.height));
		set_position(new Vector2(bounds.x, bounds.y));
	}
	
	public void addBoxShadow(BoxShadow bs) {
		this.bs = bs;	
	}
	
	private void drawBoxShadow(ShapeRenderer sr, BoxShadow bs) {
		sr.begin(ShapeType.Filled);
		sr.setColor(bs.color.gdx());
		sr.rect(bounds.x - bs.spread + bs.h_offset, bounds.y - bs.spread + bs.v_offset, bounds.width+ bs.spread*2+ bs.h_offset, bounds.height+bs.spread*2 + bs.v_offset);
		sr.end();
		if(bs.boxshadow != null) {
			drawBoxShadow(sr, bs.boxshadow);
		}

	}
	
}
