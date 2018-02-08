import java.util.List;

import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

public class UI {
	// local state
	LocalState state;
	LocalLogic logic;
	Game game;

	// fonts
	Font std_font;

	UI(LocalLogic logic, Game game) {
		this.logic = logic;
		this.state = logic.state;
		this.game = game;
	}

	void init(Font std_font) {
		this.std_font = std_font;

		// DEBUG
		game.init_host_game();

	}

	boolean handle_event(Event evt) {

		return false;
	}

	void update() {

	}

	void render(RenderTarget target) {
		render_map(target);
	}

	void render_map(RenderTarget target) {
		for (java.util.Map.Entry<Resource, List<Vector2f>> resource : state.field_resources.entrySet()) {
			CircleShape shape = new CircleShape(Map.field_size * 0.5f, 6);
			shape.setFillColor(resource.getKey().color);
			shape.setOrigin(Map.field_size * 0.5f, Map.field_size * 0.5f);

			for (Vector2f pos : resource.getValue()) {
				shape.setPosition(pos);
				target.draw(shape);
			}
		}
		for (java.util.Map.Entry<Byte, List<Vector2f>> number : state.field_numbers.entrySet()) {
			Text text = new Text("" + number.getKey(), std_font);
			text.setOrigin(text.getGlobalBounds().width * 0.5f, text.getGlobalBounds().height * 0.5f);

			for (Vector2f pos : number.getValue()) {
				text.setPosition(pos);
				target.draw(text);
			}
		}
	}
}