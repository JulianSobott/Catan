import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
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
		
	}
}