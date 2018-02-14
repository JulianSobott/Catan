package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import local.LocalState.GameMode;
import local.LocalGameLogic;
import local.LocalUI;

//TODO Error handling when Entered a wrong IP address (reentering)
public class Client extends Networkmanager{
	private Socket server;
	private static final int PORT = 56789;
	private String serverIP;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private ClientInputListener clientInputListener;
	
	private LocalUI ui;
	private LocalGameLogic local_logic;
	
	public Client(LocalUI ui, LocalGameLogic local_logic, String serverIP) throws UnknownHostException, IOException {
		this.ui = ui;
		this.local_logic = local_logic;
		this.serverIP = serverIP;
		//Init Connection to server
		this.server = new Socket(this.serverIP, PORT);
		
		
		//Init Output and Input to Server 
		try {
			output = new ObjectOutputStream(server.getOutputStream());
			input = new ObjectInputStream(server.getInputStream());
		}catch(IOException e) {
			System.err.println("Can�t create input and output streams to server");
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
			System.err.println("Can�t close Listener at ClientCommunicator");
		}
	}

	public void message_from_core(Packet packet) {
		switch(packet.getCommand()){
		case DICE_RESULT:
			ui.show_dice_result(((Packet.DiceResult) packet.data).getDiceResult());
			break;
		case BUILD_VILLAGE:
			local_logic.build(((Packet.Build) packet.data).getIdPlayer(), Command.BUILD_VILLAGE,((Packet.Build) packet.data).getPosition());
			break;
		case BUILD_CITY:
			local_logic.build(((Packet.Build) packet.data).getIdPlayer(), Command.BUILD_CITY, ((Packet.Build) packet.data).getPosition());
			break;
		case BUILD_STREET:
			local_logic.build(((Packet.Build) packet.data).getIdPlayer(), Command.BUILD_STREET, ((Packet.Build) packet.data).getPosition());
			break;
		case STRING:
			System.out.println("Client reached Message: " + packet.getDebugString());
			break;
		case START_GAME:
			ui.build_game_menu();
			local_logic.set_mode(GameMode.game);
			System.out.println("Start game at Client");
			break;
		case PLAYER_DATA:
			this.ui.update_player_data(((Packet.PlayerData) packet.data).getPlayer());
			break;
		case NEW_MAP:
			local_logic.update_new_map(((Packet.New_Map) packet.data).getFields());
			break;
		case UPDATE_BUILDINGS:
			this.local_logic.update_buildings(((Packet.UpdateBuildings) packet.data).getBuildings());
			break;
		case SET_MODE:
			local_logic.set_mode(((Packet.NEW_MODE) packet.data).getgameMode());
			break;
		case INIT_SCOREBOARD:
			System.out.println("init Scoreboard");
			ui.init_scoreboard(((Packet.Scoreboard) packet.data).getPlayer());
			break;
		default:
			System.err.println("Unknown Command reached Client");
		}
	}
	
	public void sendMessage(Packet p) {
		try {
			output.writeObject(p);
			output.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
  
	

}
