package local.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Checkbox extends Widget {
	private RectangleShape shape;
	private RectangleShape inner;
	private Runnable click_event;
	private boolean selected = true;

	public Checkbox(FloatRect bounds) {
		super(bounds);

		shape = new RectangleShape(new Vector2f(bounds.width, bounds.height));
		shape.setPosition(bounds.left, bounds.top);

		inner = new RectangleShape(new Vector2f(bounds.width * 0.6f, bounds.height * 0.6f));
		inner.setOrigin(inner.getSize().x * .5f, inner.getSize().y * .5f);
		inner.setPosition(bounds.left + bounds.width * .5f, bounds.top + bounds.height * .5f);
		inner.setFillColor(Color.BLACK);
	}

	@Override
	public void render(RenderTarget target) {
		target.draw(shape);
		if (selected)
			target.draw(inner);
	}

	@Override
	public void do_mouse_click(Vector2f pos) {
		selected = !selected;
		if (click_event != null)
			click_event.run();
	}

	// setter

	@Override
	public void set_position(Vector2f pos) {
		update_bounds(new FloatRect(pos.x, pos.y, bounds.width, bounds.height));
		shape.setPosition(pos);
		inner.setPosition(bounds.left + bounds.width * .5f, bounds.top + bounds.height * .5f);
	}

	public void set_click_callback(Runnable click_event) {
		this.click_event = click_event;
	}

	public void set_fill_color(Color color) {
		shape.setFillColor(color);
	}

	public Color getFillColor() {
		return shape.getFillColor();
	}

	public void set_outline_color(Color c) {
		this.shape.setOutlineThickness(2);
		this.shape.setOutlineColor(c);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean enable) {
		selected = enable;
	}
}
