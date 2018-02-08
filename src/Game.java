import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.ContextSettings;
import org.jsfml.window.VideoMode;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

public class Game {
	// windowing & stuff
	RenderWindow window = new RenderWindow();
	boolean running = true;
	float target_fps = 60.f;

	// view management
	View view;
	float zoom_level = 1.f;
	float mouse_value = 3.f;
	Vector2f mouse_start;
	ArrayList<Vector2f> mouse_moved;
	boolean mouse_was_moved = false;

	// font
	Font std_font;

	// timer
	Clock std_timer = new Clock();
	Clock frame_timer = new Clock();

	// local
	LocalLogic local_logic = new LocalLogic();
	UI ui = new UI(local_logic, this);
	DataIfc data_connection;

	// server
	Core core;

	Game() {
		std_font = new Font();
		try {
			std_font.loadFromFile(Paths.get("res/Ancient Modern Tales.otf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void run() throws InterruptedException {
		window.create(new VideoMode(1200, 800), "Catan", RenderWindow.DEFAULT, new ContextSettings(8));
		view = (View) window.getDefaultView();
		view.setCenter((Map.field_size + Map.field_distance + 1.f) * (float) Map.map_size * 0.5f,
				(Map.field_size + Map.field_distance) * (float) Map.map_size * 0.866f * 0.5f);
		update_view();

		local_logic.init(std_font);
		ui.init(std_font);

		std_timer.restart();
		while (running) {
			for (Event evt : window.pollEvents()) {
				if (evt.type == Event.Type.CLOSED) {
					running = false;
				}
				if (!ui.handle_event(evt)) {
					// not handled by the ui
					if (evt.type == Event.Type.RESIZED) {
						update_view();
					} else if (evt.type == Event.Type.MOUSE_LEFT) {
						mouse_was_moved = false;
					} else if (evt.type == Event.Type.MOUSE_WHEEL_MOVED) {
						zoom_level *= Math.pow(0.9f, (float) evt.asMouseWheelEvent().delta);
						update_view();
					} else if (evt.type == Event.Type.MOUSE_BUTTON_PRESSED) {
						if (evt.asMouseButtonEvent().button == Mouse.Button.RIGHT) { // reset mouse position
							mouse_start = new Vector2f((float) evt.asMouseButtonEvent().position.x,
									(float) evt.asMouseButtonEvent().position.y);
							mouse_was_moved = true;
						}
					} else if (evt.type == Event.Type.MOUSE_BUTTON_RELEASED) {
						if (evt.asMouseButtonEvent().button == Mouse.Button.RIGHT) { // disable screen moving
							mouse_was_moved = false;
						}
					} else if (evt.type == Event.Type.MOUSE_MOVED) {
						if (mouse_was_moved) {
							float x = (float) evt.asMouseEvent().position.x, y = (float) evt.asMouseEvent().position.y;
							view.move((mouse_start.x - x) * zoom_level, (mouse_start.y - y) * zoom_level);
							mouse_start = new Vector2f(x, y);
							update_view();
						}
					}
				}
			}

			// updating
			float whole_time = std_timer.getElapsedTime().asSeconds();
			float delta_time = frame_timer.restart().asSeconds();
			ui.update();

			// rendering
			window.clear(new Color(12, 145, 255));

			ui.render(window);
			local_logic.render_map(window);

			window.display();

			// pause
			long time = Math.max(0,
					(long) (((1 / target_fps) - (std_timer.getElapsedTime().asSeconds() - whole_time)) * 1000.f));
			Thread.sleep(time);

		}
	}

	void update_view() {
		view.setCenter(
				Math.max(0.f, Math.min((Map.field_size + Map.field_distance) * Map.map_size, view.getCenter().x)),
				Math.max(0.f,
						Math.min((Map.field_size + Map.field_distance) * Map.map_size * 0.866f, view.getCenter().y)));// constraint
		zoom_level = Math.max(0.2f, Math.min(Map.map_size * 0.15f, zoom_level));// constraint
		view.setSize((float) window.getSize().x * zoom_level, (float) window.getSize().y * zoom_level);
		window.setView(view);
	}

	public static void main(String[] args) throws InterruptedException {
		Game game = new Game();
		game.run();
	}

	// creates a new game with this machine as host
	void init_host_game() {
		LocalDataServer server = new LocalDataServer(ui);
		data_connection = server;

		core = new Core(server);
	}

	// creates a new game with this machine as host
	void init_guest_game() {
		data_connection = new RemoveDataClient(ui);

	}
}
