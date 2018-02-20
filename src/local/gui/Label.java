package local.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Label extends Widget {
	private static final int DEFAULT_MENU_TEXTFIELD_OUTLINE_THICKNESS = 3;
	private static final int DEFAULT_MENU_TEXTFIELD_LEFT_SPACING = 5;

	private RectangleShape shape;
	private Text text;

	private boolean visible = true;

	public Label(String text, FloatRect bounds) {
		super(bounds);
		shape = new RectangleShape(new Vector2f(bounds.width, bounds.height));
		shape.setPosition(bounds.left, bounds.top);
		shape.setOutlineColor(default_outline_color);
		shape.setOutlineThickness(DEFAULT_MENU_TEXTFIELD_OUTLINE_THICKNESS);
		shape.setFillColor(default_fill_color);
		this.text = new Text("", default_font);
		this.text.setString(text.isEmpty() ? "XOW" : text);// needed to properly calculate bounds
		this.text.setOrigin(-DEFAULT_MENU_TEXTFIELD_LEFT_SPACING, this.text.getGlobalBounds().height * 0.5f);
		this.text.setString(text);
		this.text.setPosition(bounds.left, bounds.top + bounds.height * 0.5f);
		this.text.setColor(default_text_color);
	}

	@Override
	public void render(RenderTarget target) {
		if (this.visible) {
			target.draw(shape);
			target.draw(text);
		}
	}

	@Override
	public void do_mouse_click(Vector2f pos) {
	}

	public void set_position(Vector2f pos) {
		update_bounds(new FloatRect(pos.x, pos.y, bounds.width, bounds.height));
		shape.setPosition(pos);
		text.setPosition(bounds.left, bounds.top + bounds.height * 0.5f);
	}

	public void set_text(String text) {
		this.text.setString(text);
	}

	public void set_text_size(int character_size) {
		text.setCharacterSize(character_size);
	}
	public void set_text_color(Color c) {
		this.text.setColor(c);
	}
	public void set_fill_color(Color color) {
		shape.setFillColor(color);
	}

	public void set_visible(boolean visible) {
		this.visible = visible;
	}

	public void setOutlineColor(Color color) {
		shape.setOutlineColor(color);
		shape.setOutlineThickness(3);
	}
}
