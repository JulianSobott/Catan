package com.catangame.catan.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import com.catangame.catan.utils.Color;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.math.Vector3i;

import com.catangame.catan.core.Building.Type;
import com.catangame.catan.core.Map.GeneratorType;
import com.catangame.catan.data.DevelopmentCard;
import com.catangame.catan.data.Field;
import com.catangame.catan.data.Resource;
import com.catangame.catan.data.SavedGame;
import com.catangame.catan.local.LocalPlayer;
import com.catangame.catan.local.LocalState.GameMode;
import com.catangame.catan.local.TradeDemand.Vendor;
import com.catangame.catan.local.LocalUI;
import com.catangame.catan.local.TradeDemand;
import com.catangame.catan.local.TradeOffer;
import com.catangame.catan.network.RemoteGameLogic;
import com.catangame.catan.network.RemoteUI;
import com.catangame.catan.network.Server;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.superClasses.GameLogic;
import com.catangame.catan.superClasses.UI;

public class LocalCore extends Core {
	List<UI> uis = new ArrayList<UI>();
	List<GameLogic> logics = new ArrayList<GameLogic>();
	// data server
	Server data_server;

	private SavedGame savedGame = null;

	Map map = new Map();

	// player data
	int current_player;
	boolean initial_round = true;
	List<Player> player = new ArrayList<Player>();

	//TODO add field at lobby to change this
	int winningScore = 3; //Default Score to win a game

	//Filehandler
	private LocalFilehandler fileHandler;

	public LocalCore() {
		fileHandler = new LocalFilehandler();
		Player hostPlayer = new Player("Host", 0, Color.BLACK);
		player.add(hostPlayer);
		current_player = 0;

		// create initial resource database
		TreeMap<Resource, Integer> neededResources = new TreeMap<Resource, Integer>();
		neededResources.put(Resource.WOOD, 1);
		neededResources.put(Resource.CLAY, 1);
		Building.Type.STREET.setNeededResources(neededResources);
		neededResources = new TreeMap<Resource, Integer>();
		neededResources.put(Resource.WOOD, 1);
		neededResources.put(Resource.WOOL, 1);
		neededResources.put(Resource.GRAIN, 1);
		neededResources.put(Resource.CLAY, 1);
		Building.Type.VILLAGE.setNeededResources(neededResources);
		neededResources = new TreeMap<Resource, Integer>();
		neededResources.put(Resource.GRAIN, 2);
		neededResources.put(Resource.ORE, 3);
		Building.Type.CITY.setNeededResources(neededResources);
	}

