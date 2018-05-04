package com.catangame.catan.local.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.utils.Color;
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
	private boolean centeredText = false;
	
	private List<Animation> animations = new ArrayList<>();
	private long animationStart;
	private boolean visibleText = true;

	public Label(String text, Rectangle bounds) {
		super(bounds);
		
		this.text = text;
		backColor = Color.TRANSPARENT;
		outlineColor = Color.TRANSPARENT;
		textColor = default_text_color;
		font = default_font;
		set_position(new Vector2(bounds.x, bounds.y));
	}

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		if (this.visible) {
			if(this.texture != null) {
				super.render(sr, sb);
			}else {
				sr.begin(ShapeType.Filled);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				sr.setColor(backColor.gdx());
				sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
				sr.end();
				Gdx.gl.glDisable(GL20.GL_BLEND);
			}
			

			if (outlineThickness > 0) {
				sr.begin(ShapeType.Line);
				sr.setColor(outlineColor.gdx());
				Gdx.gl.glLineWidth(outlineThickness);
				sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
				sr.end();
			}
			if(animations.contains(Animation.TEXT_BLINK)) {
				if(visibleText) {
					if(System.currentTimeMillis() - this.animationStart > 1000) {
						visibleText = !visibleText;
						this.animationStart = System.currentTimeMillis();
					}
				}else {
					if(System.currentTimeMillis() - this.animationStart > 500) {
						visibleText = !visibleText;
						this.animationStart = System.currentTimeMillis();
					}
				}
				
			}
			if(visibleText) {
				sb.begin();
				font.setColor(textColor.gdx());
				font.draw(sb, text, textPosition.x, textPosition.y);
				sb.end();
			}	
		}
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
	}

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));

		GlyphLayout layout = new GlyphLayout(font, text);
		textPosition = new Vector2(bounds.x + 5, bounds.y + (bounds.height - layout.height) * 0.5f);
		if(this.centeredText)
			textPosition = new Vector2(bounds.x + bounds.width/2 - layout.width/2, textPosition.y);
	}

	public void set_font(BitmapFont bitmapFont) {
		font = bitmapFont;
	}

	public void set_text(String text) {
		this.text = text;
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

	public void animate(Animation animation) {
		this.animations.add(animation);
		this.animationStart = System.currentTimeMillis();
	}
	
	public void stopAnimating() {
		this.animations = new ArrayList<>();
	}
	
	public void centerText() {
		this.centeredText = true;
		this.set_position(new Vector2(this.bounds.x, this.bounds.y));
	}
}
