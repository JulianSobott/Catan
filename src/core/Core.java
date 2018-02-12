package core;

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
	List<Player> player = new ArrayList<Player>();

	public Core(LocalDataServer data_server) {
		this.data_server = data_server;
	}

	public void create_new_map(int map_size, int seed) {
		map.create_map(map_size, seed);
		data_server.messageToAll(new Packet(Command.NEW_MAP, new Packet.New_Map(map.getFields())));
		//data_server.update_new_map(map.getFields());
	}

	public void dice() {
		int diceResult = (int) (Math.random() * 6.) + (int) (Math.random() * 6.) + 2;
		data_server.messageToAll(new Packet(Command.DICE_RESULT, new Packet.DiceResult((byte) diceResult)));
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
