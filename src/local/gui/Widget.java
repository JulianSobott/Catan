package local.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public abstract class Widget {
	protected FloatRect bounds;

	protected static Font default_font;
	protected static Color default_text_color;
	protected static Color default_outline_color;
	protected static Color default_outline_highlight_color;
	protected static Color default_fill_color;

	public Widget(FloatRect bounds) {
		update_bounds(bounds);
	}

	protected void update_bounds(FloatRect bounds) {
		this.bounds = bounds;
	}

	public boolean contains_cursor(Vector2f cursor_position) {
		return bounds.contains(cursor_position);
	}

	// default setter

	public static void set_default_font(Font font) {
		default_font = font;
	}

	public static void set_default_text_color(Color color) {
		default_text_color = color;
	}

	public static void set_default_outline_color(Color color) {
		default_outline_color = color;
	}

	public static void set_default_outline_highlight_color(Color color) {
		default_outline_highlight_color = color;
	}
	
	public static void set_default_fill_color(Color color) {
		default_fill_color = color;
	}
	// getter

	public Vector2f get_position() {
		return new Vector2f(bounds.left, bounds.top);
	}

	public Vector2f get_size() {
		return new Vector2f(bounds.width, bounds.height);
	}

	// abstract methods

	public abstract void render(RenderTarget target);

	public abstract void do_mouse_click();
	
	public abstract void set_text(String t);
}
