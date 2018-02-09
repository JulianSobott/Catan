package local;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.system.Vector2f;

public class LocalState {
	// runtime information
	enum GameMode {
		main_menu,
		game,

	}
	GameMode mode;

	Map<Resource, List<Vector2f>> field_resources = new HashMap<>();// maps resource fields to their positions on the board
	Map<Byte, List<Vector2f>> field_numbers = new HashMap<>();// maps field numbers to their positions on the board

}