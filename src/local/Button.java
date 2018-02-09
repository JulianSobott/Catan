package local;

import java.io.IOException;
import java.nio.file.Paths;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Button extends Widget {
	private RectangleShape shape;
	private Text text;
	private Runnable click_event;

	public Button(String text, FloatRect bounds) {
		super(bounds);

		shape = new RectangleShape(new Vector2f(bounds.width, bounds.height));
		shape.setPosition(bounds.left, bounds.top);

		this.text = new Text(text, default_font);
		this.text.setOrigin(this.text.getGlobalBounds().width * 0.5f, this.text.getGlobalBounds().height * 0.5f);
		this.text.setPosition(bounds.left + bounds.width * 0.5f, bounds.top + bounds.height * 0.5f);
		this.text.setColor(default_text_color);
	}

	@Override
	public void render(RenderTarget target) {
		target.draw(shape);
		target.draw(text);
	}

	@Override
	public void do_mouse_click() {
		if( click_event != null) click_event.run();
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

	public void set_position(Vector2f pos) {
		update_bounds(new FloatRect(pos.x, pos.y, bounds.width, bounds.height));
		shape.setPosition(pos);
		text.setPosition(bounds.left + bounds.width * 0.5f, bounds.top + bounds.height * 0.5f);
	}

	public void set_click_callback(Runnable click_event) {
		this.click_event = click_event;
	}

	public void set_text_size(int character_size) {
		text.setCharacterSize(character_size);
	}
	public void set_fill_color(Color color) {
		shape.setFillColor(color);
	}
}
