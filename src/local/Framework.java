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

import superClasses.Core;
import superClasses.GameLogic;
import core.Map;
import core.LocalCore;
import network.Command;
import network.RemoteCore;
import network.Networkmanager;
import network.Server;
import network.Packet;
import network.Client;

public class Framework {
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
	Networkmanager data_connection;
	LocalGameLogic local_logic = new LocalGameLogic();
	LocalUI ui = new LocalUI((LocalGameLogic) local_logic, this);

	// server
	Core core;

	public Framework() {
		std_font = new Font();
		try {
			std_font.loadFromFile(Paths.get("res/Canterbury.ttf"));
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
					}catch(Exception e) {
						e.printStackTrace();
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
							local_logic
									.mouse_click_input(reverse_transform_position(evt.asMouseButtonEvent().position.x,
											evt.asMouseButtonEvent().position.y, game_view));
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
				Math.max(0.f,
						Math.min((Map.field_size + Map.field_distance) * Map.map_size_x, game_view.getCenter().x)),
				Math.max(0.f, Math.min((Map.field_size + Map.field_distance) * Map.map_size_y * Map.MAGIC_HEX_NUMBER,
						game_view.getCenter().y)));// constraint
		zoom_level = Math.max(0.2f, Math.min(Map.map_size_x * 0.15f, zoom_level));// constraint
		game_view.setSize((float) window.getSize().x * zoom_level, (float) window.getSize().y * zoom_level);
		gui_view.move(-(gui_view.getSize().x - window.getSize().x) * 0.5f,
				-(gui_view.getSize().y - window.getSize().y) * 0.5f);
		gui_view.setSize((float) window.getSize().x, (float) window.getSize().y);
		ui.update_window_size(new Vector2f(window.getSize().x, window.getSize().y), gui_view);
	}

	Vector2f reverse_transform_position(int x, int y, View view) {
		return new Vector2f(
				(float) x / (float) window.getSize().x * view.getSize().x + view.getCenter().x - view.getSize().x / 2,
				(float) y / (float) window.getSize().y * view.getSize().y + view.getCenter().y - view.getSize().y / 2);
	}

	// creates a new game with this machine as host
	void init_host_game() {
		core = new LocalCore();
		ui.setCore(core);
		((LocalCore)core).addUI(ui);
		((LocalCore)core).addLogic(local_logic);
		data_connection = new Server((LocalCore) core);
		((LocalCore)core).setServer((Server) data_connection);
		local_logic.setCore(core);
	}

	// creates a new game with this machine as client
	public boolean init_guest_game(String ip, String name) {
		String serverIp = ip;
		try {
			data_connection = new Client(ui, local_logic, serverIp);
		} catch (IOException e) {
			System.err.println("Wrong IP or server is not online");
			return false;
		}
		core = new RemoteCore();
		ui.setCore(core);
		((RemoteCore)core).setClientConnection((Client) data_connection);
		local_logic.setCore(core);
		
		Color user_color = new Color((int) (Math.random() * 170. + 50), (int) (Math.random() * 170. + 50),
				(int) (Math.random() * 170.+50));// TODO implement color picker
		core.register_new_user(name, user_color);
		
		return true;
	}
}
