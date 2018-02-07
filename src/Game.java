import java.io.IOException;
import java.nio.file.Paths;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

public class Game {
	// windowing & stuff
	RenderWindow window = new RenderWindow();
	boolean running = true;
	float target_fps = 60.f;

	// font
	Font std_font;

	// timer
	Clock std_timer = new Clock();
	Clock frame_timer = new Clock();

	// local
	LocalLogic local_logic = new LocalLogic();
	UI ui = new UI(local_logic);
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
		window.create(new VideoMode(800, 600), "Catan", RenderWindow.DEFAULT);

		// tmp stuff
		Text tmp_text = new Text(Language.HELLO_WORLD.get_text(), std_font);
		tmp_text.setCharacterSize(100);
		tmp_text.setOrigin(tmp_text.getLocalBounds().width/2, tmp_text.getLocalBounds().height/2);
		tmp_text.setPosition(window.getSize().x/2, window.getSize().y/2);

		std_timer.restart();
		while( running ){
			for(Event evt : window.pollEvents()){
				if( evt.type == Event.Type.CLOSED) {
					running = false;
				}
			}

			// updating
			float whole_time = std_timer.getElapsedTime().asSeconds();
			float delta_time = frame_timer.restart().asSeconds();
			tmp_text.setScale((float)Math.sin(whole_time*Math.PI)*.2f+1.f, 1.f);

			// rendering
			window.clear(new Color(12, 145, 255));

			window.draw(tmp_text);

			window.display();

			// pause
			long time = Math.max(0, (long)(((1/target_fps)-(std_timer.getElapsedTime().asSeconds() - whole_time))*1000.f));
			Thread.sleep(time);
			
		}
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Works!");

		Game game = new Game();
		game.run();
	}
}
