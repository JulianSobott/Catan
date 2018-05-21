package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ColorPicker extends Widget {
	private Color selectColor;
	private Color color;// selected color
	private Color outlineColor;
	private float outlineThickness = 2;
	private Runnable select_event;
	private float hue = (float) Math.random();
	private float saturation = 1.f;
	private float value = 1.f;
	private boolean enabled = true;

	public ColorPicker(Rectangle bounds) {
		super(bounds);

		selectColor = default_checkbox_color;
		outlineColor = default_outline_color;

		update_color();
	}

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		sr.begin(ShapeType.Filled);
		sr.setColor(color.gdx());
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		sr.end();
		
		if (outlineThickness > 0) {
			sr.begin(ShapeType.Line);
			sr.setColor(outlineColor.gdx());
			Gdx.gl.glLineWidth(outlineThickness);
			sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			sr.end();
		}

		sr.begin(ShapeType.Filled);
		sr.setColor(selectColor.gdx());
		sr.circle(bounds.x+hue*bounds.width, bounds.y+bounds.height*0.5f, bounds.height*0.3f, 4);
		sr.end();
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		hue = (pos.x - bounds.x) / bounds.width;

		update_color();

		if (select_event != null && enabled)
			select_event.run();
	}

	// parameters in range 0..1
	private void update_color() {
		/*https://www.cs.rit.edu/~ncs/color/t_convert.html*/
		float h = hue * 360.f, s = saturation, v = value;
		float r, g, b;
		int i;
		float f, p, q, t;
		if (s == 0) {
			// achromatic (grey)
			r = g = b = v;
			return;
		}
		h /= 60; // sector 0 to 5
		i = (int) Math.floor(h);
		f = h - i; // factorial part of h
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));
		switch (i) {
		case 0:
			r = v;
			g = t;
			b = p;
			break;
		case 1:
			r = q;
			g = v;
			b = p;
			break;
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		default: // case 5:
			r = v;
			g = p;
			b = q;
			break;
		}

		color = new Color(r, g, b, 1.f);
	}

	// setter

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));
	}

	public void set_select_callback(Runnable click_event) {
		this.select_event = click_event;
	}

	public void set_color(float hue, float saturation, float value) {
		this.hue = hue;
		this.saturation = saturation;
		this.value = value;
		update_color();
	}

	public void set_outline(Color color, float thickness) {
		outlineColor = color;
		outlineThickness = thickness;
	}

	public void set_enabled(boolean enabled) {
		this.enabled = enabled;
	}

	public float get_hue() {
		return hue;
	}

	public Color get_color() {
		return color;
	}
}
