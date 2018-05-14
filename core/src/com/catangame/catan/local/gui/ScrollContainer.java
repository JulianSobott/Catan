package com.catangame.catan.local.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.local.LocalUI;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.superClasses.UI;
import com.catangame.catan.utils.Color;

public class ScrollContainer extends Widget{
	
	List<Widget> widgets = new ArrayList<Widget>();
	LocalUI ui;
	int scrolled = 0;
	private Rectangle maxBounds;
	private boolean scrollable = true;
	private boolean finalScrollable = true;
	

	public ScrollContainer(LocalUI ui, Rectangle rectangle) {
		super(new Rectangle(0,0,0,0));
		this.ui = ui;
		this.maxBounds = rectangle;
	}


	public boolean isMouseInside(int mouseX, int mouseY) {
		if(mouseX >= bounds.x && mouseX <= bounds.x + bounds.width && mouseY >= bounds.y && mouseY <= bounds.y + bounds.height) {
			if(mouseX >= maxBounds.x && mouseX <= maxBounds.x + maxBounds.width && mouseY >= maxBounds.y && mouseY <= maxBounds.y + maxBounds.height)
				return true;
		}
		return false;
	}
	
	public void addWidget(Widget w) {
		widgets.add(w);
		calcBounds();
	}
	
	public void calcBounds() {
		bounds.x = 9000; //Just for calculating
		bounds.y = 90000;
		bounds.width = 0;
		bounds.height = 0;
		for(Widget widget : widgets) {
			bounds.height += widget.bounds.height;
			if(bounds.width < widget.bounds.width) {
				bounds.width = widget.bounds.width;
			}
			if(widget.bounds.x < bounds.x) {
				bounds.x = widget.bounds.x;
			}
			if(widget.bounds.y < bounds.y) {
				bounds.y = widget.bounds.y;
			}
		}
		if(bounds.height < this.maxBounds.height) {
			finalScrollable = false;
		}else {
			finalScrollable = true;
		}
	}

	@Override
	public void set_position(Vector2 pos) {
		bounds.x = pos.x;
		bounds.y = pos.y;
	}
	public void scrolled(int amount) {
		if(finalScrollable) {
			this.scrolled += amount;
			scrollable = true;
			if(amount > 0) { //Scroll down
				if(widgets.get(widgets.size()-1).bounds.y > widgets.get(0).bounds.y) { //last Widget is at the bottom
					if(widgets.get(widgets.size()-1).bounds.y + widgets.get(widgets.size()-1).bounds.height < this.maxBounds.height) {
						scrollable = false;
					}
				}else { //first Widget is at the bottom
					if(widgets.get(0).bounds.y + widgets.get(0).bounds.height <= this.maxBounds.height + this.maxBounds.y) {
						scrollable = false;
					}
				}
			}else { // scroll up
				if(widgets.get(widgets.size()-1).bounds.y > widgets.get(0).bounds.y) { //last Widget is at the bottom
					if(widgets.get(0).bounds.y >= this.maxBounds.y) {
						scrollable = false;
					}
				}else { //first Widget is at the bottom
					if(widgets.get(widgets.size()-1).bounds.y >= this.maxBounds.y) {
						scrollable = false;
					}
				}
			}
			if(scrollable) {
				for(Widget widget : widgets) {
					widget.set_position(new Vector2(widget.bounds.x, widget.bounds.y - amount * 50));					
				}
			}
		}	
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		//TODO evaluate which widget should be rendered
		for(Widget widget : widgets) {
			if(widget.bounds.y > this.maxBounds.y && widget.bounds.y + widget.bounds.height < this.maxBounds.y + this.maxBounds.height) {
				widget.render(sr, sb);
			}
		}
	}

	@Override
	public void do_mouse_click(Vector2 pos) {
		for(Widget widget : widgets) {
			if(widget.contains_cursor(pos)) {
				widget.do_mouse_click(pos);
			}	
		}
	}
}
