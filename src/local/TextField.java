package local;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

public class TextField extends Widget {
	private static final int DEFAULT_MENU_TEXTFIELD_OUTLINE_THICKNESS = 3;
	private static final int DEFAULT_MENU_TEXTFIELD_LEFT_SPACING = 5;

	private RectangleShape shape;
	private Text text;
	private boolean is_active = false;
	private Color outline_color = Color.BLACK;

	public TextField(FloatRect bounds) {
		super(bounds);

		shape = new RectangleShape(new Vector2f(bounds.width, bounds.height));
		shape.setPosition(bounds.left, bounds.top);
		shape.setOutlineColor(default_outline_color);
		shape.setOutlineThickness(DEFAULT_MENU_TEXTFIELD_OUTLINE_THICKNESS);

		this.text = new Text("", default_font);
		this.text.setString("XOW");// needed to properly calculate bounds
		this.text.setOrigin(-DEFAULT_MENU_TEXTFIELD_LEFT_SPACING, this.text.getGlobalBounds().height * 0.5f);
		this.text.setString("");
		this.text.setPosition(bounds.left, bounds.top + bounds.height * 0.5f);
		this.text.setColor(default_text_color);
	}

	@Override
	public void render(RenderTarget target) {
		target.draw(shape);
		target.draw(text);
	}

	@Override
	public void do_mouse_click() {
		is_active = true;
		shape.setOutlineColor(default_outline_highlight_color);
	}

	public void deactivate() {
		is_active = false;
		shape.setOutlineColor(outline_color);
	}

	public void text_input(char character) {
		if (character != '\n' && character != '\r' && character != '\b' && character != '\t')
			text.setString(text.getString() + character);
	}

	// returns true if the key was handled
	public boolean special_input(Keyboard.Key key) {
		if (key == Keyboard.Key.BACKSPACE) {
			String str = text.getString();
			if (!str.isEmpty())
				text.setString(str.substring(0, str.length() - 1));
			return true;
		}
		return false;
	}

	// setter

	public void set_font(Font font) {
		text.setFont(font);
	}

	public String get_text() {
		return text.getString();
	}

	public void set_text_color(Color color) {
		text.setColor(color);
	}

	public void set_outline_color(Color color) {
		outline_color = color;
		if (!is_active)
			shape.setOutlineColor(color);
	}

	public void set_position(Vector2f pos) {
		update_bounds(new FloatRect(pos.x, pos.y, bounds.width, bounds.height));
		shape.setPosition(pos);
		text.setPosition(bounds.left, bounds.top + bounds.height * 0.5f);
	}

	public void set_text_size(int character_size) {
		text.setCharacterSize(character_size);
	}

}
