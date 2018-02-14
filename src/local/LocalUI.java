package local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import core.Player;
import core.LocalCore;
import data.Language;
import data.Resource;
import local.LocalState.GameMode;
import local.gui.Button;
import local.gui.Label;
import local.gui.TextField;
import local.gui.Widget;
import network.Command;
import network.Networkmanager;
import network.Server;
import network.Packet;
import superClasses.Core;
import superClasses.UI;

public class LocalUI extends UI{
	enum GUIMode {
		LOBBY, JOIN, GUEST_LOBBY, HOST_LOBBY, GAME,
	}

	private GUIMode mode;
	
	private Core core;
	// local state
	private LocalState state;
	private Networkmanager data_connection;
	private Framework framework;
	private Vector2f window_size;
	private View view;

	// fonts
	private Font std_font;

	// gui data
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	private TextField activeTF;

	// lobby
	private List<String> guests = new ArrayList<String>();

	//widgets Just widgets which may be changed
	private Button btnFinishedTurn;
	private Label lblDiceResult;
	private Label lblWoodCards;
	private Label lblWoolCards;
	private Label lblGrainCards;
	private Label lblClayCards;
	private Label lblOreCards;

	private List<LocalPlayer> player_data = new ArrayList<LocalPlayer>();
	private String tf_value_ip = "192.168.2.103";
	private String tf_value_name = "Julian";
	private String tf_value_seed = "";
	private String tf_value_size = "";

	LocalUI(LocalGameLogic logic, Framework framework) {
		this.state = logic.state;
		this.framework = framework;

		state.mode = GameMode.main_menu;
	}

	void init(Font std_font) {
		this.std_font = std_font;

		// Is global for all Widgets! Change them on demand
		Widget.set_default_font(std_font);
		Widget.set_default_text_color(new Color(20, 50, 50));
		Widget.set_default_outline_color(Color.TRANSPARENT);
		Widget.set_default_outline_highlight_color(new Color(200, 140, 200));
		Widget.set_default_fill_color(new Color(0, 0, 0, 0));
		build_lobby();
	}

	void set_data_interface(Networkmanager data_connection) {
		this.data_connection = data_connection;
	}

	public void destroy_widgets() {
		widgets.clear();
		activeTF = null;
	}

	// this method is called when the window gets resized
	public void rebuild_gui() {
		destroy_widgets();
		if (mode == GUIMode.LOBBY) {
			build_lobby();
		} else if (mode == GUIMode.JOIN) {
			build_join_menu();
		} else if (mode == GUIMode.GUEST_LOBBY) {
			build_guest_lobby_window();
		} else if (mode == GUIMode.HOST_LOBBY) {
			build_host_lobby_window();
		} else if (mode == GUIMode.GAME) {
			build_game_menu();
		}
	}

