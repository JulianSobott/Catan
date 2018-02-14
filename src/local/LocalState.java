package local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import core.Building;
import core.Player;
import data.Resource;

public class LocalState {
	// runtime information
	public enum GameMode {
		main_menu, game,

	}

	GameMode mode;

	public enum Action {
		idle,
		build_village,
		build_city,
		build_street,
	}
	Action curr_action = Action.idle;// describes the current action of the user;

	List<LocalPlayer> player_data = new ArrayList<LocalPlayer>();
	Player my_player_data = new Player("Anonymous", 0, Color.BLUE);

	Map<Resource, List<Vector2f>> field_resources = new HashMap<>();// maps resource fields to their positions on the board
	Map<Byte, List<Vector2f>> field_numbers = new HashMap<>();// maps field numbers to their positions on the board
	Map<Integer, List<Vector2f>> villages = new HashMap<>();// maps players to building types to positions
	Map<Integer, List<Vector2f>> cities = new HashMap<>();// maps players to building types to positions
	Map<Integer, List<AbstractStreet>> streets = new HashMap<>();// maps players to building types to positions

}