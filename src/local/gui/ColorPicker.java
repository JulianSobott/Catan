package local.gui;

import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public class ColorPicker extends Widget {
	private RectangleShape shape;
	private CircleShape slider;
	private Runnable select_event;
	private float hue = (float) Math.random();
	private float saturation = 1.f;
	private float value = 1.f;
	private boolean enabled = true;

	public ColorPicker(FloatRect bounds) {
		super(bounds);

		shape = new RectangleShape(new Vector2f(bounds.width, bounds.height));
		shape.setPosition(bounds.left, bounds.top);

		slider = new CircleShape(bounds.height * 0.3f, 4);
		slider.setOrigin(slider.getRadius(), slider.getRadius());
		slider.setPosition(bounds.left + hue * bounds.width, bounds.top + bounds.height * 0.5f);

		update_color();
	}

	@Override
	public void render(RenderTarget target) {
		target.draw(shape);
		target.draw(slider);
	}

	@Override
	public void do_mouse_click(Vector2f pos) {
		slider.setPosition(pos.x, slider.getPosition().y);
		update_color();

		if (select_event != null && enabled)
			select_event.run();
	}

	// parameters in range 0..1
	private void update_color() {
		hue = (slider.getPosition().x - shape.getGlobalBounds().left) / shape.getGlobalBounds().width;

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

		shape.setFillColor(new Color((int) (r * 255.f), (int) (g * 255.f), (int) (b * 255.f)));
	}

	// setter

	public void set_position(Vector2f pos) {
		update_bounds(new FloatRect(pos.x, pos.y, bounds.width, bounds.height));
		shape.setPosition(pos);
		slider.setPosition(bounds.left + hue * bounds.width, bounds.top + bounds.height * 0.5f);
	}

	public void set_select_callback(Runnable click_event) {
		this.select_event = click_event;
	}

	public void set_color(float hue, float saturation, float value) {
		this.hue = hue;
		this.saturation = saturation;
		this.value = value;
		set_position(shape.getPosition());
		update_color();
	}

	public void set_enabled(boolean enabled) {
		this.enabled = enabled;
		shape.setFillColor(new Color(100, 100, 100, 150));
	}

	public float get_hue() {
		return hue;
	}

	public Color get_color() {
		return shape.getFillColor();
	}
}
