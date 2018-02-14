package core;

import core.Map.GeneratorType;
import local.LocalPlayer;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.Command;
import network.LocalDataServer;
import network.Packet;

public class Core {

	// data server
	LocalDataServer data_server;

	// map
	Map map = new Map();

	// player data
	int current_player;
	List<Player> player = new ArrayList<Player>();

	public Core(LocalDataServer data_server) {
		this.current_player = 0;
		this.data_server = data_server;
	}

	public void create_new_map(int map_size, int seed) {
		map.create_map(map_size + 2, seed, map_size, new float[] { 0.f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f },
				GeneratorType.HEXAGON);
		map.calculate_available_places();
		data_server.messageToAll(new Packet(Command.NEW_MAP, new Packet.New_Map(map.getFields())));
		java.util.Map<Integer, List<Building>> new_buildings = new HashMap<Integer, List<Building>>();
		for (int i = 0; i < player.size(); i++) {
			create_initial_resources(player.get(i), map.add_random_cities(seed, 2));
			for (int j = 0; j < player.get(i).buildings.size(); j++) {
				new_buildings.put(i, player.get(i).buildings);
			}
			// TODO send message to client
			//data_server.message_to_client(i, new Packet(Command.PLAYER_DATA, new Packet.PlayerData(player.get(i))));
			//if (i == 0)
			//	data_server.message_from_core(new Packet(Command.PLAYER_DATA, new Packet.PlayerData(player.get(i))));
		}
		data_server.messageToAll(new Packet(Command.UPDATE_BUILDINGS, new Packet.UpdateBuildings(new_buildings)));
		//data_server.update_new_map(map.getFields());
	}

	private void create_initial_resources(Player p, List<Vector2i> cities) {
		List<Vector2i> fields = new ArrayList<Vector2i>();
		for (Vector2i pos : cities) {
			p.buildings.add(new Building(Building.Type.VILLAGE, new Vector3i(pos.x, pos.y, 0)));
			fields.addAll(map.get_surrounding_fields(pos));
		}
		for (Vector2i pos : fields) {
			p.add_resource(map.getFields()[pos.x][pos.y].resource, 1);
		}
		p.update_score();
	}

	public void init_game() {
		data_server.messageToAll(new Packet(Command.START_GAME));

		// translate into a more silent data structure
		List<LocalPlayer> scoreboard_data = new ArrayList<LocalPlayer>();
		for (Player p : player)
			scoreboard_data.add(new LocalPlayer(p.getName(), p.getScore(), p.getColor()));
		data_server.messageToAll(new Packet(Command.INIT_SCOREBOARD, new Packet.Scoreboard(scoreboard_data)));
	}

	public void register_new_user(String name, Color color) {
		player.add(new Player(name, color));
	}

	// USER ACTIONS

	public void dice(int id) {
		if (id == current_player) {
			int diceResult = (int) (Math.random() * 6.) + (int) (Math.random() * 6.) + 2;
			data_server.messageToAll(new Packet(Command.DICE_RESULT, new Packet.DiceResult((byte) diceResult)));
		}
	}

	public void buildRequest(int id, Command buildType, Vector2i position) {
		/*
			Is wanted place free
				No buildings on this place
				No buildings too close
			Has player enough resources
			Has player enough buildings (when limited)
		=> build()
		*/
	}

	//TODO Just my thoughts about possible methods maybe rename or remove

	public void build() {
		/*
		Add building to position
		Take resources from player
		Add score for player
		Check for win
		=> update()
		 */
	}

	public void update() {
		/*
		Player show new resources
		All show updated map
		All show updated score
		
		Next player dice (automatically?)
		 */
	}

}
