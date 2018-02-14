package core;

import core.Map.GeneratorType;
import local.LocalPlayer;
import local.LocalState.GameMode;

import org.jsfml.system.Vector2i;
import java.util.ArrayList;
import java.util.List;
import network.Command;
import network.RemoteGameLogic;
import network.Server;
import network.RemoteUI;
import network.Packet;
import superClasses.Core;
import superClasses.GameLogic;
import superClasses.UI;

public class LocalCore extends Core{
	List<UI> uis = new ArrayList<UI>();
	List<GameLogic> logics = new ArrayList<GameLogic>();
	// data server
	Server data_server;

	// map
	Map map = new Map();

	// player data
	int actualPlayer;
	List<Player> player = new ArrayList<Player>();
	
	
	public LocalCore() {
		Player hostPlayer = new Player("Host", 0);
		player.add(hostPlayer);		
	}
	
	public void dice(int id) {
		if (id == actualPlayer) {
			int diceResult = (int) (Math.random() * 6.) + (int) (Math.random() * 6.) + 2;
			for(UI ui: uis) {
				ui.show_dice_result(diceResult);
			}
		}else {
			System.out.println(id + "is not allowed to Dice");
		}
	}

	public void create_new_map(int map_size, int seed) {
		map.create_map(map_size + 2, seed, map_size, new float[] { 0.f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f },
				GeneratorType.HEXAGON);
		for(GameLogic logic : logics) {
			logic.update_new_map(map.getFields());
		}
	}

	public void init_game() {
		List<LocalPlayer> scoreboard_data = new ArrayList<LocalPlayer>();
		for (Player p : player)
			scoreboard_data.add(scoreboard_data.size(), new LocalPlayer(p.getName(), p.getScore()));
		for(UI ui : uis) {
			ui.init_scoreboard(scoreboard_data);
			ui.build_game_menu();
			
		}
		for(GameLogic logic : logics) {
			logic.set_mode(GameMode.game);
		}
	}
	
	@Override
	public void register_new_user(String name) {
		int id = player.size();
		player.add(new Player(name, id));
		UI ui = new RemoteUI(data_server);
		ui.setID(id);
		uis.add(ui);
		GameLogic logic = new RemoteGameLogic(data_server);
		logics.add(logic);	
		uis.get(0).show_guest_at_lobby(name);
	}

	// USER ACTIONS

	

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
		if(id == actualPlayer) {
			dice(id);
			actualPlayer = actualPlayer + 1 >= player.size() ? 0 : actualPlayer + 1; 
		}
	}
}
