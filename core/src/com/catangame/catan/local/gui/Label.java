package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Label extends Widget {
	private static final int DEFAULT_MENU_TEXTFIELD_OUTLINE_THICKNESS = 3;
	private static final int DEFAULT_MENU_TEXTFIELD_LEFT_SPACING = 5;

	private Color backColor;
	private Color outlineColor;
	private float outlineThickness = 0;
	private String text;
	private BitmapFont font;
	private Color textColor;
	private Vector2 textPosition;

	private boolean visible = true;

	public Label(String text, Rectangle bounds) {
		super(bounds);
		
		this.text = text;
		backColor = new Color(0);
		outlineColor = new Color(0);
		textColor = default_text_color;
		font = default_font;
		set_position(new Vector2(bounds.x, bounds.y));
	}

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		if (this.visible) {
			sr.begin(ShapeType.Filled);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			sr.setColor(backColor);
			sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			sr.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);

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
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
	}

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));

		GlyphLayout layout = new GlyphLayout(font, text);
		textPosition = new Vector2(bounds.x, bounds.y + (bounds.height - layout.height) * 0.5f);
	}

	public void set_text(String text) {
		this.text = text;
	}

	public void set_text_size(int character_size) {//TODO l3
	}
	public void set_text_color(Color c) {
		textColor = c;
	}
	public void set_fill_color(Color color) {
		backColor = color;
	}

	public void set_visible(boolean visible) {
		this.visible = visible;
	}

	public void setOutlineColor(Color color) {
		outlineColor = color;
	}

	// adjusts width automatically based on text size
	public void adjustWidth(float padding) {//TODO l3
		/*update_bounds(
				new Rectangle(bounds.left, bounds.top, text.getGlobalBounds().width + padding * 2, bounds.height));
		shape.setSize(new Vector2(bounds.width, shape.getSize().y));
		this.text.setPosition(bounds.left, bounds.top + bounds.height * 0.5f);*/
	}
}
