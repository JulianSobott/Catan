package local;

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

import core.Core;
import core.Map;
import network.Command;
import network.DataIfc;
import network.LocalDataServer;
import network.Packet;
import network.RemoteDataClient;

public class Game {
	//Debugging Stuff
	boolean startAtLobby = true;

	// windowing & stuff
	RenderWindow window = new RenderWindow();
	boolean running = true;
	float target_fps = 60.f;

	// view management
	View game_view;
	View gui_view;// TODO make gui view aware of window resizing & handle inputs properly
	float zoom_level = 0.5f;
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
	DataIfc data_connection;
	LocalLogic local_logic = new LocalLogic();
	UI ui = new UI(local_logic, this);

	// server
	Core core;

	public Game() {
		std_font = new Font();
		try {
			std_font.loadFromFile(Paths.get("res/Ancient Modern Tales.otf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() throws InterruptedException {
		window.create(new VideoMode(1200, 800), "Catan", RenderWindow.DEFAULT, new ContextSettings(8));
		game_view = (View) window.getDefaultView();
		gui_view = new View(game_view.getCenter(), game_view.getSize());
		game_view.setCenter(Map.index_to_position(new Vector2i(Map.map_size_x / 2, Map.map_size_y / 2)));
		update_view();

		local_logic.init(std_font);
		ui.init(std_font);

		std_timer.restart();
		while (running) {
			for (Event evt : window.pollEvents()) {
				if (evt.type == Event.Type.CLOSED) {
					running = false;
					try {
						data_connection.closeAllResources();
					} catch (Exception e) {
						System.err.println("Closed before all resources closed");
					}

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
						if (evt.asMouseButtonEvent().button == Mouse.Button.LEFT) {
							local_logic.mouse_click_input(reverse_transform_position(
									evt.asMouseButtonEvent().position.x, evt.asMouseButtonEvent().position.y));
						} else if (evt.asMouseButtonEvent().button == Mouse.Button.RIGHT) { // reset mouse position
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
							game_view.move((mouse_start.x - x) * zoom_level, (mouse_start.y - y) * zoom_level);
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

			window.setView(game_view);
			local_logic.render_map(window);
			window.setView(gui_view);
			ui.render(window);

			window.display();

			// pause
			long time = Math.max(0,
					(long) (((1 / target_fps) - (std_timer.getElapsedTime().asSeconds() - whole_time)) * 1000.f));
			Thread.sleep(time);

		}
	}

	void update_view() {
		game_view.setCenter(
				Math.max(0.f, Math.min((Map.field_size + Map.field_distance) * Map.map_size_x, game_view.getCenter().x)),
				Math.max(0.f, Math.min((Map.field_size + Map.field_distance) * Map.map_size_y * Map.MAGIC_HEX_NUMBER,
						game_view.getCenter().y)));// constraint
		zoom_level = Math.max(0.2f, Math.min(Map.map_size_x * 0.15f, zoom_level));// constraint
		game_view.setSize((float) window.getSize().x * zoom_level, (float) window.getSize().y * zoom_level);
		ui.update_window_size(new Vector2f(window.getSize().x, window.getSize().y));
	}

	Vector2f reverse_transform_position(int x, int y) {
		return new Vector2f(
				(float) x / (float) window.getSize().x * game_view.getSize().x + game_view.getCenter().x - game_view.getSize().x / 2,
				(float) y / (float) window.getSize().y * game_view.getSize().y + game_view.getCenter().y - game_view.getSize().y / 2);
	}

	// creates a new game with this machine as host
	void init_host_game() {
		LocalDataServer server = new LocalDataServer(ui, local_logic);
		data_connection = server;
		local_logic.set_data_interface(data_connection);
		ui.set_data_interface(data_connection);
		core = new Core(server);
	}

	// creates a new game with this machine as client
	public boolean init_guest_game(String ip, String name) {
		String serverIp = "192.168.2.118";
		try {
			data_connection = new RemoteDataClient(ui, local_logic, serverIp);
		}catch(IOException e) {
			return false;
		}
		
		local_logic.set_data_interface(data_connection);
		ui.set_data_interface(data_connection);
		((RemoteDataClient) data_connection).message_to_core(new Packet(Command.NAME, new Packet.Name(name)));
		return true;
	}
}
