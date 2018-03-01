package com.catangame.catan.local.gui;

import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Checkbox extends Widget {
	private Color backColor;
	private Rectangle inner;
	private Color innerColor;
	private Runnable click_event;
	private boolean selected = true;

	public Checkbox(Rectangle bounds) {
		super(bounds);

		set_position(new Vector2(bounds.x, bounds.y));
		backColor = default_back_color;
		innerColor = default_checkbox_color;
	}

	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		sr.begin(ShapeType.Filled);
		sr.setColor(backColor.gdx());
		sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		sr.end();

		if (selected) {
			sr.begin(ShapeType.Filled);
			sr.setColor(innerColor.gdx());
			sr.rect(inner.x, inner.y, inner.width, inner.height);
			sr.end();
		}
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		selected = !selected;
		if (click_event != null)
			click_event.run();
	}

	// setter

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));

		float factor = 0.6f;
		inner = new Rectangle(bounds.x + (bounds.width * (1 - factor)) / 2.f,
				bounds.y + (bounds.height * (1 - factor)) / 2.f, bounds.width * factor, bounds.height * factor);
	}

	public void set_click_callback(Runnable click_event) {
		this.click_event = click_event;
	}

	public void set_fill_color(Color color) {
		backColor = color;
	}

	public Color getFillColor() {
		return backColor;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean enable) {
		selected = enable;
	}
}
