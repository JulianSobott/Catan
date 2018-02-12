package network;
import local.Field;
import local.LocalLogic;
import local.LocalState;
import local.UI;

public abstract class DataIfc {
	// ui
	UI ui;
	LocalLogic local_logic;

	DataIfc(UI ui, LocalLogic local_logic) {
		this.ui = ui;
		this.local_logic = local_logic;
	}

	public abstract void closeAllResources();

	// messages from the core
	public void message_from_core(Packet packet) {
		switch(packet.getCommand()) {
		case START_GAME:
			this.local_logic.set_mode(LocalState.GameMode.game);
			this.ui.build_game_menu();
			break;
		case NEW_MAP:
			this.local_logic.update_new_map(((Packet.New_Map) packet.data).getFields());
			break;
		case DICE_RESULT:
			this.ui.update_accessibleWidgets("lblDiceResult", Integer.toString((int)((Packet.DiceResult) packet.data).getDiceResult()));
			break;	
		default:
			System.err.println("Not yet implemented Command: " + packet.getCommand());
		}
	}
	
	public void update_widget_text(String name, String text) {
		this.ui.update_accessibleWidgets(name, text);
	}

	// messages from the clients
	public abstract void message_to_core(Packet packet);
}