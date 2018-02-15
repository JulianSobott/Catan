package core;

import core.Map.GeneratorType;
import data.Field;
import data.Resource;
import local.LocalPlayer;
import local.LocalState.GameMode;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.Command;
import network.RemoteGameLogic;
import network.Server;
import network.RemoteUI;
import network.Packet;
import superClasses.Core;
import superClasses.GameLogic;
import superClasses.UI;

public class LocalCore extends Core {
	List<UI> uis = new ArrayList<UI>();
	List<GameLogic> logics = new ArrayList<GameLogic>();
	// data server
	Server data_server;

	// map
	Map map = new Map();

	// player data
	int current_player;
	List<Player> player = new ArrayList<Player>();

	public LocalCore() {
		Player hostPlayer = new Player("Host", 0, Color.RED);// TODO color
		player.add(hostPlayer);
		current_player = 0;
	}

	public void dice(int id) {
		int diceResult = -1;
		if (id == current_player) {
			diceResult = (int) (Math.random() * 6.) + (int) (Math.random() * 6.) + 2;
			for (UI ui : uis) {
				ui.show_dice_result((byte) diceResult);
			}
		} else {
			System.out.println(id + "is not allowed to Dice");
		}
		//distributing resources
		for (Player p : player) {
			List<Building> villages = p.buildings;
			for (Building building : villages) {
				List<Field> surroundingFields = map.get_surrounding_fields_objects(building);
				for (Field field : surroundingFields) {
					if (field.number == (byte) diceResult) {
						int addCount = building.get_type() == Building.Type.VILLAGE ? 1 : 2;
						p.add_resource(field.resource, addCount);
					}
				}
			}

		}
		for (UI ui : uis) {
			ui.update_player_data(player.get(ui.getID()));
		}
	}

	public void create_new_map(int map_size, int seed) {
		map.create_map(map_size + 2, seed, map_size, new float[] { 0.f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f },
				GeneratorType.HEXAGON);
		map.calculate_available_places();
		java.util.Map<Integer, List<Building>> new_buildings = new HashMap<Integer, List<Building>>();
		for (int i = 0; i < player.size(); i++) {
			create_initial_resources(player.get(i), map.add_random_cities(seed, 2));
			for (int j = 0; j < player.get(i).buildings.size(); j++) {
				new_buildings.put(i, player.get(i).buildings);
			}
			// TODO send message to client
			//data_server.message_to_client(i, new Packet(Command.PLAYER_DATA, new Packet.PlayerData(player.get(i))));
			uis.get(i).update_player_data(player.get(i));
		}
		//data_server.messageToAll(new Packet(Command.UPDATE_BUILDINGS, new Packet.UpdateBuildings(new_buildings)));
		//data_server.update_new_map(map.getFields());
		for (GameLogic logic : logics) {
			logic.update_new_map(map.getFields());
			logic.update_buildings(new_buildings);
		}
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
		List<LocalPlayer> scoreboard_data = new ArrayList<LocalPlayer>();
		for (Player p : player) {
			scoreboard_data.add(new LocalPlayer(p.getName(), p.getScore(), p.getColor()));
		}
		for (UI ui : uis) {
			ui.init_scoreboard(scoreboard_data);
			ui.build_game_menu();

		}
		for (GameLogic logic : logics) {
			logic.set_mode(GameMode.game);
		}
		// buildRequest(0, Building.Type.VILLAGE, new Vector2i(1, 3)); TODO delete?
	}

	@Override
	public void register_new_user(String name, Color color) {
		int id = player.size();
		player.add(new Player(name, id, color));
		UI ui = new RemoteUI(data_server);
		ui.setID(id);
		uis.add(ui);
		GameLogic logic = new RemoteGameLogic(data_server);
		logic.setID(id);
		logics.add(logic);
		uis.get(0).show_guest_at_lobby(name);
		data_server.set_id_last_joined(id);
	}

	// USER ACTIONS

	@Override
	public void buildRequest(int id, Building.Type buildType, Vector3i position) {
		//TODO Check if city is too close to other city
		if (id == current_player) {
			boolean build_sth = false;
			if (buildType == Building.Type.VILLAGE || buildType == Building.Type.CITY) {
				Vector2i pos = new Vector2i(position.x, position.y);
				if (map.is_city_place_available(pos)) {
					map.build_city(pos);
					// TODO check resources
					build_sth = true;
					if (buildType == Building.Type.VILLAGE) {
						player.get(id).buildings
								.add(new Building(Building.Type.VILLAGE, new Vector3i(position.x, position.y, 0)));
						player.get(id).take_resource(Resource.WOOD, 1);
						player.get(id).take_resource(Resource.CLAY, 1);
						player.get(id).take_resource(Resource.GRAIN, 1);
						player.get(id).take_resource(Resource.WOOL, 1);
					} else if (buildType == Building.Type.CITY) {
						player.get(id).buildings
								.add(new Building(Building.Type.CITY, new Vector3i(position.x, position.y, 0)));
						player.get(id).take_resource(Resource.ORE, 3);
						player.get(id).take_resource(Resource.GRAIN, 2);
					}
				}
			} else if (buildType == Building.Type.STREET) {
				if (map.is_street_place_available(position)) {
					map.build_street(position);
					// TODO check resources
					build_sth = true;
					player.get(id).buildings
							.add(new Building(Building.Type.STREET, new Vector3i(position.x, position.y, 0)));
					player.get(id).take_resource(Resource.WOOD, 1);
					player.get(id).take_resource(Resource.CLAY, 1);
				}
			}
			if (build_sth) {
				for (GameLogic logic : logics) {
					logic.add_building(id, new Building(buildType, position));
				}
			}
			uis.get(id).update_player_data(player.get(id));
		}
	}

	//TODO Just my thoughts about possible methods maybe rename or remove
	//TODO Either delete this methods or transfer code from build_request()
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

	public void addLogic(GameLogic logic) {
		logics.add(logic);
	}

	public void addUI(UI ui) {
		uis.add(ui);
	}

	public void setServer(Server server) {
		this.data_server = server;
	}

	public void changePlayerName(int id, String newName) {
		player.get(id).setName(newName);
	}

	@Override
	public void nextTurn(int id) {
		if (id == current_player) {
			dice(id);
			current_player = current_player + 1 >= player.size() ? 0 : current_player + 1;
		}
	}
}
