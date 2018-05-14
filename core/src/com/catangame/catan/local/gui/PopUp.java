package com.catangame.catan.local.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.data.Language;

public class PopUp extends Widget {

	private Button btnClose;
	private BitmapFont font;
	private String text;
	private Vector2 textPosition;
	private boolean visible = true;
	
	private List<Widget> widgets = new ArrayList<Widget>();
	private Color fontColor;
	
	
	public PopUp(String text, Rectangle bounds) {
		super(bounds);
		btnClose = new Button("X", new Rectangle(bounds.x + bounds.width - 35, bounds.y + 5, 30, 30));
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
				visible = false;
			}
		});
		font = default_font;
		this.text = text;
		set_position(new Vector2(bounds.x, bounds.y));
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		super.render(sr, sb);
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		if(visible) {
			sr.begin(ShapeType.Filled);
			sr.setColor(new Color(158/255, 31/255, 31/255, .95f));
			sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			sr.end();
			btnClose.render(sr, sb);
			sb.begin();
			sb.setColor(fontColor);
			font.draw(sb, text, textPosition.x, textPosition.y);
			sb.end();
			
			for(Widget w : widgets) {
				w.render(sr, sb);
			}
		}	
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		if(btnClose.contains_cursor(pos)) {
			btnClose.do_mouse_click(pos);
		}
		for(Widget w : widgets) {
			if(w.contains_cursor(pos)) {
				w.do_mouse_click(pos);
			}
		}
	}

	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));

		GlyphLayout layout = new GlyphLayout(font, text);
		textPosition = new Vector2(bounds.x + (bounds.width - layout.width) * 0.5f,
				bounds.y + (bounds.height - layout.height) * 0.5f);
	}
	
	public void set_font(BitmapFont bitmapFont) {
		font = bitmapFont;
	}

	public void addWidget(Widget widget) {
		widget.set_position(new Vector2(widget.get_position().x + this.bounds.x, widget.get_position().y + this.bounds.y));
		widgets.add(widget);
	}

	public void setFontColor(Color color) {
		this.fontColor = color;
	}

}
