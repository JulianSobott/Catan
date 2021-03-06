package com.catangame.catan.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.LocalState.GameMode;
import com.catangame.catan.local.LocalGameLogic;
import com.catangame.catan.local.LocalUI;
import com.catangame.catan.network.Packet.TradeDemand;
import com.catangame.catan.superClasses.UI;

//TODO Error handling when Entered a wrong IP address (reentering)
public class Client extends Networkmanager {
	private Socket server;
	private static final int PORT = 56780;
	private String serverIP;

	private ObjectOutputStream output;
	private ObjectInputStream input;

	private ClientInputListener clientInputListener;

	private LocalUI ui;
	private LocalGameLogic gameLogic;

	public Client(LocalUI ui, LocalGameLogic gameLogic, String serverIP) throws UnknownHostException, IOException {
		this.ui = ui;
		this.gameLogic = gameLogic;
		this.serverIP = serverIP;
		//Init Connection to server
		this.server = new Socket(this.serverIP, PORT);

		//Init Output and Input to Server 
		try {
			output = new ObjectOutputStream(server.getOutputStream());
			input = new ObjectInputStream(server.getInputStream());
			System.out.println("Connected");
		} catch (IOException e) {
			System.err.println("Can't create input and output streams to server");
			e.printStackTrace();
			return;
		}
		ui.build_guest_lobby_window();
		clientInputListener = new ClientInputListener(this, input);
		clientInputListener.start();
	}

	public void closeAllResources() {
		clientInputListener.closeConnectionToServer();
		try {
			input.close();
			output.close();
			server.close();
		} catch (IOException e) {
			System.err.println("Can't close Listener at ClientCommunicator");
		}
	}

	public void message_from_core(Packet packet) {
		switch (packet.getCommand()) {
		case DICE_RESULT:
			ui.show_dice_result(((Packet.DiceResult) packet.data).getDiceResult());
			break;
		case STRING:
			System.out.println("Client reached Message: " + packet.getDebugString());
			break;
		case START_GAME:
			ui.build_game_menu();
			gameLogic.set_mode(GameMode.game);
			System.out.println("Start game at Client");
			break;
		case PLAYER_DATA:
			this.ui.update_player_data(((Packet.PlayerData) packet.data).getPlayer());
			break;
		case NEW_MAP:
			gameLogic.update_new_map(((Packet.New_Map) packet.data).getFields(), ((Packet.New_Map) packet.data).harbours);
			break;
		case UPDATE_BUILDINGS:
			this.gameLogic.update_buildings(((Packet.UpdateBuildings) packet.data).getBuildings());
			break;
		case NEW_BUILDING:
			gameLogic.add_building(((Packet.NewBuilding) packet.data).getID(),
					((Packet.NewBuilding) packet.data).getBuilding());
			break;
		case SET_CURR_USER:
			ui.set_current_player(((Packet.SetCurrUser) packet.data).getPlayer());
			break;
		case SET_MODE:
			gameLogic.set_mode(((Packet.NewMode) packet.data).getGameMode());
			break;
		case INIT_SCOREBOARD:
			ui.update_scoreboard(((Packet.Scoreboard) packet.data).getPlayer());
			break;
		case TRADE_DEMAND:
			ui.show_trade_demand(((Packet.TradeDemand) packet.data).getTradeDemand());
			break;
		case DEMAND_DECLINED:
			ui.showDemandDeclined(((Packet.ID)packet.data).getID());
			break;
		case ADD_TRADE_OFFER:
			ui.addTradeOffer(((Packet.TradeOffer) packet.data).getTradeOffer());
			break;
		case CLOSE_TRADE_WINDOW:
			ui.closeTradeWindow();
			break;
		case SET_ID:
			ui.setID(((Packet.ID) packet.data).getID());
			gameLogic.setID(((Packet.ID) packet.data).getID());
			break;
		case SHOW_KICKED:
			ui.show_kicked(((Packet.StringData)packet.data).string);
			break;
		case SHOW_ALL_POSSIBLE_NAMES:
			ui.showAllPossibleNames(((Packet.PlayerList)packet.data).getPlayer());	
			break;
		case SHOW_NEW_MEMBER:
			ui.show_guest_at_lobby(((Packet.Name)packet.data).getName());
			break;
		case END_SCREEN:
			ui.showEndScreen(0, ((Packet.PlayerList) packet.data).getPlayer()); //TODO get Winner ID from Core or remove
			break;
		case SHOW_DEVELOPMENTCARD_WINDOW:
			ui.showDevelopmentCardWindow(((Packet.Developmentcard) packet.data).getCard());
			break;
		case SHOW_TO_MUCH_RESOURCES:
			ui.showToMuchResourcesWindow(((Packet.Num) packet.data).num);
			break;
		case MOVE_ROBBER:
			if(packet.data != null) {
				if(((Packet.Position) packet.data).position != null) {
					gameLogic.setRobberPosition((Vector2) (((Packet.Position) packet.data).position));
				}
			}else {
				ui.showMoveRobber();
			}	
			break;
		case STEEL_RESOURCE:
			if(((Packet.PlayerList) packet.data).getPlayer() != null) {
				ui.showSteelResource(((Packet.PlayerList) packet.data).getPlayer());
			}
			break;
		case SHOW_ALL_JOINABLE_GAMES:
			ui.showAllJoinableGames(((Packet.JoinableGames)packet.data).allJoinableGames);
			break;
		case SHOW_GUEST_LOBBY:
			ui.showGuestLobby(((Packet.StringData)packet.data).string);
			break;
		case MESSAGE:
			ui.addNewMessage(((Packet.MessageData) packet.data).msg);
			break;
		case CONNECTION_LOST:
			ui.showConnectionLost(((Packet.StringData) packet.data).string);
			break;
		default:
			System.err.println("Unknown Command reached Client" + packet.getCommand());
		}
	}

	public void sendMessage(Packet p) {
		try {
			output.writeObject(p);
			output.flush();
			this.output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