	void dice() {
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

	// \p startResources are the initial resources measured by the count of villages
	public void create_new_map(int islandSize, int seed, float[] resourceRatio, GeneratorType generatorType,
			int randomStartBuildings, int startResources) {
		map.create_map(islandSize + 2, seed, islandSize, resourceRatio, generatorType);
		map.calculate_available_places();
		java.util.Map<Integer, List<Building>> new_buildings = new HashMap<Integer, List<Building>>();
		for (int i = 0; i < player.size(); i++) {
			for (Vector3i pos : map.add_random_cities(seed, randomStartBuildings)) {
				player.get(i).buildings.add(new Building(Building.Type.VILLAGE, pos));
			}
			player.get(i).update_score();
			for (int j = 0; j < player.get(i).buildings.size(); j++) {
				new_buildings.put(i, player.get(i).buildings);
			}

			// Initial round resources
			for (java.util.Map.Entry<Resource, Integer> nr : Building.Type.VILLAGE.getNeededResources().entrySet())
				player.get(i).add_resource(nr.getKey(), nr.getValue() * startResources);

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

	public void register_new_user(String name, Color color, int communicatorID) {
		boolean correctName = false;
		if (savedGame != null) {
			for (Player p : savedGame.getPlayer()) {
				if (p.getName().equals(name)) {
					correctName = true;
					System.out.println("Correct Name");
				}
			}
			if (!correctName) {
				UI ui = new RemoteUI(data_server);
				ui.setID(communicatorID);
				((RemoteUI) ui).showAllPossibleNames(savedGame.getPlayer());
			}
		}
		if (savedGame == null || correctName) {
			String playername = checkName(name);
			int id = player.size();
			player.add(new Player(playername, id, color));
			UI ui = new RemoteUI(data_server);
			ui.setID(id);
			uis.add(ui);
			GameLogic logic = new RemoteGameLogic(data_server);
			logic.setID(id);
			logics.add(logic);
			for (UI tempUI : uis) {
				tempUI.show_guest_at_lobby(name);
			}
			data_server.set_id_last_joined(id);
		}
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
		Player this_player = player.get(id);
		if (id == current_player) {
			boolean build_sth = false;
			java.util.Map<Resource, Integer> resources = this_player.resources;

			boolean enoughResources = true;
			for (java.util.Map.Entry<Resource, Integer> nr : buildType.getNeededResources().entrySet())
				if (resources.get(nr.getKey()) < nr.getValue())
					enoughResources = false;
			if (enoughResources) {

				if (buildType == Building.Type.VILLAGE) {
					if (map.is_village_place_available(position)) {
						List<Vector3i> nearbyBuildingSites = map.get_nearby_building_sites(position);
						if ((initial_round || owns_nearby_building(this_player, nearbyBuildingSites))
								&& no_nearby_settlement(nearbyBuildingSites)) {
							map.build_village(position);
							build_sth = true;
							this_player.buildings.add(new Building(Building.Type.VILLAGE, position));
						}
					}
				} else if (buildType == Building.Type.CITY) {
					if (map.is_city_place_available(position)) {
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
						}
					}
				} else if (buildType == Building.Type.STREET) {
					if (map.is_street_place_available(position)
							&& owns_nearby_building(this_player, map.get_nearby_building_sites(position))) {
						map.build_street(position);
						build_sth = true;
						this_player.buildings.add(new Building(Building.Type.STREET, position));
					}
				}
			}
			if (build_sth) {
				for (java.util.Map.Entry<Resource, Integer> nr : buildType.getNeededResources().entrySet())
					this_player.take_resource(nr.getKey(), nr.getValue());

				for (GameLogic logic : logics) {
					logic.add_building(id, new Building(buildType, position));
				}
				this_player.update_score();
				update_scoreboard_data();
				uis.get(id).update_player_data(this_player);
			}

		}
	}

	private boolean owns_nearby_building(Player p, List<Vector3i> buildings) {
		for (Vector3i b : buildings) {
			for (Building pb : p.buildings) {
				if (Vector3i.are_equal(pb.get_position(), b)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean no_nearby_settlement(List<Vector3i> buildings) {
		for (Vector3i b : buildings) {
			for (Player p : player) {
				for (Building pb : p.buildings) {
					if ((b.z == Map.LAYER_NORTH_STMT || b.z == Map.LAYER_SOUTH_STMT)
							&& Vector3i.are_equal(pb.get_position(), b))
						return false;
				}
			}
		}
		return true;
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
			//Checks if player won
			if (player.get(id).getScore() >= winningScore) {
				for (UI ui : uis) {
					ui.showEndScreen(id, player);
				}
			} else {
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
			player.get(offer.getDemanderID()).add_resource(r, offer.getDemandedResources().get(r));
			player.get(offer.getVendor_id()).take_resource(r, offer.getDemandedResources().get(r));
		}
		for (Resource r : offer.getOfferedResources().keySet()) {
			player.get(offer.getDemanderID()).take_resource(r, offer.getOfferedResources().get(r));
			player.get(offer.getVendor_id()).add_resource(r, offer.getOfferedResources().get(r));
		}
		for (UI ui : uis) {
			ui.update_player_data(player.get(ui.getID()));
			ui.closeTradeWindow();
		}
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
		SavedGame game = new SavedGame(map.getFields(), player, date, current_player);
		if (game_name.isEmpty()) {
			game_name = "QuickSave_" + c.get(c.YEAR) + "_" + c.get(c.MONTH) + 1 + "_" + c.get(c.DAY_OF_MONTH) + "_"
					+ c.get(c.HOUR_OF_DAY);
		}
		game.setName(game_name);
		fileHandler.saveGame(game);
	}

	public void loadGame(SavedGame game) {
		savedGame = game;
	}

	public void kickPlayer(String name) {
		int id = 0;
		for (Player p : player) {
			if (p.getName() == name) {
				break;
			} else {
				id++;
			}
		}
		for (UI ui : uis) {
			if (ui.getID() == id) {
				ui.show_kicked();
			}
		}
		player.remove(id);
		uis.remove(id);
		logics.remove(id);
		data_server.remove_client(id);
	}

	public boolean getLoadedGame() {
		return savedGame != null;
	}

	public SavedGame getSavedGame() {
		return savedGame;
	}

	public void setSavedGame(SavedGame savedGame) {
		this.savedGame = savedGame;
	}

	public void startLoadedGame() {
		List<Player> newPlayerdata = new ArrayList<Player>();
		for (Player p : player) {
			for (Player loadedP : savedGame.getPlayer()) {
				if (p.getName() == loadedP.getName()) {
					newPlayerdata.add(loadedP);
				}
			}
		}
		current_player = savedGame.getCurrentPlayer();
		newPlayerdata.add(savedGame.getPlayer().get(0)); //Always add Host
		player = newPlayerdata;
		map.set_fields(savedGame.getFields());
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

	@Override
	public void buyDevelopmentCard(int id) {
		//TODO VAlidate if user is allowed and have enough resources
		if (id == current_player) {
			if (player.get(id).get_resources(Resource.GRAIN) >= 1 && player.get(id).get_resources(Resource.ORE) >= 1
					&& player.get(id).get_resources(Resource.WOOL) >= 1) {
				Random rand = new Random();
				DevelopmentCard card = DevelopmentCard.values()[rand.nextInt(DevelopmentCard.values().length - 1)];
				player.get(id).addDevelopmentCard(card);

				player.get(id).take_resource(Resource.GRAIN, 1);
				player.get(id).take_resource(Resource.ORE, 1);
				player.get(id).take_resource(Resource.WOOL, 1);
				uis.get(id).update_player_data(player.get(id));
			}
		}
	}

	@Override
	public void playCard(int id, DevelopmentCard card) {
		if (id == current_player) {
			switch (card) {
			case FREE_RESOURCES:

				break;
			case KNIGHT:

				break;
			case MONOPOL:

				break;
			case FREE_STREETS:

				break;
			case POINT:

				break;
			default:
				System.err.println("Unknown Card reached core:" + card);
			}
		}
	}

}
