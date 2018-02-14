import core.Map;
import local.Framework;

// new main class 
public class Main {
	
	public static void main(String[] args) throws InterruptedException {

		// TODO handle server-only mode


		Map.update_constants();
		Framework framework = new Framework();
		framework.run();
	}
}