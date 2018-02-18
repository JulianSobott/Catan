package core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

import core.Building.Type;
import core.Map.GeneratorType;
import data.Field;
import data.Resource;
import data.SavedGame;
import local.LocalPlayer;
import local.LocalState.GameMode;
import local.TradeDemand.Vendor;
import math.Vector3iMath;
import local.LocalUI;
import local.TradeDemand;
import local.TradeOffer;
import network.RemoteGameLogic;
import network.RemoteUI;
import network.Server;
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
	boolean initial_round = true;
	List<Player> player = new ArrayList<Player>();

	//Filehandler
	private LocalFilehandler fileHandler;

	public LocalCore() {
		fileHandler = new LocalFilehandler();
		Player hostPlayer = new Player("Host", 0, Color.BLACK);
		player.add(hostPlayer);
		current_player = 0;
	}

	public void dice() {
		int diceResult = (int) (Math.random() * 6.) + (int) (Math.random() * 6.) + 2;
		for (UI ui : uis) {
			ui.show_dice_result((byte) diceResult);
		}
		//distributing resources
		for (Player p : player) {
			List<Building> villages = p.buildings;
			for (Building building : villages) {
				List<Field> surroundingFields = map.get_surrounding_field_objects(building);
				for (Field field : surroundingFields) {
					if (field.number == (byte) diceResult) {
						int addCount = building.get_type() == Building.Type.VILLAGE ? 1
								: building.get_type() == Building.Type.CITY ? 2 : 0;
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
			for (Vector3i pos : map.add_random_cities(seed, 1)) {
				player.get(i).buildings.add(new Building(Building.Type.VILLAGE, pos));
			}
			player.get(i).update_score();
			for (int j = 0; j < player.get(i).buildings.size(); j++) {
				new_buildings.put(i, player.get(i).buildings);
			}

			// Initial round resources TODO move into database
			player.get(i).add_resource(Resource.CLAY, 1);
			player.get(i).add_resource(Resource.GRAIN, 1);
			player.get(i).add_resource(Resource.WOOD, 1);
			player.get(i).add_resource(Resource.WOOL, 1);

			// DEBUG
			player.get(i).add_resource(Resource.CLAY, 50);
			player.get(i).add_resource(Resource.GRAIN, 50);
			player.get(i).add_resource(Resource.ORE, 50);
			player.get(i).add_resource(Resource.WOOD, 50);
			player.get(i).add_resource(Resource.WOOL, 50);

			uis.get(i).update_player_data(player.get(i));
		}
		for (GameLogic logic : logics) {
			logic.update_new_map(map.getFields());
			logic.update_buildings(new_buildings);
		}
	}

	// creates the resources after the initial round
	private void create_initial_resources(Player p) {
		List<Vector2i> fields = new ArrayList<Vector2i>();
		for (Building building : p.buildings) {
			fields.addAll(map.get_surrounding_fields(building.get_position()));
		}
		for (Vector2i pos : fields) {
			p.add_resource(map.getFields()[pos.x][pos.y].resource, 1);
		}
	}

	public void init_game() {
		update_scoreboard_data();
		for (UI ui : uis) {
			ui.build_game_menu();
			ui.set_current_player(player.get(current_player).getName());
		}
		for (GameLogic logic : logics) {
			logic.set_mode(GameMode.game);
		}
		uis.get(0).update_player_data(player.get(0));
		//		buildRequest(0, Type.VILLAGE, new Vector3i(0,0,1));
	}

	@Override
	public void register_new_user(String name, Color color) {
		String playername = checkName(name);
		int id = player.size();
		player.add(new Player(playername, id, color));
		UI ui = new RemoteUI(data_server);
		ui.setID(id);
		uis.add(ui);
		GameLogic logic = new RemoteGameLogic(data_server);
		logic.setID(id);
		logics.add(logic);
		uis.get(0).show_guest_at_lobby(playername);
		data_server.set_id_last_joined(id);
	}

	public String checkName(String name) {
		String newName = name;
		for (Player p : player) {
			if (p.getName().equals(name)) {
				newName = checkName(name + (int) (Math.random() * 100));
			}
		}
		return newName;
	}

	// USER ACTIONS
	@Override
	public void buildRequest(int id, Building.Type buildType, Vector3i position) {
		//TODO Check if city is too close to other city
		Player this_player = player.get(id);
		if (id == current_player && (initial_round || owns_nearby_building(this_player,
				map.get_nearby_building_sites(position), buildType == Building.Type.VILLAGE))) {
			boolean build_sth = false;
			java.util.Map<Resource, Integer> resources = this_player.resources;
			if (buildType == Building.Type.VILLAGE) {
				if (map.is_village_place_available(position)) {
					// TODO extract all building costs into a database to make them configurable
					if (resources.get(Resource.WOOD) >= 1 && resources.get(Resource.CLAY) >= 1
							&& resources.get(Resource.GRAIN) >= 1 && resources.get(Resource.WOOL) >= 1) {
						map.build_village(position);
						build_sth = true;
						this_player.buildings.add(new Building(Building.Type.VILLAGE, position));
						this_player.take_resource(Resource.WOOD, 1);
						this_player.take_resource(Resource.CLAY, 1);
						this_player.take_resource(Resource.GRAIN, 1);
						this_player.take_resource(Resource.WOOL, 1);
					}
				}
			} else if (buildType == Building.Type.CITY) {
				if (map.is_city_place_available(position)) {
					if (resources.get(Resource.ORE) >= 3 && resources.get(Resource.GRAIN) >= 2) {
						// find & remove old village
						int village_index = -1;
						for (int i = 0; i < this_player.buildings.size(); i++) {
							if (this_player.buildings.get(i).equals(new Building(Building.Type.VILLAGE, position))) {
								village_index = i;
								break;
							}
						}
						if (village_index >= 0) {// the selected building is another player's
							this_player.buildings.remove(village_index);

							map.build_city(position);
							build_sth = true;

							this_player.buildings.add(new Building(Building.Type.CITY, position));
							this_player.take_resource(Resource.ORE, 3);
							this_player.take_resource(Resource.GRAIN, 2);
						}
					}
				}
			} else if (buildType == Building.Type.STREET) {
				if (map.is_street_place_available(position)) {
					if (resources.get(Resource.WOOD) >= 1 && resources.get(Resource.CLAY) >= 1) {
						map.build_street(position);
						build_sth = true;
						this_player.buildings.add(new Building(Building.Type.STREET, position));
						this_player.take_resource(Resource.WOOD, 1);
						this_player.take_resource(Resource.CLAY, 1);
					}
				}
			}
			if (build_sth) {
				for (GameLogic logic : logics) {
					logic.add_building(id, new Building(buildType, position));
				}
				this_player.update_score();
				update_scoreboard_data();
				uis.get(id).update_player_data(this_player);
			}

		}
	}

	private boolean owns_nearby_building(Player p, List<Vector3i> buildings, boolean but_not_a_settlement) {
		boolean found_building = false;
		for (Vector3i b : buildings) {
			for (Building pb : p.buildings) {
				if (Vector3iMath.are_equal(pb.get_position(), b)) {
					if (but_not_a_settlement && (b.z == Map.LAYER_NORTH_STMT || b.z == Map.LAYER_SOUTH_STMT))
						return false;// found a nearby settlement
					else {
						if (but_not_a_settlement)
							found_building = true;
						else
							return true;
					}
				}
			}
		}
		if (found_building)
			return true;
		else
			return false;
	}

	public void update_scoreboard_data() {
		List<LocalPlayer> scoreboard_data = new ArrayList<LocalPlayer>();
		for (Player p : player) {
			scoreboard_data.add(new LocalPlayer(p.getName(), p.getScore(), p.getColor()));
		}
		for (UI ui : uis) {
			ui.update_scoreboard(scoreboard_data);
		}
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

	public void changePlayerProps(int id, String newName, Color color) {
		String newerName = checkName(newName);
		player.get(id).setName(newerName);
		player.get(id).setColor(color);
	}

	@Override
	public void nextTurn(int id) {
		if (id == current_player) {
			current_player = current_player + 1 >= player.size() ? 0 : current_player + 1;

			if (current_player == 0 && initial_round) {// initial round has finished
				initial_round = false;

				for (Player p : player) {
					create_initial_resources(p);
				}
			}

			if (!initial_round)
				dice();
		}

		// notify others about player change
		String name = player.get(current_player).getName();
		for (UI ui : uis) {
			ui.set_current_player(name);
		}
	}

	@Override
	public void new_trade_demand(TradeDemand tradeDemand) {
		if (tradeDemand.getVendor() == Vendor.BANK) {
			int neededResources = 0;
			int availableResources = 0;
			for (Resource r : tradeDemand.getOfferedResources().keySet()) {
				if (player.get(tradeDemand.get_demander_id()).get_resources(r) >= 4) {
					availableResources += player.get(tradeDemand.get_demander_id()).get_resources(r);
				}
			}
			for (Resource r : tradeDemand.getWantedResources().keySet()) {
				neededResources += 4;
			}
			if (neededResources <= availableResources) {
				for (Resource r : tradeDemand.getWantedResources().keySet()) {
					player.get(tradeDemand.get_demander_id()).add_resource(r, 1);
				}
				for (Resource r : tradeDemand.getOfferedResources().keySet()) {
					player.get(tradeDemand.get_demander_id()).take_resource(r, 4);
				}
				uis.get(tradeDemand.get_demander_id()).update_player_data(player.get(tradeDemand.get_demander_id()));
				uis.get(tradeDemand.get_demander_id()).closeTradeWindow();
			}
		} else if (tradeDemand.getVendor() == Vendor.PLAYER) {
			for (Player p : player) {
				if (p.getId() != tradeDemand.get_demander_id()) {
					boolean showTrade = true;
					for (Resource r : tradeDemand.getWantedResources().keySet()) {
						if (p.resources.get(r) < 1) {
							showTrade = false;
						}
					}
					if (showTrade) {
						uis.get(p.getId()).show_trade_demand(tradeDemand);
					}
				}
			}
		}
	}

	@Override
	public void new_trade_offer(TradeOffer tradeOffer) {
		java.util.Map<Resource, Integer> demanderPlayerResources = player.get(tradeOffer.getDemanderID()).resources;
		java.util.Map<Resource, Integer> offeredResources = tradeOffer.getOfferedResources();
		boolean sendOffer = true;
		for (Resource r : offeredResources.keySet()) {
			if (offeredResources.get(r) > demanderPlayerResources.get(r)) {
				sendOffer = false;
			}
		}
		if (sendOffer) {
			uis.get(tradeOffer.getDemanderID()).addTradeOffer(tradeOffer);
		}
	}

	@Override
	public void acceptOffer(TradeOffer offer) {
		for (Resource r : offer.getDemandedResources().keySet()) {
			player.get(offer.getDemanderID()).add_resource(r, 1);
			player.get(offer.getVendor_id()).take_resource(r, 1);
		}
		for (Resource r : offer.getOfferedResources().keySet()) {
			player.get(offer.getDemanderID()).take_resource(r, offer.getOfferedResources().get(r));
			player.get(offer.getVendor_id()).add_resource(r, offer.getOfferedResources().get(r));
		}
		uis.get(offer.getDemanderID()).update_player_data(player.get(offer.getDemanderID()));
		uis.get(offer.getDemanderID()).closeTradeWindow();
		uis.get(1).update_player_data(player.get(1));
		uis.get(1).closeTradeWindow();
	}

	@Override
	public void closeTrade() {
		for (UI ui : uis) {
			ui.closeTradeWindow();
		}
	}

	public void showIpAtLobby(String ip) {
		((LocalUI) this.uis.get(0)).showIpInLobby(ip);
	}

	public void saveGame(String game_name) {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		SavedGame game = new SavedGame(map.getFields(), player, date);
		if (game_name.isEmpty()) {
			game_name = "QuickSave_" + c.get(c.YEAR) + "_" + c.get(c.MONTH) + 1 + "_" + c.get(c.DAY_OF_MONTH) + "_"
					+ c.get(c.HOUR_OF_DAY);
		}
		game.setName(game_name);
		fileHandler.saveGame(game);
	}

	public void loadGame(SavedGame game) {
		player = game.getPlayer();
		map.set_fields(game.getFields());
		update_scoreboard_data();
		for (UI ui : uis) {
			ui.build_game_menu();
			ui.set_current_player(player.get(current_player).getName());
			ui.update_player_data(player.get(ui.getID()));
		}
		java.util.Map<Integer, List<Building>> new_buildings = new HashMap<Integer, List<Building>>();
		for (Player p : player) {
			for (int j = 0; j < p.buildings.size(); j++) {
				new_buildings.put(p.getId(), p.buildings);
			}
		}
		for (GameLogic logic : logics) {
			logic.set_mode(GameMode.game);
			logic.update_new_map(map.getFields());
			logic.update_buildings(new_buildings);
		}
	}

	public void kickPlayer(String name) {
		int id = 0;
		for(Player p : player) {
			if(p.getName() == name) {
				id = p.getId();
			}
		}
		for(UI ui : uis) {
			if(ui.getID() == id) {
				ui.show_kicked();
			}
		}
		player.remove(id);
		uis.remove(id);
		logics.remove(id);
		data_server.remove_client(id);
	}
}
