package local;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import core.LocalCore;
import core.Player;
import data.Language;
import data.Resource;
import local.LocalState.GameMode;
import local.gui.Button;
import local.gui.ColorPicker;
import local.gui.Label;
import local.gui.TextField;
import local.gui.Widget;
import superClasses.Core;
import superClasses.UI;

public class LocalUI extends UI {
	enum GUIMode {
		LOBBY, JOIN, GUEST_LOBBY, HOST_LOBBY, GAME,
	}

	private GUIMode mode;

	private Core core;
	// local state
	private LocalState state;
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
	private Button btnFinishedMove;
	private Button btnBuildVillage;
	private Button btnBuildCity;
	private Button btnBuildStreet;
	private Label lblDiceResult;
	private Label lblWoodCards;
	private Label lblWoolCards;
	private Label lblGrainCards;
	private Label lblClayCards;
	private Label lblOreCards;
	private Label lblInfo;

	private String tf_value_ip = "127.0.0.1";
	private String tf_value_name = "Julian";
	private String tf_value_seed = "" + (int) (Math.random() * Integer.MAX_VALUE);
	private String tf_value_size = "5";
	private String lbl_value_info = "";
	private String lbl_value_dice = "0";
	private float color_pkr_hue = (float) Math.random();

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

	public void setCore(Core core) {
		this.core = core;
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
		float mm_button_spacing = 10;

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

		// rearrange buttons
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
		for (int i = 0; i < state.player_data.size(); i++) {
			Label lblPlayerScore = new Label(
					state.player_data.get(i).getName() + ": " + state.player_data.get(i).getScore(),
					new FloatRect(window_size.x - 250, 50 * i, 250, 50));
			lblPlayerScore.set_fill_color(state.player_data.get(i).getColor());
			widgets.add(lblPlayerScore);
		}

		//player resources
		int pos_count = 1;
		float cards_width = 100;
		float orientation_anchor = (window_size.x / 5) * 3;
		Widget.set_default_outline_color(Color.WHITE);
		lblClayCards = new Label(Language.CLAY.get_text() + "\n" + state.my_player_data.get_resources(Resource.CLAY),
				new FloatRect(orientation_anchor - (cards_width + 10) * pos_count++, window_size.y - 85, cards_width,
						80));
		lblClayCards.set_fill_color(Resource.CLAY.get_color());
		widgets.add(lblClayCards);
		lblGrainCards = new Label(Language.GRAIN.get_text() + "\n" + state.my_player_data.get_resources(Resource.GRAIN),
				new FloatRect(orientation_anchor - (cards_width + 10) * pos_count++, window_size.y - 85, cards_width,
						80));
		lblGrainCards.set_fill_color(Resource.GRAIN.get_color());
		widgets.add(lblGrainCards);
		lblOreCards = new Label(Language.ORE.get_text() + "\n" + state.my_player_data.get_resources(Resource.ORE),
				new FloatRect(orientation_anchor - (cards_width + 10) * pos_count++, window_size.y - 85, cards_width,
						80));
		lblOreCards.set_fill_color(Resource.ORE.get_color());
		widgets.add(lblOreCards);
		lblWoodCards = new Label(Language.WOOD.get_text() + "\n" + state.my_player_data.get_resources(Resource.WOOD),
				new FloatRect(orientation_anchor - (cards_width + 10) * pos_count++, window_size.y - 85, cards_width,
						80));
		lblWoodCards.set_fill_color(Resource.WOOD.get_color());
		widgets.add(lblWoodCards);
		lblWoolCards = new Label(Language.WOOL.get_text() + "\n" + state.my_player_data.get_resources(Resource.WOOL),
				new FloatRect(orientation_anchor - (cards_width + 10) * pos_count++, window_size.y - 85, cards_width,
						80));
		lblWoolCards.set_fill_color(Resource.WOOL.get_color());
		widgets.add(lblWoolCards);
		Widget.set_default_outline_color(Color.TRANSPARENT);

		// finished move button
		btnFinishedMove = new Button(Language.FINISHED_MOVE.get_text(),
				new FloatRect(window_size.x - 155, window_size.y - 130, 150, 70));
		btnFinishedMove.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.nextTurn(id);
			}
		});
		widgets.add(btnFinishedMove);

		//build menu
		pos_count = 0;
		float buttons_width = 110;
		btnBuildVillage = new Button(Language.BUILD_VILLAGE.get_text(), new FloatRect(
				orientation_anchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width, 70));
		btnBuildVillage.set_click_callback(new Runnable() {
			@Override
			public void run() {
				state.curr_action = LocalState.Action.build_village;
				show_informative_hint(Language.SELECT_BUILD_PLACE, "");
			}
		});
		widgets.add(btnBuildVillage);
		btnBuildCity = new Button(Language.BUILD_CITY.get_text(), new FloatRect(
				orientation_anchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width, 70));
		btnBuildCity.set_click_callback(new Runnable() {
			@Override
			public void run() {
				state.curr_action = LocalState.Action.build_city;
				show_informative_hint(Language.SELECT_BUILD_PLACE, "");
			}
		});
		widgets.add(btnBuildCity);
		btnBuildStreet = new Button(Language.BUILD_STREET.get_text(), new FloatRect(
				orientation_anchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width, 70));
		btnBuildStreet.set_click_callback(new Runnable() {
			@Override
			public void run() {
				state.curr_action = LocalState.Action.build_street;
				show_informative_hint(Language.SELECT_BUILD_PLACE, "");
			}
		});
		widgets.add(btnBuildStreet);

		//dice result
		lblDiceResult = new Label(lbl_value_dice, new FloatRect(10, 10, 50, 50));
		lblDiceResult.set_fill_color(new Color(170, 170, 170));
		widgets.add(lblDiceResult);

		// info label
		lblInfo = new Label(lbl_value_info, new FloatRect(10, window_size.y - 50, 100, 50));
		widgets.add(lblInfo);
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

		Label lblConnecting = new Label("Try to Connect to: " + tfIp.get_text(),
				new FloatRect(window_size.x / 2, window_size.y - 200, 100, 50));
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

		Label lbl = new Label("Successfully joined Lobby", new FloatRect(0, 0, 100, 100));
		widgets.add(lbl);
	}

	public void build_host_lobby_window() {
		destroy_widgets();
		mode = GUIMode.HOST_LOBBY;

		float column0 = 0;
		float column1 = window_size.x / 2 > 300 ? window_size.x / 2 : 300;
		float height_anchor = 10;
		float textfield_width = 200;
		float textfield_height = 50;
		float row_count = 0;

		Label lbl;
		//Row0 ==> Settings
		lbl = new Label(Language.SETTINGS.get_text() + ": ", new FloatRect(column0,
				height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
		widgets.add(lbl);
		row_count++;
		lbl = new Label(Language.MAP_SIZE.get_text() + ": ", new FloatRect(column0,
				height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
		widgets.add(lbl);
		lbl = new Label(Language.SEED.get_text() + ": ", new FloatRect(column0,
				height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
		widgets.add(lbl);
		lbl = new Label(Language.YOUR_NAME.get_text() + ": ", new FloatRect(column0,
				height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
		widgets.add(lbl);
		lbl = new Label(Language.YOUR_COLOR.get_text() + ": ", new FloatRect(column0,
				height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
		widgets.add(lbl);

		row_count = 2;
		TextField tfMapSize = new TextField(new FloatRect(column0 + 200,
				height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
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

		TextField tfSeed = new TextField(new FloatRect(column0 + 200,
				height_anchor + (textfield_height + 10) * row_count, textfield_width, textfield_height));
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
		Button btnRandom = new Button(Language.RANDOM.get_text(), new FloatRect(column0 + 205 + textfield_width,
				height_anchor + (textfield_height + 10) * row_count++, 100, textfield_height));
		btnRandom.set_click_callback(new Runnable() {
			@Override
			public void run() {
				tf_value_seed = "" + (int)(Math.random() * Integer.MAX_VALUE);
				tfSeed.set_text(tf_value_seed);
			}
		});
		widgets.add(btnRandom);

		TextField tfName = new TextField(new FloatRect(column0 + 200,
				height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
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

		ColorPicker colorPicker = new ColorPicker(new FloatRect(column0 + 200,
				height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
		colorPicker.set_color(color_pkr_hue, 1.f, 0.8f);
		colorPicker.set_select_callback(new Runnable() {
			ColorPicker cp = colorPicker;

			@Override
			public void run() {
				color_pkr_hue = cp.get_hue();
			}
		});
		widgets.add(colorPicker);

		//Row1 ==> members
		lbl = new Label(Language.MEMBERS.get_text(), new FloatRect(column1, 10, 100, 100));
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
				int map_size = tf_value_size.length() > 0 ? Integer.parseInt(tf_value_size) : 5;
				int seed = tf_value_seed.length() > 0 ? Integer.parseInt(tf_value_seed)
						: ((int) Math.random() * 100) + 1;
				String user_name = tf_value_name.length() > 0 ? tf_value_name : "Anonymous";
				Color user_color = colorPicker.get_color();

				((LocalCore) core).changePlayerProps(0, user_name, user_color);
				((LocalCore) core).create_new_map(map_size, seed);
				((LocalCore) core).init_game();
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
			} else if (evt.asKeyEvent().key == Key.ESCAPE) {
				if (state.curr_action != LocalState.Action.idle) {
					switch_to_idle();
					return true;
				} else
					return false;
			} else
				return false;
		} else
			return false;
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
				widget.do_mouse_click(cursor_position);
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

	// mode switching functions
	public void switch_to_idle() {
		state.curr_action = LocalState.Action.idle;
		if (state.curr_player.equals(state.my_player_data.getName())) {
			show_informative_hint(Language.DO_MOVE, "");
			rebuild_gui();
		} else {
			show_informative_hint(Language.OTHERS_MOVE, state.curr_player);
			btnFinishedMove.set_enabled(false);
			btnBuildCity.set_enabled(false);
			btnBuildStreet.set_enabled(false);
			btnBuildVillage.set_enabled(false);
		}
	}

	// change the gui layout

	@Override
	public void show_guest_at_lobby(String name) {
		guests.add(guests.size(), name);
		rebuild_gui();
	}

	//Access to the widgets

	@Override
	public void update_scoreboard(List<LocalPlayer> player) {
		state.player_data = player;
		rebuild_gui();// TODO only rebuild at begin
	}

	@Override
	public void show_informative_hint(Language text, String replacementStr) {
		if (!replacementStr.isEmpty())
			lbl_value_info = text.get_text(replacementStr);
		else
			lbl_value_info = text.get_text();
		lblInfo.set_text(lbl_value_info);
	}

	@Override
	public void show_dice_result(byte result) {
		lbl_value_dice = Integer.toString((int) result);
		lblDiceResult.set_text(lbl_value_dice);
	}

	@Override
	public void set_current_player(String player) {
		state.curr_player = player;
		switch_to_idle();
	}

	@Override
	public void update_player_data(Player player) {
		state.my_player_data = player;
		if (lblClayCards != null) {
			lblWoodCards.set_text(Language.WOOD.get_text() + "\n" + player.get_resources(Resource.WOOD));
			lblWoolCards.set_text(Language.WOOL.get_text() + "\n" + player.get_resources(Resource.WOOL));
			lblGrainCards.set_text(Language.GRAIN.get_text() + "\n" + player.get_resources(Resource.GRAIN));
			lblClayCards.set_text(Language.CLAY.get_text() + "\n" + player.get_resources(Resource.CLAY));
			lblOreCards.set_text(Language.ORE.get_text() + "\n" + player.get_resources(Resource.ORE));
		}
	}

}
