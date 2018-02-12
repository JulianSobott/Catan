package local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import local.LocalState.GameMode;
import network.Command;
import network.DataIfc;
import network.LocalDataServer;
import network.Packet;
import network.RemoteDataClient;

public class UI {
	// local state
	private LocalState state;
	private LocalLogic logic;
	private DataIfc data_connection;
	private Game game;
	private Vector2f window_size;
	
	// fonts
	Font std_font;

	// gui data
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	private Map<String, Widget> accessibleWidgets = new HashMap<String, Widget>();
	private TextField activeTF;

	// lobby
	private int numGuests = 0;

	UI(LocalLogic logic, Game game) {
		this.logic = logic;
		this.state = logic.state;
		this.game = game;

		state.mode = GameMode.main_menu;
	}

	void init(Font std_font) {
		this.std_font = std_font;

		// Is global for all Widgets! Change them on demand
		Widget.set_default_font(std_font);
		Widget.set_default_text_color(new Color(20, 50, 50));
		Widget.set_default_outline_color(Color.BLACK);
		Widget.set_default_outline_highlight_color(new Color(200, 140, 200));
		Widget.set_default_fill_color(new Color(0,0,0,0));
		Label.set_default_outline_color(new Color(0,0,0,0));
		build_lobby();
	}

	void set_data_interface(DataIfc data_connection) {
		this.data_connection = data_connection;
	}

	public void destroy_widgets() {
		widgets.clear();
		activeTF = null;
	}

