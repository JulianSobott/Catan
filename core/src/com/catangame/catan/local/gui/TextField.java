package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TextField extends Widget {
	private static final int DEFAULT_MENU_TEXTFIELD_OUTLINE_THICKNESS = 3;
	private static final int DEFAULT_MENU_TEXTFIELD_LEFT_SPACING = 5;

	private Color backColor;
	private Color outlineColor;
	private float outlineThickness = 0;
	private Color disabledOutlineColor;
	private String text;
	private BitmapFont font;
	private Color textColor;
	private Vector2 textPosition;
	private boolean is_active = false;
	private Runnable input_event;
	private Runnable enterCallBack;

	public TextField(Rectangle bounds) {
		super(bounds);

		this.text = "";
		backColor = default_back_color;
		outlineColor = default_outline_color;
		disabledOutlineColor = default_disabled_outline_color;
		textColor = default_text_color;
		font = default_font;
		set_position(new Vector2(bounds.x, bounds.y));
	}

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		sr.begin(ShapeType.Filled);
		sr.setColor(backColor.gdx());
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		sr.end();

		sr.begin(ShapeType.Line);
		if (is_active)
			sr.setColor(outlineColor.gdx());
		else
			sr.setColor(disabledOutlineColor.gdx());
		Gdx.gl.glLineWidth(outlineThickness);
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		sr.end();

		sb.begin();
		font.setColor(textColor.gdx());
		font.draw(sb, text, textPosition.x, textPosition.y);
		sb.end();
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		is_active = true;
		outlineColor = default_outline_highlight_color;
		Gdx.input.setOnscreenKeyboardVisible(true);
	}

	public void deactivate() {
		is_active = false;
	}

	public void text_input(char character) {
		if(character == '\n' || character =='\r') {
			if(this.enterCallBack != null) {
				this.enterCallBack.run();
			}	
		}
		if (character != 0 && character != 0x7F && character != '\n' && character != '\r' && character != '\b' && character != '\t') {
			text += character;
			if (input_event != null)
				input_event.run();
		}
	}

	// returns true if the key was handled
	public boolean special_input(int key) {
		if (key == Keys.BACKSPACE) {
			if (!text.isEmpty()) {
				text = text.substring(0, text.length() - 1);
				if (input_event != null)
					input_event.run();
			}
			return true;
		} else if (key == Keys.FORWARD_DEL) {
			if (!text.isEmpty()) {
				text = "";
				if (input_event != null)
					input_event.run();
			}
			return true;
		}
		return false;
	}

	// setter

	public void set_font(BitmapFont bitmapFont) {
		font = bitmapFont;
		textPosition = new Vector2(bounds.x+5, bounds.y + (int)(bounds.height - font.getLineHeight()*.7) * 0.5f);
	}

	public void set_text(String string) {
		text = string;
		if (input_event != null)
			input_event.run();
	}

	public String get_text() {
		return text;
	}

	public void set_text_color(Color color) {
		textColor = color;
	}

	public void set_outline(Color color, float thickness) {
		outlineColor = color;
		outlineThickness = thickness;
	}

	public void setDisabledoutline(Color color) {
		disabledOutlineColor = color;
	}

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));

		textPosition = new Vector2(bounds.x+5, bounds.y + (bounds.height - font.getLineHeight()) * 0.5f);
	}
	
	public void set_input_callback(Runnable input_event) {
		this.input_event = input_event;
	}
	
	public void setEnterCallback(Runnable r) {
		this.enterCallBack = r;
	}
}
