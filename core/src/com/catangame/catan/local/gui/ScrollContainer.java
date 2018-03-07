package com.catangame.catan.local.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.local.LocalUI;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.superClasses.UI;

public class ScrollContainer extends Widget{
	
	List<Widget> widgets = new ArrayList<Widget>();
	LocalUI ui;
	int scrolled = 0;
	
	public ScrollContainer(Rectangle bounds) {
		super(bounds);
		this.bounds = bounds;
	}

	public ScrollContainer(LocalUI ui) {
		super(new Rectangle(0,0,0,0));
		this.ui = ui;
	}

	public boolean isMouseInside(int mouseX, int mouseY) {
		if(mouseX >= bounds.x && mouseX <= bounds.x + bounds.width && mouseY >= bounds.y && mouseY <= bounds.y + bounds.height) {
			return true;
		}
		return false;
	}
	
	public void addWidget(Widget w) {
		widgets.add(w);
	}
	
	public void calcBounds() {
		bounds.x = 9000; //Just for calculating
		bounds.y = 90000;
		for(Widget widget : widgets) {
			bounds.height += widget.bounds.height;
			bounds.width = widget.bounds.width;
			if(widget.bounds.x < bounds.x) {
				bounds.x = widget.bounds.x;
			}
			if(widget.bounds.y < bounds.y) {
				bounds.y = widget.bounds.y;
			}
		}
	}

	@Override
	public void set_position(Vector2 pos) {
		bounds.x = pos.x;
		bounds.y = pos.y;
	}
	public void scrolled(int amount) {
		this.scrolled += amount;
		for(Widget widget : widgets) {
			widget.set_position(new Vector2(widget.bounds.x, widget.bounds.y + scrolled * 100 + 10));
		}
		
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		//TODO evaluate which widget should be rendered
		for(Widget widget : widgets) {
			widget.render(sr, sb);
		}
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		
	}
}
