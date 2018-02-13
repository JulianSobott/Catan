package core;

import core.Map.GeneratorType;
import local.LocalPlayer;

import org.jsfml.system.Vector2i;
import java.util.ArrayList;
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
	int actualPlayer;
	List<Player> player = new ArrayList<Player>();

	public Core(LocalDataServer data_server) {
		this.actualPlayer = 0;
		this.data_server = data_server;
	}

	public void create_new_map(int map_size, int seed) {
		map.create_map(map_size + 2, seed, map_size, new float[] { 0.f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f },
				GeneratorType.HEXAGON);
		data_server.messageToAll(new Packet(Command.NEW_MAP, new Packet.New_Map(map.getFields())));
		//data_server.update_new_map(map.getFields());
	}

	public void init_game() {
		data_server.messageToAll(new Packet(Command.START_GAME));

		// translate into a more silent data structure
		List<LocalPlayer> scoreboard_data = new ArrayList<LocalPlayer>();
		for (Player p : player)
			scoreboard_data.add(scoreboard_data.size(), new LocalPlayer(p.getName(), p.getScore()));
		data_server.messageToAll(new Packet(Command.INIT_SCOREBOARD, new Packet.Scoreboard(scoreboard_data)));
	}

	public void register_new_user(String name) {
		player.add(player.size(), new Player(name));
	}

	// USER ACTIONS

	public void dice(int id) {
		if (id == actualPlayer) {
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
