package com.catangame.catan.local.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.local.LocalUI;

public class Container extends Widget{
	public List<Widget> widgets = new ArrayList<Widget>();
	LocalUI ui;
	int scrolled = 0;
	private Rectangle maxBounds;
	private boolean scrollable = true;
	private boolean finalScrollable = true;
	public boolean visible = false;
	

	public Container(LocalUI ui, Rectangle rectangle) {
		super(new Rectangle(0,0,0,0));
		this.ui = ui;
		this.maxBounds = rectangle;
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
		if(bounds.height + this.bounds.y < this.maxBounds.height - this.maxBounds.y) {
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
			scrollable = false;
			for(Widget widget : widgets) {
				widget.set_position(new Vector2(widget.bounds.x, widget.bounds.y - amount * 50));
				if(widget.bounds.y >= this.maxBounds.y && widget.bounds.y + widget.bounds.height <= this.maxBounds.y + this.maxBounds.height) {
					scrollable = true;
				}
				
			}
			if(widgets.get(widgets.size()-1).bounds.y + widgets.get(widgets.size()-1).bounds.height*3 < this.maxBounds.height ||
					widgets.get(0).bounds.y - widgets.get(0).bounds.height > this.maxBounds.y) {
				scrollable = false;
			}
			if(!scrollable) {
				scrolled(amount* (-1));
			}

		}	
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		//TODO evaluate which widget should be rendered
		if(visible) {
			for(Widget widget : widgets) {
				if(widget.bounds.y + widget.bounds.height/2 > this.maxBounds.y && widget.bounds.y - widget.bounds.height/2 < this.maxBounds.y + this.maxBounds.height) {
					widget.render(sr, sb);
				}	
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
