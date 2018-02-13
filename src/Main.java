import core.Map;
import local.Game;

// new main class 
public class Main {
	
	public static void main(String[] args) throws InterruptedException {

		// TODO handle server-only mode


		Map.update_constants();
		Game game = new Game();
		game.run();
	}
}