	public void build_lobby() {
		destroy_widgets();
		mode = GUIMode.LOBBY;

		float mm_button_width = 400;
		float mm_button_height = 100;
		float mm_button_spacing = 20;

		Button btn = new Button(Language.CREATE_NEW_GAME.get_text(),
				new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Start new game");
				framework.init_host_game();
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
				framework.running = false;
			}
		});
		widgets.add(btn);

		// rearrange buttons TODO
		for (int i = 0; i < widgets.size(); i++) {
			Button button = (Button) widgets.get(i);
			button.set_position(new Vector2f((window_size.x - mm_button_width) * 0.5f,
					(window_size.y - (mm_button_height + mm_button_spacing) * widgets.size()) * 0.5f
							+ (mm_button_height + mm_button_spacing) * i));
		}

	}

	public void build_game_menu() {
		destroy_widgets();
		mode = GUIMode.GAME;

		//Score board
		Label lblScoreBoard = new Label("", new FloatRect(window_size.x - 250, 0, 250, 300));
		widgets.add(lblScoreBoard);
		for (int i = 0; i < player_data.size(); i++) {
			Label lblPlayerScore = new Label(player_data.get(i).getName() + ": " + player_data.get(i).getScore(),
					new FloatRect(window_size.x - 250, 50 * i, 250, 50));
			widgets.add(lblPlayerScore);
		}
		//player resources
		lblClayCards = new Label("Clay", new FloatRect((window_size.x / 5) * 3 - 70, window_size.y - 95, 70, 90));
		lblClayCards.set_fill_color(Resource.CLAY.get_color());
		widgets.add(lblClayCards);
		lblGrainCards = new Label("Grain", new FloatRect((window_size.x / 5) * 3 - 140, window_size.y - 95, 70, 90));
		lblGrainCards.set_fill_color(Resource.GRAIN.get_color());
		widgets.add(lblGrainCards);
		lblOreCards = new Label("Ore", new FloatRect((window_size.x / 5) * 3 - 210, window_size.y - 95, 70, 90));
		lblOreCards.set_fill_color(Resource.ORE.get_color());
		widgets.add(lblOreCards);
		lblWoodCards = new Label("WOOD", new FloatRect((window_size.x / 5) * 3 - 280, window_size.y - 95, 70, 90));
		lblWoodCards.set_fill_color(Resource.WOOD.get_color());
		widgets.add(lblWoodCards);
		lblWoolCards = new Label("Wool", new FloatRect((window_size.x / 5) * 3 - 350, window_size.y - 95, 70, 90));
		lblWoolCards.set_fill_color(Resource.WOOL.get_color());
		widgets.add(lblWoolCards);
		//dice
		btnFinishedTurn = new Button(Language.FINISHED_TURN.get_text(),
				new FloatRect(window_size.x - 100, window_size.y - 130, 100, 70));
		btnFinishedTurn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.nextTurn(id);
			}
		});
		//btnDice.set_enabled(false);
		widgets.add(btnFinishedTurn);
		//dice result
		lblDiceResult = new Label("-1", new FloatRect(10, 10, 50, 50));
		lblDiceResult.set_fill_color(new Color(170, 170, 170));
		widgets.add(lblDiceResult);

		//build menu
		Button btnBuildVillage = new Button(Language.BUILD_VILLAGE.get_text(),
				new FloatRect((window_size.x / 5) * 3, window_size.y - 80, 110, 70));
		btnBuildVillage.set_click_callback(new Runnable() {
			@Override
			public void run() {

			}
		});
		widgets.add(btnBuildVillage);
		Button btnBuildCity = new Button(Language.BUILD_CITY.get_text(),
				new FloatRect((window_size.x / 5) * 3 + 120, window_size.y - 80, 110, 70));
		btnBuildCity.set_click_callback(new Runnable() {
			@Override
			public void run() {

			}
		});
		widgets.add(btnBuildCity);
		Button btnBuildStreet = new Button(Language.BUILD_STREET.get_text(),
				new FloatRect((window_size.x / 5) * 3 + 240, window_size.y - 80, 110, 70));
		btnBuildStreet.set_click_callback(new Runnable() {
			@Override
			public void run() {

			}
		});
		widgets.add(btnBuildStreet);
	}

	public void build_join_menu() {
		destroy_widgets();
		mode = GUIMode.JOIN;

		float mm_tf_width = 400;
		float mm_tf_height = 50;
		float mm_tf_spacing = 20;

		TextField tfIp = new TextField(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		tfIp.set_text_size(30);
		tfIp.set_text(tf_value_ip);
		tfIp.set_input_callback(new Runnable() {
			TextField textField = tfIp;

			@Override
			public void run() {
				tf_value_ip = textField.get_text();
			}
		});
		widgets.add(tfIp);

		TextField tfName = new TextField(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		tfName.set_text_size(30);
		tfName.set_text(tf_value_name);
		tfName.set_input_callback(new Runnable() {
			TextField textField = tfName;

			@Override
			public void run() {
				tf_value_name = textField.get_text();
			}
		});
		widgets.add(tfName);

		for (int i = 0; i < widgets.size(); i++) {
			TextField temp_tf = (TextField) widgets.get(i);
			temp_tf.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f + 200,
					(window_size.y - (mm_tf_height + mm_tf_spacing) * widgets.size()) * 0.5f
							+ (mm_tf_height + mm_tf_spacing) * i));
		}

		Label lbl = new Label("Enter IP: ", new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		lbl.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f,
				(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f + (mm_tf_height + mm_tf_spacing) * 0));
		widgets.add(lbl);
		lbl = new Label("Enter Name: ", new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		lbl.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f,
				(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f + (mm_tf_height + mm_tf_spacing) * 1));
		widgets.add(lbl);
		
		Label lblConnecting = new Label("Try to Connect to: " + tfIp.get_text(), new FloatRect(window_size.x/2, window_size.y - 200, 100, 50));
		lblConnecting.set_visible(false);
		widgets.add(lblConnecting);
		Button btn = new Button(Language.JOIN.get_text(), new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		btn.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f + 200,
				(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f + (mm_tf_height + mm_tf_spacing) * 2));
		btn.set_fill_color(new Color(60, 255, 60, 100));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				lblConnecting.set_visible(true);
				if (tf_value_ip.length() > 4 && tf_value_name.length() > 0) {
					//Entered wrong Ip or server is not online
					new Thread(new Runnable() {
						public void run() {
							if (!framework.init_guest_game(tf_value_ip.trim(), tf_value_name.trim())) {
								System.out.println("Not accepted");
								tfIp.set_outline_color(Color.RED);
								lblConnecting.set_text("Entered wrong IP or the server is not online");
							}
						}
					}).start();
				} else {
					if (tf_value_ip.length() <= 4) {
						tfIp.set_outline_color(Color.RED);
					}
					if (tf_value_name.length() == 0) {
						tfName.set_outline_color(Color.RED);
					}
				}
			}

		});
		widgets.add(btn);
	}

	public void build_guest_lobby_window() {
		destroy_widgets();
		mode = GUIMode.GUEST_LOBBY;

		Label lbl = new Label("Succesfully joined Lobby", new FloatRect(0, 0, 100, 100));
		widgets.add(lbl);
	}

	public void build_host_lobby_window() {
		destroy_widgets();
		mode = GUIMode.HOST_LOBBY;

		float row0 = 0;
		float row1 = window_size.x / 2 > 200 ? window_size.x / 2 : 200;

		Label lbl;
		//Row0 ==> Settings
		lbl = new Label(Language.SETTINGS.get_text() + ": ", new FloatRect(row0, 10, 100, 100));
		widgets.add(lbl);
		lbl = new Label(Language.MAP_SIZE.get_text() + ": ", new FloatRect(row0, 110, 100, 35));
		widgets.add(lbl);
		TextField tfMapSize = new TextField(new FloatRect(row0 + 120, 110, 200, 35));
		tfMapSize.set_text_color(new Color(20, 20, 20));
		tfMapSize.set_text(tf_value_size);
		tfMapSize.set_input_callback(new Runnable() {
			TextField textField = tfMapSize;

			@Override
			public void run() {
				tf_value_size = textField.get_text();
			}
		});
		widgets.add(tfMapSize);

		lbl = new Label(Language.SEED.get_text() + ": ", new FloatRect(row0, 150, 100, 35));
		widgets.add(lbl);
		TextField tfSeed = new TextField(new FloatRect(row0 + 120, 150, 200, 35));
		tfSeed.set_text_color(new Color(20, 20, 20));
		tfSeed.set_text(tf_value_seed);
		tfSeed.set_input_callback(new Runnable() {
			TextField textField = tfSeed;

			@Override
			public void run() {
				tf_value_seed = textField.get_text();
			}
		});
		widgets.add(tfSeed);

		lbl = new Label(Language.YOUR_NAME.get_text() + ": ", new FloatRect(row0, 290, 100, 35));
		widgets.add(lbl);
		TextField tfName = new TextField(new FloatRect(row0 + 120, 290, 200, 35));
		tfName.set_text_color(new Color(20, 20, 20));
		tfName.set_text(tf_value_name);
		tfName.set_input_callback(new Runnable() {
			TextField textField = tfName;

			@Override
			public void run() {
				tf_value_name = textField.get_text();
			}
		});
		widgets.add(tfName);

		//Row1 ==> members
		lbl = new Label(Language.MEMBERS.get_text(), new FloatRect(row1, 10, 100, 100));
		widgets.add(lbl);

		for (int i = 0; i < guests.size(); i++) {
			lbl = new Label(guests.get(i),
					new FloatRect(view.getSize().x / 2 > 200 ? view.getSize().x / 2 : 200, 200 + 110 * i, 400, 100));
			lbl.set_fill_color(new Color(100, 100, 100, 90));
			widgets.add(lbl);
		}

		Button btnStart = new Button(Language.START.get_text(),
				new FloatRect(view.getSize().x - 300, view.getSize().y - 200, 200, 100));
		btnStart.set_click_callback(new Runnable() {
			@Override
			public void run() {
				int map_size = tf_value_size.length() > 0 ? Integer.parseInt(tf_value_size) : 5; //TODO get from TF
				int seed = tf_value_seed.length() > 0 ? Integer.parseInt(tf_value_seed)
						: ((int) Math.random() * 100) + 1;
				String user_name = tf_value_name.length() > 0 ? tf_value_name : "Anonymous";
				((LocalCore)core).changePlayerName(0, user_name);
				((LocalCore)core).create_new_map(map_size, seed);
				((LocalCore)core).init_game();
			}
		});
		widgets.add(btnStart);
	}

	// returns true if event was handled
	boolean handle_event(Event evt) {
		if (evt.type == Event.Type.MOUSE_BUTTON_PRESSED) {
			if (evt.asMouseButtonEvent().button == Mouse.Button.LEFT) { // reset mouse position
				return check_on_click_widgets(framework.reverse_transform_position(evt.asMouseButtonEvent().position.x,
						evt.asMouseButtonEvent().position.y, view));
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

	public void update_window_size(Vector2f size, View view) {
		window_size = size;
		this.view = view;
		rebuild_gui();
	}

	// change the gui layout

	public void show_guest_at_lobby(String name) {
		guests.add(guests.size(), name);
		rebuild_gui();
	}

	public void init_scoreboard(List<LocalPlayer> player) {
		player_data = player;
		rebuild_gui();
	}

	@Override
	public void show_dice_result(int diceResult) {
		lblDiceResult.set_text(Integer.toString(diceResult));
	}
	
	public void setCore(Core core) {
		this.core = core;
	}
}
