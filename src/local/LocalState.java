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

	List<LocalPlayer> player_data = new ArrayList<LocalPlayer>();
	Player my_player_data = new Player("Anonymous", Color.BLUE);

	Map<Resource, List<Vector2f>> field_resources = new HashMap<>();// maps resource fields to their positions on the board
	Map<Byte, List<Vector2f>> field_numbers = new HashMap<>();// maps field numbers to their positions on the board
	Map<Integer, Map<Building.Type, List<Vector2f>>> buildings = new HashMap<>();// maps players to building types to positions

}