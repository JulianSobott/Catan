package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		sr.begin(ShapeType.Filled);
		if (enabled)
			sr.setColor(backColor);
		else
			sr.setColor(disabledBackColor);
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		sr.end();

		if (outlineThickness > 0) {
			sr.begin(ShapeType.Line);
			sr.setColor(outlineColor);
			Gdx.gl.glLineWidth(outlineThickness);
			sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			sr.end();
		}

		sb.begin();
		font.setColor(textColor);
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
	public void set_position(Vector2 pos) {//TODO l3
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
	public void adjustWidth(float padding) {// TODO l3
		/*update_bounds(new Rectangle(bounds.left, bounds.top, text.getGlobalBounds().width + padding * 2, bounds.height));
		shape.setSize(new Vector2(bounds.width, shape.getSize().y));
		this.text.setPosition(bounds.left + bounds.width * 0.5f, bounds.top + bounds.height * 0.5f);*/
	}

	public void set_text_position(float f, float i) {// TODO l3
	}

	public void set_text_size(int i) {// TODO l3
	}
}
