import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.ContextSettings;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

public class Game {
	// windowing & stuff
	RenderWindow window = new RenderWindow();
	boolean running = true;
	float target_fps = 60.f;
	
	// view management
	View view;
	float delta = 0.01667f;
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
		window.create(new VideoMode(800, 600), "Catan", RenderWindow.DEFAULT, new ContextSettings(0, 0, 4));
		view = (View) window.getDefaultView();

		local_logic.init(std_font);
		ui.init(std_font);

		std_timer.restart();
		while( running ){
			for(Event evt : window.pollEvents()) {
				if( evt.type == Event.Type.CLOSED) {
					running = false;
				}
				if( !ui.handle_event(evt) ) {
					// not handled by the ui
					 if (evt.type == Event.Type.RESIZED)
					 {
						 update_view();
					 }
					 else if (evt.type == Event.Type.MOUSE_WHEEL_MOVED)
					 {
						 zoom_level *= Math.pow(0.9f, (float)evt.asMouseWheelEvent().delta);
						 update_view();
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
			long time = Math.max(0, (long)(((1/target_fps)-(std_timer.getElapsedTime().asSeconds() - whole_time))*1000.f));
			Thread.sleep(time);
			
		}
	}

	void update_view()
	{
		view.setSize((float)window.getSize().x * zoom_level, (float)window.getSize().y * zoom_level);
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