	public void build_lobby() {
		destroy_widgets();

		float mm_button_width = 400;
		float mm_button_height = 100;
		float mm_button_spacing = 20;

		Button btn = new Button(Language.CREATE_NEW_GAME.get_text(),
				new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Start new game");
				game.init_host_game();
				build_host_lobby_window();
			}
		});
		widgets.add(btn);

		btn = new Button(Language.JOIN_GAME.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Join Game");
				build_join_menu();
			}
		});
		widgets.add(btn);

		btn = new Button(Language.LOAD_GAME.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				
			}
		});
		btn.set_enabled(false); //TODO remove when implemented
		widgets.add(btn);

		btn = new Button(Language.OPTIONS.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Options");
			}
		});
		btn.set_enabled(false); //TODO remove when implemented
		widgets.add(btn);

		btn = new Button(Language.EXIT.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Exit game");
			}
		});
		btn.set_enabled(false); //TODO remove when implemented
		widgets.add(btn);
		
		

		// rearrange buttons TODO
		for (int i = 0; i < widgets.size(); i++) {
			Button button = (Button) widgets.get(i);
			button.set_position(new Vector2f((window_size.x - mm_button_width) * 0.5f,
					(window_size.y - (mm_button_height + mm_button_spacing) * widgets.size()) * 0.5f
							+ (mm_button_height + mm_button_spacing) * i));
		}

	}
	
	public void update_accessibleWidgets(String name, String newText) {
		accessibleWidgets.get(name).set_text(newText);
	}
	public void build_game_menu() {
		destroy_widgets();
		//Score board
		
		//player resources
		
		//dice 
		Button btnDice = new Button(Language.DICE.get_text(), new FloatRect(window_size.x - 100 ,window_size.y - 130 ,100,70));
		btnDice.set_click_callback(new Runnable() {
			@Override
			public void run() {
				data_connection.message_to_core(new Packet(Command.DICE));
			}	
		});
		btnDice.set_enabled(false);
		accessibleWidgets.put("btnDice", btnDice);
		//dice result
		Label lblDiceResult = new Label("-1", new FloatRect(10,10,50,50));
		lblDiceResult.set_fill_color(new Color(170, 170, 170));
		accessibleWidgets.put("lblDiceResult", lblDiceResult);
		
		//build menu
	}
	
	public void build_join_menu() {
		destroy_widgets();
		
		float mm_tf_width = 400;
		float mm_tf_height = 50;
		float mm_tf_spacing = 20;
		
		TextField tfIp = new TextField(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		tfIp.set_text_size(30);
		tfIp.set_text("192.168.2.103");
		widgets.add(tfIp);
		
		TextField tfName = new TextField(new FloatRect(0,0, mm_tf_width, mm_tf_height));
		tfName.set_text_size(30);
		tfName.set_text("Julian");
		widgets.add(tfName);
		
		for (int i = 0; i < widgets.size(); i++) {
			TextField temp_tf = (TextField) widgets.get(i);
			temp_tf.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f + 200,
					(window_size.y - (mm_tf_height + mm_tf_spacing) * widgets.size()) * 0.5f
							+ (mm_tf_height + mm_tf_spacing) * i));
		}
		
		
		Label lbl = new Label("Enter IP: ", new FloatRect(0,0, mm_tf_width, mm_tf_height));
		lbl.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f,
					(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f
							+ (mm_tf_height + mm_tf_spacing) * 0));
		widgets.add(lbl);
		lbl = new Label("Enter Name: ", new FloatRect(0,0, mm_tf_width, mm_tf_height));
		lbl.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f,
				(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f
						+ (mm_tf_height + mm_tf_spacing) * 1));
		widgets.add(lbl);
		
		Button btn = new Button(Language.JOIN.get_text(), new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		btn.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f + 200,
				(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f
						+ (mm_tf_height + mm_tf_spacing) * 2));
		btn.set_fill_color(new Color(60, 255, 60, 100));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				if(tfIp.get_text().length() > 4 && tfName.get_text().length() > 0) {
					//Entered wrong Ip or server is not online
					if(!game.init_guest_game(tfIp.get_text().trim(), tfName.get_text().trim())) {
						tfIp.set_outline_color(Color.RED);
					}
				}else {
					if(tfIp.get_text().length() <= 4) {
						tfIp.set_outline_color(Color.RED);
					}
					if(tfName.get_text().length() == 0) {
						tfName.set_outline_color(Color.RED);
					}
				}
			}
			
		});
		widgets.add(btn);
	}
	
	public void build_guest_lobby_window() {
		destroy_widgets();
		Label lbl = new Label("Succesfully joined Lobby", new FloatRect(0,0,100,100));
		widgets.add(lbl);
	}
	
	public void build_host_lobby_window() {
		destroy_widgets();
		float row0 = 0;
		float row1 = window_size.x/2 > 200 ? window_size.x/2 : 200 ;
		
		Label lbl;
		//Row0 ==> Settings
		lbl = new Label(Language.SETTINGS.get_text()+": ", new FloatRect(row0, 10, 100, 100));
		widgets.add(lbl);
		lbl = new Label(Language.MAP_SIZE.get_text()+": ", new FloatRect(row0, 110, 100, 35));
		widgets.add(lbl);
		TextField tfMapSize = new TextField(new FloatRect(row0 + 100, 110, 200, 35));
		tfMapSize.set_text_color(new Color(20, 20, 20));
		widgets.add(tfMapSize);
		
		lbl = new Label(Language.SEED.get_text()+": ", new FloatRect(row0, 150, 100, 35));
		widgets.add(lbl);
		TextField tfSeed = new TextField(new FloatRect(row0 + 100, 150, 200, 35));
		tfSeed.set_text_color(new Color(20, 20, 20));
		widgets.add(tfSeed);
		
		//Row1 ==> members
		lbl = new Label(Language.MEMBERS.get_text(), new FloatRect(row1, 10, 100, 100));
		widgets.add(lbl);
		
		Button btnStart = new Button(Language.START.get_text(), new FloatRect(window_size.x - 300,window_size.y - 200,200,100));
		btnStart.set_click_callback(new Runnable() {
			@Override
			public void run() {
				int map_size = tfMapSize.get_text().length() > 0 ? Integer.parseInt(tfMapSize.get_text()) : 5; //TODO get from TF
				int seed = tfSeed.get_text().length() > 0 ? Integer.parseInt(tfSeed.get_text()) : ((int)Math.random()*100)+1; 
				((LocalDataServer) data_connection).create_new_map(map_size, seed); //TODO add settings from lobby
				((LocalDataServer) data_connection).messageToAll(new Packet(Command.START_GAME));
			}			
		});
		widgets.add(btnStart);
	}

	public void show_guest_at_lobby(String name) {
		Label lbl = new Label(name, new FloatRect( window_size.x/2 > 200 ? window_size.x/2 : 200, 200 + 110*numGuests, 400, 100));
		lbl.set_fill_color(new Color(100, 100, 100, 90));
		widgets.add(lbl);
		numGuests++;
	}

	// returns true if event was handled
	boolean handle_event(Event evt) {
		if (evt.type == Event.Type.MOUSE_BUTTON_PRESSED) {
			if (evt.asMouseButtonEvent().button == Mouse.Button.LEFT) { // reset mouse position
				return check_on_click_widgets(new Vector2f((float) evt.asMouseButtonEvent().position.x,
						(float) evt.asMouseButtonEvent().position.y));
			} else
				return false;
		} else if (evt.type == Event.Type.TEXT_ENTERED) {
			if (activeTF != null) {
				activeTF.text_input(evt.asTextEvent().character);
				return true;
			} else
				return false;
		} else if (evt.type == Event.Type.KEY_PRESSED) {
			if (activeTF != null) {
				return activeTF.special_input(evt.asKeyEvent().key);
			} else
				return false;
		} else
			return false;
	}

	void update() {

	}

	void render(RenderTarget target) {
		for (Widget widget : widgets) {
			widget.render(target);
		}
		for(Object widget : accessibleWidgets.values()) {
			((Widget) widget).render(target);
		}
	}

	// returns true if was on a widget
	private boolean check_on_click_widgets(Vector2f cursor_position) {
		boolean found_widget = false;
		for (Widget widget : widgets) {
			if (widget.contains_cursor(cursor_position)) {
				found_widget = true;
				if (activeTF != null) {
					activeTF.deactivate();
					activeTF = null;
				}

				if (widget instanceof TextField) {
					activeTF = (TextField) widget;
				}
				widget.do_mouse_click();
				break;
			}
		}
		if (found_widget) {
			return true;
		} else {
			if (activeTF != null) {
				activeTF.deactivate();
				activeTF = null;
			}
			return false;
		}
	}

	public void update_window_size(Vector2f size) {
		window_size = size;
	}

}