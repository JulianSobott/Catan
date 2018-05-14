package com.catangame.catan.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.jsfml.graphics.FloatRect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.catangame.catan.utils.BoxShadow;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.network.Packet;
import com.catangame.catan.core.Building;
import com.catangame.catan.core.LocalCore;
import com.catangame.catan.core.LocalFilehandler;
import com.catangame.catan.core.Map;
import com.catangame.catan.core.Player;
import com.catangame.catan.core.Map.GeneratorType;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.DevCardType;
import com.catangame.catan.data.Language;
import com.catangame.catan.data.Resource;
import com.catangame.catan.data.SavedGame;
import com.catangame.catan.local.LocalState.Action;
import com.catangame.catan.local.LocalState.GameMode;
import com.catangame.catan.local.TradeDemand.Vendor;
import com.catangame.catan.local.gui.Background;
import com.catangame.catan.local.gui.Button;
import com.catangame.catan.local.gui.Checkbox;
import com.catangame.catan.local.gui.ColorPicker;
import com.catangame.catan.local.gui.Container;
import com.catangame.catan.local.gui.Label;
import com.catangame.catan.local.gui.LobbyBackground;
import com.catangame.catan.local.gui.Message;
import com.catangame.catan.local.gui.MessageField;
import com.catangame.catan.local.gui.PopUp;
import com.catangame.catan.local.gui.ScrollContainer;
import com.catangame.catan.local.gui.TextField;
import com.catangame.catan.local.gui.Widget;
import com.catangame.catan.local.gui.Widget.Animation;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.superClasses.UI;
import com.catangame.catan.utils.FontMgr;
import com.catangame.catan.utils.TextureMgr;

public class LocalUI extends UI implements InputProcessor {
	enum GUIMode {
		LOBBY, JOIN, LOAD, MENU, GUEST_LOBBY, HOST_LOBBY, GAME, TRADE_DEMAND, TRADE_VENDOR, DEV_CARD, END_SCREEN, TO_MUCH_RESOURCES, STEEL_RESOURCE, JOINABLE_GAMES, CONNECTION_LOST;
	}

	//TODO Either implement this modes or delete this enum!!
	enum WindowMode {
		TRADE_VENDOR, TRADE_DEMANDER, SHOW_DEV_CARDS, DEV_SHOW_FREE_RESOURCES;
	}

	enum MenuMode {
		MENU, SAVE, LOAD, OPTIONS;
	}

	private MenuMode menuMode;
	private GUIMode mode;

	private Core core;
	// local state
	private LocalState state;
	private Framework framework;
	private Vector2 window_size;
	private OrthographicCamera camera;
	// fonts
	private BitmapFont std_font;
	public float scale = 2;

	// gui data
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	private TextField activeTF;
	private boolean showChatTf = false;
	private boolean showDevelopmentCards = false;
	private List<Message> messages = new LinkedList<Message>();
	private List<ScrollContainer> allScrollContainer = new ArrayList<ScrollContainer>();
	private String lostConnectionPlayerName = "";

	// lobby
	private SavedGame savedGame = null;
	private List<String> guests = new ArrayList<String>();
	private List<Player> allPossiblePlayer = new ArrayList<Player>();
	private int idxPlayer = 0;

	//widgets Just widgets which may be changed
	private Button btnFinishedMove;
	private Button btnBuildVillage;
	private Button btnBuildCity;
	private Button btnBuildStreet;
	private Button btnBuyDevelopmentCard;
	private Label lblDiceResult;
	private Label lblWoodCards;
	private Label lblWoolCards;
	private Label lblGrainCards;
	private Label lblClayCards;
	private Label lblOreCards;
	private Label lblInfo;
	private Button btnTrade;
	private java.util.Map<Resource, Widget> mapLblNumResources = new HashMap<Resource, Widget>();

	boolean buttonsEnabled = true;

	//Trading
	private TradeDemand tradeDemand;
	private TradeOffer tradeOffer;
	private List<TradeOffer> allTradeOffer = new ArrayList<TradeOffer>();

	//Lobby
	private String serverIP = "";
	private String tf_value_ip = "127.0.0.1";
	private String tf_value_name = "Anonymous";
	private String tf_value_seed = "" + (int) (Math.random() * Integer.MAX_VALUE);
	private String tf_value_size = "5";
	private String tf_value_random_houses = "1";
	private String tf_value_resource_houses = "1";
	private boolean cb_value_is_circle = false;
	private boolean cbValueIsLocal = true;
	private String lbl_value_info = "";
	private String lbl_value_dice = "0";
	private String tf_game_name = "";
	private float color_pkr_hue = (float) Math.random();
	private Color playerColor = Color.RED;
	private boolean onlineLobby = false;
	private String btnJoinText = Language.JOIN_GAME.get_text();


	//Menu
	List<SavedGame> allGames = null;
	List<Packet.JoinableGame> allJoinableGames;
	//End Screen
	private List<Player> player;

	public int scrolled = 0;
	LocalUI(LocalGameLogic logic, Framework framework) {
		this.state = logic.state;
		this.framework = framework;

		state.mode = GameMode.main_menu;
	}

	void init(BitmapFont std_font) {
		this.std_font = std_font;

		// Is global for all Widgets! Change them on demand
		Widget.set_default_font(std_font);
		Widget.set_default_back_color(Color.WHITE);
		Widget.set_default_text_color(new Color(0.08f, 0.2f, 0.2f, 1.f));
		Widget.set_default_outline_color(Color.TRANSPARENT);
		Widget.set_default_outline_highlight_color(new Color(0.1f, 0.1f, 0.9f, .9f));
		Widget.set_default_disabled_outline_color(new Color(0.3f, 0.3f, 0.3f, .9f));
		Widget.set_default_disabled_background_color(new Color(0.4f, 0.4f, 0.4f, 1.f));
		Widget.set_default_checkbox_color(Color.BLACK);
		build_lobby();
	}

	public void setCore(Core core) {
		this.core = core;
	}

	public void destroy_widgets() {
		widgets.clear();
		allScrollContainer.clear();
		activeTF = null;
	}

	// this method is called when the window gets resized
	public void rebuild_gui() {
		destroy_widgets();
		if(state.numToRemove > 0) {
			buildToMuchResourcesWindow();
		} else if (mode == GUIMode.LOBBY) {
			build_lobby();
		} else if (mode == GUIMode.JOIN) {
			build_join_menu();
		} else if (mode == GUIMode.GUEST_LOBBY) {
			build_guest_lobby_window();
		} else if (mode == GUIMode.HOST_LOBBY) {
			build_host_lobby_window();
		} else if (mode == GUIMode.GAME) {
			build_game_menu();
		} else if (mode == GUIMode.TRADE_DEMAND) {
			build__demander_trade_window();
		} else if (mode == GUIMode.TRADE_VENDOR) {
			build_vendor_trade_window();
		} else if(mode == GUIMode.DEV_CARD) {
			buildDevCardWindow();
		} else if(mode == GUIMode.TO_MUCH_RESOURCES) {
			buildToMuchResourcesWindow();
		} else if(mode == GUIMode.STEEL_RESOURCE) {
			buildSteelResource();
		} else if (mode == GUIMode.LOAD) {
			build_load_window();
		} else if (mode == GUIMode.MENU) {
			build_menu();
		} else if (mode == GUIMode.END_SCREEN) {
			buildEndScreen();
		} else if(mode == GUIMode.JOINABLE_GAMES) {
			buildAllJoinableGamesWindow();
		} else if(mode == GUIMode.CONNECTION_LOST){
			showConnectionLost(this.lostConnectionPlayerName);
		}


	}

	public void build_lobby() {
		destroy_widgets();

		mode = GUIMode.LOBBY;

		int mm_button_width = 400;
		int mm_button_height = 100;
		int mm_button_spacing = 10;

		Background background = new LobbyBackground(new Rectangle(0,0,window_size.x, window_size.y));
		widgets.add(background);

		Button btn = new Button(Language.CREATE_NEW_GAME.get_text(),
				new Rectangle(0, 0, mm_button_width, mm_button_height));
		btn.addHoverEffect1();
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				framework.init_host_game();
				//TODO Change to above when server is online
				//framework.initOnlineHostGame();
				build_host_lobby_window();
			}
		});
		widgets.add(btn);


		btn = new Button(Language.JOIN_GAME.get_text(), new Rectangle(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				build_join_menu();
			}
		});
		btn.addBoxShadow(new BoxShadow(new Color(126, 71, 20, 90), 0, 2, 2));
		btn.addHoverEffect1();
		widgets.add(btn);

		btn = new Button(Language.LOAD_GAME.get_text(), new Rectangle(0, 0, mm_button_width, mm_button_height));
		btn.addHoverEffect1();
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				framework.init_host_game();
				build_load_window();
			}
		});

		widgets.add(btn);

		btn = new Button(Language.OPTIONS.get_text(), new Rectangle(0, 0, mm_button_width, mm_button_height));
		btn.addHoverEffect1();
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Options");
			}
		});
		btn.set_enabled(false); //TODO remove when implemented
		widgets.add(btn);

		btn = new Button(Language.EXIT.get_text(), new Rectangle(0, 0, mm_button_width, mm_button_height));
		btn.addHoverEffect1();
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Exit game");
				Gdx.app.exit();
			}
		});
		widgets.add(btn);
		// rearrange buttons
		for (int i = 0; i < widgets.size(); i++) {
			if(widgets.get(i) instanceof Button) {
				Button button = (Button) widgets.get(i);
				button.set_position(new Vector2((window_size.x - mm_button_width) * 0.5f,
						(window_size.y - (mm_button_height + mm_button_spacing) * widgets.size()) * 0.5f
								+ (mm_button_height + mm_button_spacing) * i));
			}
		}

	}

	public void build_game_menu() {
		destroy_widgets();
		mode = GUIMode.GAME;

		//Score board
		for (int i = 0; i < state.player_data.size(); i++) {
			Label lblPlayerScore = new Label(
					state.player_data.get(i).getName() + ": " + state.player_data.get(i).getScore(),
					new Rectangle(window_size.x - 250, 50 * i, 250, 50));
			lblPlayerScore.set_fill_color(state.player_data.get(i).getColor());
			widgets.add(lblPlayerScore);
		}

		//player resources
		Label lblResource = null;
		int pos_count = 1;
		float cards_width = 70;
		final float orientationAnchor = (window_size.x / 2) - 200;
		Widget.set_default_outline_color(Color.WHITE);
		Iterator<Entry<Resource, Integer>> it = state.my_player_data.get_all_resources().entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			java.util.Map.Entry<Resource, Integer> pair = (java.util.Map.Entry<Resource, Integer>) it.next();
			Resource r = pair.getKey();
			int num = pair.getValue();
			lblResource = new Label(Integer.toString(num),
					new Rectangle(5, 200 + (cards_width + 5) * i, cards_width, cards_width));
			lblResource.set_fill_color(r.get_color());
			lblResource.centerText();
			lblResource.setTexture(TextureMgr.getTexture(r.name()));
			lblResource.set_text_color(new Color(20, 20, 30, 255));
			mapLblNumResources.put(r, lblResource);
			widgets.add(lblResource);
			i++;
		}
		Widget.set_default_outline_color(Color.TRANSPARENT);

		//Chat and information
		ScrollContainer sc = new ScrollContainer(this, new Rectangle(0, lblResource.get_position().y + lblResource.get_size().y + 10, 400, window_size.y -  lblResource.get_position().y - lblResource.get_size().y - 90));
		i = 0;
		int additionalMargin = 0;
		for(int j = messages.size()-1; j >= 0; j--) {
			Message msg = messages.get(j);

			MessageField dummymfMessage = new MessageField(msg, new Rectangle(0,0, 200, 200));
			MessageField mfMessage = new MessageField(msg, new Rectangle(5, window_size.y - dummymfMessage.getHeight() - additionalMargin - 90, 170, 200));
			sc.addWidget(mfMessage);
			additionalMargin += dummymfMessage.getHeight()+2;
			i++;
		}
		sc.calcBounds();
		allScrollContainer.add(sc);
		widgets.add(sc);
		final TextField tfChat = new TextField(new Rectangle(5, window_size.y - 70, 300, 20));
		tfChat.set_font(FontMgr.getFont(FontMgr.Type.ROBOTO_LIGHT, 16));
		tfChat.setEnterCallback(new Runnable() {
			@Override
			public void run() {
				if(showChatTf){
					if(!tfChat.get_text().trim().isEmpty()) {
						addNewMessage(new Message(state.player_data.get(id), tfChat.get_text()));
						core.newChatMessage(new Message(state.player_data.get(id), tfChat.get_text()));
					}
					showChatTf = false;
					widgets.get(7).setVisible(false);
					activeTF = null;
				}else {
					showChatTf = true;
					widgets.get(7).setVisible(true);
				}
			}
		});
		if(showChatTf) {
			tfChat.setVisible(true);
		}else {
			tfChat.setVisible(false);
		}
		widgets.add(tfChat);

		//player Development Cards
		Button btnShowDevelopmentCards = new Button(Language.DEVELOPMENT_CARD.get_text(),
				new Rectangle(5, 100, 220, 50));
		btnShowDevelopmentCards.adjustWidth(5);
		btnShowDevelopmentCards.set_enabled(buttonsEnabled);
		btnShowDevelopmentCards.adjustWidth(3f);
		btnShowDevelopmentCards.set_click_callback(new Runnable() {
			@Override
			public void run() {
				if (showDevelopmentCards)
					showDevelopmentCards = false;
				else
					showDevelopmentCards = true;
				rebuild_gui();
			}
		});
		widgets.add(btnShowDevelopmentCards);
		if (showDevelopmentCards) {
			i = 0;
			sc = new ScrollContainer(this, new Rectangle(215, 0, 300, window_size.y));
			for (final DevCard card : state.my_player_data.getDevelopmentCards()) {
				Button btnCard = new Button(Language.valueOf(card.type.toString()).get_text(), new Rectangle(215, 100 + 75 * i, 300, 70));
				btnCard.set_fill_color(new Color(0.2f, 0.3f, 0.67f, 0.9f));
				btnCard.set_click_callback(new Runnable() {

					@Override
					public void run() {
						showDevelopmentCards = false;
						showDevelopmentCardWindow(card);
					}
				});
				sc.addWidget(btnCard);
				i++;
			}
			allScrollContainer.add(sc);
			widgets.add(sc);
			sc.calcBounds();

		}

		// finished move button
		btnFinishedMove = new Button(Language.FINISHED_MOVE.get_text(),
				new Rectangle(window_size.x - 155, window_size.y - 155, 150, 70));
		btnFinishedMove.adjustWidth(5);
		btnFinishedMove.set_enabled(buttonsEnabled);
		btnFinishedMove.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.nextTurn(id);
			}
		});
		widgets.add(btnFinishedMove);

		//build menu
		pos_count = 0;
		final float buttons_width = 110;
		btnBuildVillage = new Button(Language.BUILD_VILLAGE.get_text(), new Rectangle(
				orientationAnchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width, 70));
		btnBuildVillage.set_enabled(buttonsEnabled);
		final Container cVillage = new Container(this, new Rectangle(orientationAnchor + (buttons_width + 5) * 0, window_size.y - 240, 200, 150));
		btnBuildVillage.addHover(new Runnable() {
			@Override
			public void run() {

				if(cVillage.widgets.size() == 0) {
					Label lblContainer = new Label("", new Rectangle(orientationAnchor + (buttons_width + 5) * 0, window_size.y - 240, 200, 150));
					lblContainer.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
					cVillage.addWidget(lblContainer);
					 java.util.Map<Resource, Integer> neededresources = Building.Type.VILLAGE.getNeededResources();
					 String str = "";
					 for(Resource r : neededresources.keySet()) {
						 str += Language.valueOf(r.toString()).get_text() + ": " + neededresources.get(r) + "\r\n";
					 }
					Label lblText = new Label(str, new Rectangle(orientationAnchor + (buttons_width + 5) * 0, window_size.y - 240, 200, 150));
					lblText.set_text_color(Color.WHITE);
					cVillage.addWidget(lblText);
				}
				java.util.Map<Resource, Integer> neededresources = Building.Type.VILLAGE.getNeededResources();
				for(Resource r : neededresources.keySet()) {
					((Label)(mapLblNumResources.get(r))).set_text((state.my_player_data.get_resources(r)-neededresources.get(r)) + "");
					((Label)(mapLblNumResources.get(r))).set_text_color(Color.RED);
				 }
				cVillage.visible = true;
			}
		}, new Runnable() {
			@Override
			public void run() {
				cVillage.visible = false;

				java.util.Map<Resource, Integer> neededresources = Building.Type.VILLAGE.getNeededResources();
				for(Resource r : neededresources.keySet()) {
					 ((Label)(mapLblNumResources.get(r))).set_text(state.my_player_data.get_resources(r) + "");
					 ((Label)(mapLblNumResources.get(r))).set_text_color(new Color(20, 20, 30, 255));
				 }
			}
		});
		widgets.add(cVillage);
		btnBuildVillage.set_click_callback(new Runnable() {
			@Override
			public void run() {
				if(state.curr_action != LocalState.Action.build_village) {
					state.curr_action = LocalState.Action.build_village;
					show_informative_hint(Language.SELECT_BUILD_PLACE, "");
					btnBuildVillage.set_fill_color(state.my_player_data.getColor());
					btnBuildStreet.set_fill_color(Widget.getDefaultFillColor());
					btnBuildCity.set_fill_color(Widget.getDefaultFillColor());
				}else {
					show_informative_hint(Language.DO_MOVE, "");
					state.curr_action = null;
					btnBuildVillage.set_fill_color(Widget.getDefaultFillColor());
				}
			}
		});
		widgets.add(btnBuildVillage);

		btnBuildCity = new Button(Language.BUILD_CITY.get_text(), new Rectangle(
				orientationAnchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width, 70));
		btnBuildCity.set_enabled(buttonsEnabled);
		final Container cCity = new Container(this, new Rectangle(orientationAnchor + (buttons_width + 5) * 1, window_size.y - 190, 200, 100));
		btnBuildCity.addHoverEffect1();
		btnBuildCity.addHover(new Runnable() {
			@Override
			public void run() {
				if(cCity.widgets.size() == 0) {
					Label lblContainer = new Label("", new Rectangle(orientationAnchor + (buttons_width + 5) * 1, window_size.y - 190, 200, 100));
					lblContainer.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
					cCity.addWidget(lblContainer);
					 java.util.Map<Resource, Integer> neededresources = Building.Type.CITY.getNeededResources();
					 String str = "";
					 for(Resource r : neededresources.keySet()) {
						 str += Language.valueOf(r.toString()).get_text() + ": " + neededresources.get(r) + "\r\n";
					 }
					Label lblText = new Label(str, new Rectangle(orientationAnchor + (buttons_width + 5) * 1, window_size.y - 190, 200, 100));
					lblText.set_text_color(Color.WHITE);
					cCity.addWidget(lblText);
				}
				cCity.visible = true;
				java.util.Map<Resource, Integer> neededresources = Building.Type.CITY.getNeededResources();
				for(Resource r : neededresources.keySet()) {
					((Label)(mapLblNumResources.get(r))).set_text((state.my_player_data.get_resources(r)-neededresources.get(r)) + "");
					((Label)(mapLblNumResources.get(r))).set_text_color(Color.RED);
				 }
			}
		}, new Runnable() {
			@Override
			public void run() {
				cCity.visible = false;
				java.util.Map<Resource, Integer> neededresources = Building.Type.CITY.getNeededResources();
				for(Resource r : neededresources.keySet()) {
					 ((Label)(mapLblNumResources.get(r))).set_text(state.my_player_data.get_resources(r) + "");
					 ((Label)(mapLblNumResources.get(r))).set_text_color(new Color(20, 20, 30, 255));
				 }
			}
		});
		widgets.add(cCity);
		btnBuildCity.set_click_callback(new Runnable() {
			@Override
			public void run() {
				if(state.curr_action != LocalState.Action.build_city) {
					state.curr_action = LocalState.Action.build_city;
					show_informative_hint(Language.SELECT_BUILD_PLACE, "");
					btnBuildCity.set_fill_color(state.my_player_data.getColor());
					btnBuildStreet.set_fill_color(Widget.getDefaultFillColor());
					btnBuildVillage.set_fill_color(Widget.getDefaultFillColor());
				}else {
					show_informative_hint(Language.DO_MOVE, "");
					state.curr_action = null;
					btnBuildCity.set_fill_color(Widget.getDefaultFillColor());
				}
			}
		});
		widgets.add(btnBuildCity);

		btnBuildStreet = new Button(Language.BUILD_STREET.get_text(), new Rectangle(
				orientationAnchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width, 70));
		btnBuildStreet.set_enabled(buttonsEnabled);
		final Container cStreet = new Container(this, new Rectangle(orientationAnchor + (buttons_width + 5) * 2, window_size.y - 190, 200, 100));
		btnBuildStreet.addHoverEffect1();
		btnBuildStreet.addHover(new Runnable() {
			@Override
			public void run() {
				if(cStreet.widgets.size() == 0) {
					Label lblContainer = new Label("", new Rectangle(orientationAnchor + (buttons_width + 5) * 2, window_size.y - 190, 200, 100));
					lblContainer.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
					cStreet.addWidget(lblContainer);
					 java.util.Map<Resource, Integer> neededresources = Building.Type.STREET.getNeededResources();
					 String str = "";
					 for(Resource r : neededresources.keySet()) {
						 str += Language.valueOf(r.toString()).get_text() + ": " + neededresources.get(r) + "\r\n";
					 }
					Label lblText = new Label(str, new Rectangle(orientationAnchor + (buttons_width + 5) * 2, window_size.y - 190, 200, 100));
					lblText.set_text_color(Color.WHITE);
					cStreet.addWidget(lblText);
				}
				cStreet.visible = true;
				java.util.Map<Resource, Integer> neededresources = Building.Type.STREET.getNeededResources();
				for(Resource r : neededresources.keySet()) {
					((Label)(mapLblNumResources.get(r))).set_text((state.my_player_data.get_resources(r)-neededresources.get(r)) + "");
					((Label)(mapLblNumResources.get(r))).set_text_color(Color.RED);
				 }
			}
		}, new Runnable() {
			@Override
			public void run() {
				cStreet.visible = false;
				java.util.Map<Resource, Integer> neededresources = Building.Type.STREET.getNeededResources();
				for(Resource r : neededresources.keySet()) {
					 ((Label)(mapLblNumResources.get(r))).set_text(state.my_player_data.get_resources(r) + "");
					 ((Label)(mapLblNumResources.get(r))).set_text_color(new Color(20, 20, 30, 255));
				 }
			}
		});
		widgets.add(cStreet);
		btnBuildStreet.set_click_callback(new Runnable() {
			@Override
			public void run() {
				if(state.curr_action != LocalState.Action.build_street) {
					state.curr_action = LocalState.Action.build_street;
					show_informative_hint(Language.SELECT_BUILD_PLACE, "");
					btnBuildStreet.set_fill_color(state.my_player_data.getColor());
					btnBuildVillage.set_fill_color(Widget.getDefaultFillColor());
					btnBuildCity.set_fill_color(Widget.getDefaultFillColor());
				}else {
					show_informative_hint(Language.DO_MOVE, "");
					state.curr_action = null;
					btnBuildStreet.set_fill_color(Widget.getDefaultFillColor());
				}
			}
		});
		widgets.add(btnBuildStreet);

		btnBuyDevelopmentCard = new Button(Language.DEVELOPMENT_CARD.get_text(), new Rectangle(
				orientationAnchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width + 100, 70));
		btnBuyDevelopmentCard.adjustWidth(5);
		btnBuyDevelopmentCard.set_enabled(buttonsEnabled);
		final Container cDevCard = new Container(this, new Rectangle(orientationAnchor + (buttons_width + 5) * 3, window_size.y - 240, 200, 150));
		btnBuyDevelopmentCard.addHover(new Runnable() {
			@Override
			public void run() {
				if(cDevCard.widgets.size() == 0) {
					Label lblContainer = new Label("", new Rectangle(orientationAnchor + (buttons_width + 5) * 3, window_size.y - 240, 200, 150));
					lblContainer.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
					cDevCard.addWidget(lblContainer);
					String str = Language.GRAIN.get_text() +": "+1 + "\r\n" + Language.ORE.get_text() +": "+1 + "\r\n" + Language.WOOL.get_text() +": "+1;
					Label lblText = new Label(str, new Rectangle(orientationAnchor + (buttons_width + 5) * 3, window_size.y - 240, 200, 150));
					lblText.set_text_color(Color.WHITE);
					cDevCard.addWidget(lblText);
				}
				cDevCard.visible = true;
				java.util.Map<Resource, Integer> neededresources = new HashMap<>();
				neededresources.put(Resource.GRAIN,	 1);
				neededresources.put(Resource.ORE,	 1);
				neededresources.put(Resource.WOOL,	 1);
				for(Resource r : neededresources.keySet()) {
					((Label)(mapLblNumResources.get(r))).set_text((state.my_player_data.get_resources(r)-neededresources.get(r)) + "");
					((Label)(mapLblNumResources.get(r))).set_text_color(Color.RED);
				 }
			}
		}, new Runnable() {
			@Override
			public void run() {
				cDevCard.visible = false;
				java.util.Map<Resource, Integer> neededresources = new HashMap<>();
				neededresources.put(Resource.GRAIN,	 1);
				neededresources.put(Resource.ORE,	 1);
				neededresources.put(Resource.WOOL,	 1);
				for(Resource r : neededresources.keySet()) {
					 ((Label)(mapLblNumResources.get(r))).set_text(state.my_player_data.get_resources(r) + "");
					 ((Label)(mapLblNumResources.get(r))).set_text_color(new Color(20, 20, 30, 255));
				 }
			}
		});
		widgets.add(cDevCard);
		btnBuyDevelopmentCard.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.buyDevelopmentCard(id);
			}
		});
		widgets.add(btnBuyDevelopmentCard);
		//dice result
		lblDiceResult = new Label(lbl_value_dice, new Rectangle(10, 10, 50, 50));
		lblDiceResult.set_fill_color(new Color(0.67f, 0.67f, 0.67f, 1.f));
		lblDiceResult.set_outline(Color.BLACK, 2);
		widgets.add(lblDiceResult);

		// info label
		lblInfo = new Label(lbl_value_info, new Rectangle(10, window_size.y - 50, 100, 50));
		widgets.add(lblInfo);

		//Trade Button
		btnTrade = new Button(Language.TRADE.get_text(), new Rectangle(100, 10, 70, 40));
		btnTrade.set_font(FontMgr.getFont(25));
		btnTrade.adjustWidth(5);
		btnTrade.set_enabled(buttonsEnabled);
		btnTrade.set_click_callback(new Runnable() {
			@Override
			public void run() {
				tradeDemand = new TradeDemand();
				tradeDemand.set_demander_id(id);
				mode = GUIMode.TRADE_DEMAND;
				rebuild_gui();
			}
		});
		widgets.add(btnTrade);
		//Menu Button
		Button btnMenu = new Button(Language.MENU.get_text(), new Rectangle(window_size.x / 2 - 50, 10, 100, 40));
		btnMenu.set_fill_color(new Color(0.98f, 0.98f, 0.98f, 0.16f));
		btnMenu.set_click_callback(new Runnable() {
			@Override
			public void run() {
				mode = GUIMode.MENU;
				rebuild_gui();
			}
		});
		widgets.add(btnMenu);
	}

	public void build__demander_trade_window() {
		Label lblWindow = new Label("", new Rectangle(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new Rectangle(window_size.x - 70, 25, 40, 40));
		btnClose.set_text_color(Color.RED);
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.closeTrade();
				mode = GUIMode.GAME;
				rebuild_gui();
			}
		});
		widgets.add(btnClose);
		if (tradeDemand.getVendor() == null) {
			Button btnAskBank = new Button(Language.BANK.get_text(), new Rectangle(200, window_size.y / 4, 200, 100));
			btnAskBank.set_click_callback(new Runnable() {
				@Override
				public void run() {
					tradeDemand.setVendor(Vendor.BANK);
					rebuild_gui();
				}
			});
			java.util.Map<Resource, Integer> playerResources = state.my_player_data.get_all_resources();
			boolean enabled = false;
			for (Resource r : playerResources.keySet()) {
				if(playerResources.get(r) >= 2 && state.my_player_data.harbours.contains(r)) {
					enabled = true;
					break;
				}
				if(playerResources.get(r) >= 3 && state.my_player_data.harbours.contains(null)) {
					enabled = true;
					break;
				}
				if (playerResources.get(r) >= 4) {
					enabled = true;
					break;
				} else {
					enabled = false;
				}
			}
			btnAskBank.set_enabled(enabled);
			widgets.add(btnAskBank);
			Button btnAskPlayer = new Button(Language.PLAYER.get_text(), new Rectangle(500, window_size.y / 4, 200, 100));
			btnAskPlayer.set_click_callback(new Runnable() {
				@Override
				public void run() {
					tradeDemand.setVendor(Vendor.PLAYER);
					rebuild_gui();
				}
			});
			widgets.add(btnAskPlayer);
		}
		int i = 0;
		int btnHeight = 50;
		int btnSpace = 10;

		//All wanted resources
		//TODO Add phrases to Language
		if (tradeDemand.getVendor() != null) {
			Label lblWantedResources = new Label(Language.CMD_SELECT_WANTED.get_text(), new Rectangle(50, 100, 200, 50));
			lblWantedResources.set_text_color(Color.WHITE);
			widgets.add(lblWantedResources);

			float start1 = 30;
			float containerWidth1 = window_size.x / 4;
			float lblWidth1 = containerWidth1 / 3;
			float btnWidth1 = containerWidth1 / 6 - 5;
			float lblHeight = 50;
			for (final Resource r : Resource.values()) {
				if (r != Resource.OCEAN && r != Resource.DESERT) {

					final Button btnWantedResource = new Button((Language.valueOf(r.toString()).get_text()),
							new Rectangle(start1, 170 + (lblHeight + btnSpace) * i, lblWidth1, lblHeight));
					if (tradeDemand.getWantedResources().containsKey(r)) {
						btnWantedResource.set_fill_color(r.get_color());
						btnWantedResource.set_text_color(Color.WHITE);
						btnWantedResource.set_outline(Color.GREEN, 2);
					} else {
						Color c = r.get_color();
						btnWantedResource.set_text_color(Color.BLACK);
						btnWantedResource.set_fill_color(new Color(c.r, c.g, c.b, 0.4f));
						btnWantedResource.set_outline(Color.TRANSPARENT, 2);
					}
					btnWantedResource.set_click_callback(new Runnable() {
						@Override
						public void run() {
							if (tradeDemand.getWantedResources().containsKey(r)) {
								Color c = r.get_color();
								btnWantedResource.set_fill_color(new Color(c.r, c.g, c.b, 0.4f));
								btnWantedResource.set_text_color(Color.BLACK);
								btnWantedResource.set_outline(Color.TRANSPARENT, 2);
								tradeDemand.removeWantedResource(r);
							} else {
								btnWantedResource.set_fill_color(r.get_color());
								btnWantedResource.set_text_color(Color.WHITE);
								btnWantedResource.set_outline(Color.GREEN, 2);
								tradeDemand.addWantedResource(r);
							}
							rebuild_gui();
						}
					});
					widgets.add(btnWantedResource);

					Button btnMinus = new Button("-", new Rectangle(start1 + btnSpace + lblWidth1,
							190 + (lblHeight + btnSpace) * i, btnWidth1, 30));
					/*btnMinus.set_text_position(start1 + btnSpace + lblWidth1 + btnWidth1 / 2,
							190 + (lblHeight + btnSpace) * i);*/// TODO del
					//btnMinus.adjustWidth(2);
					btnMinus.set_click_callback(new Runnable() {
						@Override
						public void run() {
							tradeDemand.substractWantedResource(r);
							rebuild_gui();
						}
					});
					widgets.add(btnMinus);
					String num = "0";
					if (tradeDemand.getWantedResources().containsKey(r)) {
						num = tradeDemand.getWantedResources().get(r).toString();
					}
					Label lblNumresources = new Label(num, new Rectangle(start1 + btnSpace * 2 + lblWidth1 + btnWidth1,
							180 + (lblHeight + btnSpace) * i, btnWidth1, 50));
					lblNumresources.set_fill_color(Color.WHITE);
					widgets.add(lblNumresources);
					Button btnPlus = new Button("+", new Rectangle(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2,
							190 + (lblHeight + btnSpace) * i, btnWidth1, 30));
					/*btnPlus.set_text_position(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2.5f,
							195 + (lblHeight + btnSpace) * i);*/// TODO del
					//btnPlus.adjustWidth(2);
					btnPlus.set_click_callback(new Runnable() {
						@Override
						public void run() {
							tradeDemand.addWantedResource(r);
							rebuild_gui();
						}
					});
					widgets.add(btnPlus);
					i++;
				}
			}
			//All Resources for offer
			i = 0;
			float start2 = window_size.x / 4 + 20;
			Label lblOfferedResources = new Label(Language.CMD_SELECT_OFFERED.get_text(), new Rectangle(start2, 100, 300, 50));
			lblOfferedResources.set_text_color(Color.WHITE);
			widgets.add(lblOfferedResources);
			for (final Resource r : Resource.values()) {

				if (r != Resource.OCEAN && r != Resource.DESERT && state.my_player_data.get_resources(r) >= 1
						&& tradeDemand.getVendor() == Vendor.PLAYER
						|| r != Resource.OCEAN && r != Resource.DESERT && (state.my_player_data.get_resources(r) >= 4 || state.my_player_data.get_resources(r) >= 3 && state.my_player_data.harbours.contains(null)
						|| state.my_player_data.get_resources(r) >= 2 && state.my_player_data.harbours.contains(r))
								&& tradeDemand.getVendor() == Vendor.BANK) {
					String resourceString = (Language.valueOf(r.toString()).get_text()) + ": "
							+ state.my_player_data.get_resources(r);
					if(state.my_player_data.harbours.contains(null) && tradeDemand.getVendor() == Vendor.BANK) {
						resourceString += " (3-1)";
					}else if(state.my_player_data.harbours.contains(r) && tradeDemand.getVendor() == Vendor.BANK) {
						resourceString += " (2-1)";
					}
					final Button btnOfferedResource = new Button(resourceString,
							new Rectangle(start2, (btnHeight + btnSpace) * i + 200, 150, btnHeight));
					if (tradeDemand.getOfferedResources().containsKey(r)) {
						btnOfferedResource.set_fill_color(r.get_color());
						btnOfferedResource.set_text_color(Color.WHITE);
						btnOfferedResource.set_outline(Color.RED, 2);
					} else {
						Color c = r.get_color();
						btnOfferedResource.set_fill_color(new Color(c.r, c.g, c.b, 0.39f));
						btnOfferedResource.set_outline(Color.TRANSPARENT, 2);
					}
					btnOfferedResource.set_click_callback(new Runnable() {
						@Override
						public void run() {
							if (tradeDemand.getOfferedResources().containsKey(r)) {
								Color c = r.get_color();
								btnOfferedResource.set_fill_color(new Color(c.r, c.g, c.b, 0.39f));
								btnOfferedResource.set_text_color(Color.BLACK);
								btnOfferedResource.set_outline(Color.TRANSPARENT, 2);
								tradeDemand.removeOfferedResource(r);
							} else {
								btnOfferedResource.set_fill_color(r.get_color());
								btnOfferedResource.set_text_color(Color.WHITE);
								btnOfferedResource.set_outline(Color.RED, 2);
								tradeDemand.addOfferedResource(r);
							}
						}
					});
					btnOfferedResource.adjustWidth(2);
					widgets.add(btnOfferedResource);
					i++;
				}
			}

			Label lblAllOffers = new Label(Language.ALL_OFFERS.get_text(), new Rectangle(window_size.x / 2, 150, 300, 50));
			lblAllOffers.set_text_color(Color.WHITE);
			widgets.add(lblAllOffers);
			//show all offers
			//TODO for every player one scrollable Container
			//TODO show when player rejected trade demand
			i = 0;
			for(final LocalPlayer p : state.player_data) {
				if(p.getID() != id && !p.declinedOffer) {
					Label lblPlayer = new Label(p.getName(), new Rectangle(window_size.x / 2, 260 + i*50, 300, 50));
					lblPlayer.set_text_color(p.getColor());
					widgets.add(lblPlayer);
					int offerI = 0;
					for(final TradeOffer offer : allTradeOffer) {
						if(offer.getVendor_id() == p.getID()) {
							Label lblOfferID = new Label(Language.OFFER.get_text() + offerI + " "+Language.FROM.get_text()+ " " + p.getName(),
									new Rectangle(window_size.x / 2, 200 + (150) * offerI, 300, 50));
							lblOfferID.set_text_color(state.player_data.get(offer.getVendor_id()).getColor());
							lblOfferID.set_font(FontMgr.getFont(23));
							widgets.add(lblOfferID);
							Label lblOfferContainer = new Label("",
									new Rectangle(window_size.x / 2, 200 + (150 + 3) * offerI, window_size.x / 2 - 30, 150));
							lblOfferContainer.set_fill_color(new Color(1.f, 1.f, 1.f, 0.3f)); //TODO Maybe change to player color
							widgets.add(lblOfferContainer);

							//Offered resources
							int j = 0;
							Label lblOfferedResource;
							for (Resource r : offer.getOfferedResources().keySet()) {
								lblOfferedResource = new Label(Language.valueOf(r.toString()).get_text() + ": " + offer.getOfferedResources().get(r),
										new Rectangle(window_size.x / 2 + 120 * j, 250 + (150) * offerI, 120, 50));
								lblOfferedResource.set_fill_color(r.get_color());
								lblOfferedResource.set_font(FontMgr.getFont(23));
								lblOfferedResource.set_text_color(Color.GREEN);
								widgets.add(lblOfferedResource);
								j++;
							}
							//vendor Wanted Resources
							j = 0;
							Label lblWantedResource;
							for (Resource r : offer.getDemandedResources().keySet()) {
								lblWantedResource = new Label(Language.valueOf(r.toString()).get_text() + ": " + offer.getDemandedResources().get(r),
										new Rectangle(window_size.x / 2 + 120 * j, 300 + (150) * offerI, 120, 50));
								lblWantedResource.set_fill_color(r.get_color());
								lblWantedResource.set_font(FontMgr.getFont(23));
								lblWantedResource.set_text_color(Color.RED);
								widgets.add(lblWantedResource);
								j++;
							}

							//Button accept
							Button btnAccept = new Button(Language.ACCEPT.get_text(),
									new Rectangle(window_size.x - 140, 250 + (110 + 20) * offerI, 80, 50));
							btnAccept.set_click_callback(new Runnable() {
								@Override
								public void run() {
									core.acceptOffer(offer);
								}
							});
							btnAccept.adjustWidth(2);
							widgets.add(btnAccept);
							Button btnReject = new Button("X", new Rectangle(window_size.x - 80, 200 + (110 + 20) * offerI, 50, 30));
							btnReject.set_text_color(Color.RED);
							btnReject.set_click_callback(new Runnable() {
								private List<TradeOffer> newAllTradeOffer = new ArrayList<TradeOffer>();

								@Override
								public void run() {
									for (TradeOffer innerOffer : allTradeOffer) {
										if (innerOffer != offer) {
											newAllTradeOffer.add(innerOffer);
										}
									}
									allTradeOffer = newAllTradeOffer;
									rebuild_gui();
								}
							});
							widgets.add(btnReject);
							offerI++;
						}
					}
					i++;
				}
			}
			//sc = new ScrollContainer(new Rectangle(window_size.x / 2, 200 , 300, (110 + 20) * i));
			//Button Send demand
			Button btnSendDemand = new Button(Language.SEND.get_text(),
					new Rectangle(window_size.x - 300, window_size.y - 100, 200, 70));
			btnSendDemand.set_fill_color(Color.GREEN);
			btnSendDemand.set_click_callback(new Runnable() {
				@Override
				public void run() {
					for(LocalPlayer p : state.player_data){
						p.declinedOffer = false;
					}
					core.new_trade_demand(tradeDemand);
					rebuild_gui();
				}
			});
			widgets.add(btnSendDemand);
		}

	}

	public void build_vendor_trade_window() {
		Label lblWindow = new Label("", new Rectangle(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new Rectangle(window_size.x - 70, 25, 40, 40));
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.declineTradeDemand(id);
				mode = GUIMode.GAME;
				rebuild_gui();
			}
		});
		widgets.add(btnClose);
		int i = 0;
		int btnWidth = 50;
		int btnResourceWidth = 120;
		int lblHeight = 50;
		int btnSpace = 10;
		//Show all wanted resources
		Label lblAllWantedResources = new Label("Player want these resources: ", new Rectangle(30, 40, 200, 50));
		lblAllWantedResources.set_text_color(Color.WHITE);
		widgets.add(lblAllWantedResources);
		for (Resource r : tradeDemand.wantedResources.keySet()) {
			Label lblWantedResource = new Label(Language.valueOf(r.toString()).get_text(),
					new Rectangle((btnResourceWidth + btnSpace) * i + 50, 100, btnResourceWidth, 50));
			lblWantedResource.set_fill_color(r.get_color());
			widgets.add(lblWantedResource);
			i++;
		}
		//Show all offered Resources
		Label allOfferedResources = new Label("Player Offers these resources for trading: ",
				new Rectangle(window_size.x / 2 + 90, 40, 200, 50));
		allOfferedResources.set_text_color(Color.WHITE);
		widgets.add(allOfferedResources);
		i = 0;
		for (Resource r : tradeDemand.offeredResources.keySet()) {
			Label lblWantedResource = new Label(Language.valueOf(r.toString()).get_text(), new Rectangle(
					window_size.x / 2 + (btnResourceWidth + btnSpace) * i + 80, 100, btnResourceWidth, 50));
			lblWantedResource.set_fill_color(r.get_color());
			widgets.add(lblWantedResource);
			i++;
		}

		//show all possible trading resources with numbers
		float start1 = 30;
		float containerWidth1 = window_size.x / 4;
		float lblWidth1 = containerWidth1 / 3;
		float btnWidth1 = containerWidth1 / 6 - 5;
		Label lblWantedResourcesFromDemander = new Label(
				"I want from " + state.player_data.get(tradeDemand.get_demander_id()).getName() + "|",
				new Rectangle(start1, 200, 300, 50));
		lblWantedResourcesFromDemander.set_text_color(Color.WHITE);
		widgets.add(lblWantedResourcesFromDemander);
		i = 0;
		for (final Resource r : Resource.values()) {
			if (tradeDemand.offeredResources.containsKey(r)) {
				String num = "0";
				if (tradeOffer.getDemandedResources().containsKey(r)) {
					num = tradeOffer.getDemandedResources().get(r).toString();
				}
				final Label lblNumresources = new Label(num, new Rectangle(start1 + btnSpace * 2 + lblWidth1 + btnWidth1,
						280 + (lblHeight + btnSpace) * i, btnWidth1, 50));
				lblNumresources.set_fill_color(Color.WHITE);;
				widgets.add(lblNumresources);

				Label lblResource = new Label(Language.valueOf(r.toString()).get_text(),
						new Rectangle(start1, 270 + (lblHeight + btnSpace) * i, lblWidth1, lblHeight));
				lblResource.set_text_color(r.get_color());
				widgets.add(lblResource);
				Button btnMinus = new Button("-",
						new Rectangle(start1 + btnSpace + lblWidth1, 290 + (lblHeight + btnSpace) * i, btnWidth1, 30));
				btnMinus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.substractWantedResource(r);
						if(tradeOffer.getDemandedResources().containsKey(r)) {
							lblNumresources.set_text(tradeOffer.getDemandedResources().get(r).toString());
						}else {
							lblNumresources.set_text("0");
						}

					}
				});
				widgets.add(btnMinus);

				Button btnPlus = new Button("+", new Rectangle(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2,
						290 + (lblHeight + btnSpace) * i, btnWidth1, 30));
				btnPlus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.addWantedResource(r);
						lblNumresources.set_text(tradeOffer.getDemandedResources().get(r).toString());
					}
				});
				widgets.add(btnPlus);
				i++;
			}
		}

		//Show Resources that are offered by myself with number
		float start = window_size.x / 4;
		float containerWidth = window_size.x / 4;
		float lblWidth = containerWidth / 3;
		float btnWidths = containerWidth / 6;
		Label lblOfferedResourcesFromMe = new Label(
				"I give " + state.player_data.get(tradeDemand.get_demander_id()).getName(),
				new Rectangle(start, 200, 300, 50));
		lblOfferedResourcesFromMe.set_text_color(Color.WHITE);
		widgets.add(lblOfferedResourcesFromMe);
		i = 0;
		for (final Resource r : Resource.values()) {
			if (tradeDemand.wantedResources.containsKey(r)) {
				String num = Integer.toString(tradeOffer.getOfferedResources().get(r));
				if (tradeOffer.getOfferedResources().containsKey(r)) {
					num = tradeOffer.getOfferedResources().get(r).toString();
				}
				final Label lblNumresources = new Label(num, new Rectangle(start + btnSpace * 2 + lblWidth + btnWidths,
						280 + (lblHeight + btnSpace) * i, btnWidths, 50));
				lblNumresources.set_fill_color(Color.WHITE);
				widgets.add(lblNumresources);
				Label lblResource = new Label(Language.valueOf(r.toString()).get_text(),
						new Rectangle(start, 270 + (lblHeight + btnSpace) * i, lblWidth, lblHeight));
				lblResource.set_text_color(r.get_color());
				widgets.add(lblResource);
				Button btnMinus = new Button("-",
						new Rectangle(start + btnSpace + lblWidth, 290 + (lblHeight + btnSpace) * i, btnWidths, 30));
				btnMinus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.substractOfferedResource(r);
						if(tradeOffer.getOfferedResources().containsKey(r)) {
							lblNumresources.set_text(tradeOffer.getOfferedResources().get(r).toString());
						}else {
							lblNumresources.set_text("0");
						}
					}
				});
				widgets.add(btnMinus);

				Button btnPlus = new Button("+", new Rectangle(start + btnSpace * 3 + lblWidth + btnWidths * 2,
						290 + (lblHeight + btnSpace) * i, btnWidths, 30));
				btnPlus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						if(state.my_player_data.get_resources(r) >= tradeOffer.getOfferedResources().get(r) + 1) {
							tradeOffer.addOfferedResource(r);
							lblNumresources.set_text(tradeOffer.getOfferedResources().get(r).toString());
						}
					}
				});
				widgets.add(btnPlus);
				i++;
			}
		}
		//Player resources
		i = 0;
		for (Resource r : Resource.values()) {
			if (r != Resource.OCEAN && r != Resource.DESERT) {
				String str = Language.valueOf(r.toString()).get_text() + ": " + state.my_player_data.get_resources(r);
				Label lblResource = new Label(str, new Rectangle(40 + i * 150, window_size.y - 110, 140, 70));
				lblResource.set_fill_color(r.get_color());
				widgets.add(lblResource);
				i++;
			}
		}
		//Show all own offers (if multiple are made)
		//sc = new ScrollContainer(this, new Rectangle(window_size.x / 2, 200 , window_size.x / 2 - 30, window_size.y - 200));
		Label lblAllOffers = new Label("All own offers", new Rectangle(window_size.x / 2, 150, 300, 50));
		lblAllOffers.set_text_color(Color.WHITE);
		widgets.add(lblAllOffers);
		i = 0;
		for (TradeOffer offer : allTradeOffer) {
			//Offer Label
			Label lblOfferID = new Label("Offer " + i, new Rectangle(window_size.x / 2, 200 + (150) * i, 300, 50));
			lblOfferID.set_text_color(state.player_data.get(offer.getVendor_id()).getColor());
			lblOfferID.set_font(FontMgr.getFont(23));
			widgets.add(lblOfferID);
			Label lblOfferContainer = new Label("",
					new Rectangle(window_size.x / 2, 200 + (150 + 3) * i, window_size.x / 2 - 30, 150));
			lblOfferContainer.set_fill_color(new Color(1.f, 1.f, 1.f, 0.3f)); //TODO Maybe change to player color
			lblOfferContainer.set_outline(Color.BLACK, 2);
			widgets.add(lblOfferContainer);

			//Offered resources
			int j = 0;
			Label lblOfferedResource;
			for (Resource r : offer.getOfferedResources().keySet()) {
				lblOfferedResource = new Label(Language.valueOf(r.toString()).get_text() + ": " + offer.getOfferedResources().get(r),
						new Rectangle(window_size.x / 2 + 120 * j, 250 + (150) * i, 120, 50));
				lblOfferedResource.set_fill_color(r.get_color());
				lblOfferedResource.set_font(FontMgr.getFont(23));
				lblOfferedResource.set_text_color(Color.RED);
				widgets.add(lblOfferedResource);
				j++;
			}
			//Wanted Resources
			j = 0;
			Label lblWantedResource;
			for (Resource r : offer.getDemandedResources().keySet()) {
				lblWantedResource = new Label(Language.valueOf(r.toString()).get_text() + ": " + offer.getDemandedResources().get(r),
						new Rectangle(window_size.x / 2 + 120 * j, 300 + (150) * i, 120, 50));
				lblWantedResource.set_fill_color(r.get_color());
				lblWantedResource.set_font(FontMgr.getFont(23));
				lblWantedResource.set_text_color(Color.GREEN);
				widgets.add(lblWantedResource);
				j++;
			}
			i++;
		}
		//Button Send offer
		Button btnSendOffer = new Button(Language.SEND.get_text(),
				new Rectangle(window_size.x - 300, window_size.y - 110, 200, 70));
		btnSendOffer.set_fill_color(Color.GREEN);
		btnSendOffer.set_click_callback(new Runnable() {
			TradeDemand t = tradeDemand;

			@Override
			public void run() {
				allTradeOffer.add(tradeOffer);
				core.new_trade_offer(tradeOffer);
				tradeOffer = new TradeOffer();
				tradeOffer.setVendor_id(id);
				tradeOffer.setDemanderID(tradeDemand.get_demander_id());
				tradeOffer.setDemandedResources(t.getOfferedResources());
				tradeOffer.setOfferedresources(t.getWantedResources());
				rebuild_gui();
			}
		});
		widgets.add(btnSendOffer);
	}

	public void buildDevCardWindow() {
		switch(state.devCard.type) {
		case FREE_RESOURCES:
			buildIngameWindow();
			state.devCard = new DevCard(DevCardType.FREE_RESOURCES, new DevCard.FreeResources());
			int i = 0;
			for(final Resource r : Resource.values()) {
				if(r != Resource.OCEAN && r != Resource.DESERT) {
					final String str = Language.valueOf(r.name()).get_text() + ": ";
					int valResource;
					if(((DevCard.FreeResources) state.devCard.data).newResources.containsKey(r))
						valResource = ((DevCard.FreeResources) state.devCard.data).newResources.get(r) + state.my_player_data.get_resources(r);
					else
						valResource = state.my_player_data.get_resources(r);
					final Label lblResource = new Label(str + valResource, new Rectangle(100, 100 + i*100, 200, 90));
					lblResource.set_fill_color(r.get_color());
					widgets.add(lblResource);
					Button btnAddresource = new Button("+", new Rectangle(320, 100 + i*100, 100, 90));
					btnAddresource.set_click_callback(new Runnable() {
						@Override
						public void run() {
							((DevCard.FreeResources)state.devCard.data).addResource(r);
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									int valResource = ((DevCard.FreeResources) state.devCard.data).newResources.get(r) + state.my_player_data.get_resources(r);
									lblResource.set_text(str + valResource);
								}
							});
							((DevCard.FreeResources)state.devCard.data).remainedFreeresources--;
							if(((DevCard.FreeResources)state.devCard.data).remainedFreeresources <= 0) {
								mode = GUIMode.GAME;
								state.my_player_data.getDevelopmentCards().remove(DevCard.Type.FREE_RESOURCES);
								core.playCard(id, state.devCard);
								rebuild_gui();
							}
						}
					});
					widgets.add(btnAddresource);
					i++;
				}
			}
			break;
		case FREE_STREETS:
			if(state.devCard.data == null) {
				state.devCard = new DevCard(DevCardType.FREE_STREETS, new DevCard.FreeStreets());
			}
			mode = GUIMode.GAME;
			show_informative_hint(Language.BUILD_FREE_STREETS, Integer.toString(((DevCard.FreeStreets) state.devCard.data).remainedFreeStreets));
			rebuild_gui();

			//state.devCard ;
			break;
		case KNIGHT:
			mode = GUIMode.GAME;
			state.devCard = new DevCard(DevCardType.KNIGHT, new DevCard.Knight());
			core.playCard(id, state.devCard);
			rebuild_gui();
			break;
		case MONOPOL:
			buildIngameWindow();
			state.devCard = new DevCard(DevCardType.MONOPOL, new DevCard.Monopol());
			i = 0;
			for(final Resource r : Resource.values()) {
				if(r != Resource.OCEAN && r != Resource.DESERT) {
					String str = Language.valueOf(r.name()).get_text() + ": ";
					Button btnResource = new Button(str + state.my_player_data.get_resources(r), new Rectangle(100, 100 + i*100, 200, 90));
					btnResource.set_fill_color(r.get_color());
					btnResource.set_click_callback(new Runnable() {
						@Override
						public void run() {
							((DevCard.Monopol)state.devCard.data).resource = r;
							core.playCard(id, state.devCard);
						}
					});
					widgets.add(btnResource);
					i++;
				}
			}
			break;
		case POINT:
			mode = GUIMode.GAME;
			core.playCard(id, state.devCard);
			break;
		default:
			System.err.println("Unkown Card played");
			break;
		}

	}

	private void buildToMuchResourcesWindow() {
		buildIngameWindow();

		float start1 = window_size.x / 2 - window_size.x / 4;
		float containerWidth1 = window_size.x / 3;
		float lblWidth1 = containerWidth1 / 2;
		float btnWidth1 = 40;
		float lblHeight = 50;
		float btnSpace = 20;
		int i = 0;

		Label lblNum = new Label(Language.TO_MUCH_RESOURCES.get_text(Integer.toString(state.numToRemove)), new Rectangle(start1, 40, 100, 50));
		if(state.numToRemove > 0) {
			lblNum.set_fill_color(Color.RED);
		}else {
			lblNum.set_fill_color(Color.GREEN);
		}
		lblNum.adjustWidth(7);
		widgets.add(lblNum);

		for (final Resource r : Resource.values()) {
			if (r != Resource.OCEAN && r != Resource.DESERT) {
				String str;
				if(state.removedResources.containsKey(r)) {
					str = Integer.toString(state.my_player_data.get_resources(r) - state.removedResources.get(r));
				}else {
					if(state.my_player_data.get_all_resources().containsKey(r)) {
						str = Integer.toString(state.my_player_data.get_resources(r));
					}else {
						str = "0";
					}
				}
				final Button btnResource = new Button((Language.valueOf(r.toString()).get_text())+ ": " + str ,
						new Rectangle(start1, 170 + (lblHeight + btnSpace) * i, lblWidth1, lblHeight));
				if (state.removedResources.containsKey(r)) {
					btnResource.set_fill_color(r.get_color());
					btnResource.set_text_color(Color.WHITE);
					btnResource.set_outline(Color.RED, 2);
				} else {
					Color c = r.get_color();
					btnResource.set_text_color(Color.BLACK);
					btnResource.set_fill_color(new Color(c.r, c.g, c.b, 0.4f));
					btnResource.set_outline(Color.TRANSPARENT, 2);
				}
				widgets.add(btnResource);

				Button btnMinus = new Button("-", new Rectangle(start1 + btnSpace + lblWidth1,
						190 + (lblHeight + btnSpace) * i, btnWidth1, 30));
				btnMinus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						if(state.removedResources.containsKey(r)) {
							state.numToRemove++;
							if(state.removedResources.get(r) == 1) {
								state.removedResources.remove(r);
							}else {
								state.removedResources.put(r, state.removedResources.get(r) - 1);
							}
						}
						rebuild_gui();
					}
				});
				widgets.add(btnMinus);
				String num = "0";
				if (state.removedResources.containsKey(r)) {
					num = state.removedResources.get(r).toString();
				}
				Label lblNumresources = new Label(num, new Rectangle(start1 + btnSpace * 2 + lblWidth1 + btnWidth1,
						180 + (lblHeight + btnSpace) * i, btnWidth1, 50));
				lblNumresources.set_fill_color(Color.WHITE);
				widgets.add(lblNumresources);
				Button btnPlus = new Button("+", new Rectangle(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2,
						190 + (lblHeight + btnSpace) * i, btnWidth1, 30));
				btnPlus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						if(state.numToRemove != 0) {
							if(state.removedResources.containsKey(r)) {
								if(state.my_player_data.get_resources(r) - state.removedResources.get(r) > 0) {
									state.removedResources.put(r, state.removedResources.get(r) + 1);
									state.numToRemove--;
									rebuild_gui();
								}
							}else {
								if(state.my_player_data.get_resources(r) > 0) {
									state.removedResources.put(r, 1);
									state.numToRemove--;
									rebuild_gui();
								}
							}
						}
					}
				});
				widgets.add(btnPlus);
				i++;
			}
		}
		Button btnSend = new Button(Language.SEND.get_text(),
				new Rectangle(window_size.x - 300, window_size.y - 110, 200, 70));
		if(state.numToRemove > 0) {
			btnSend.set_enabled(false);
		}else {
			btnSend.set_enabled(true);
		}
		btnSend.set_fill_color(Color.GREEN);
		btnSend.set_click_callback(new Runnable() {
			@Override
			public void run() {
				if(state.numToRemove == 0) {
					core.removeResources(id, state.removedResources);
					state.removedResources.clear();
				}
			}
		});
		widgets.add(btnSend);
	}

	private void buildSteelResource() {
		buildIngameWindow();
		int i = 0;
		int btnWidth = 200;
		for(final Player p : state.surroundingPlayers) {
			Button btnPlayer = new Button(p.getName(), new Rectangle(window_size.x/2 + btnWidth, 100 + 60*i, btnWidth, 50));
			btnPlayer.set_fill_color(p.getColor());
			btnPlayer.set_click_callback(new Runnable() {
				@Override
				public void run() {
					core.stealResource(id, p.getId());
				}
			});
			btnPlayer.adjustWidth(10);
			widgets.add(btnPlayer);
		}
	}

	private void buildIngameWindow() {
		Label lblWindow = new Label("", new Rectangle(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new Rectangle(window_size.x - 70, 25, 40, 40));
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
				mode = GUIMode.GAME;
				rebuild_gui();
			}
		});
		widgets.add(btnClose);
	}

	public void build_join_menu() {
		destroy_widgets();
		mode = GUIMode.JOIN;

		float mm_tf_width = 400;
		float mm_tf_height = 50;
		float mm_tf_spacing = 20;

		final Checkbox cbOnlineLobby = new Checkbox(new Rectangle(window_size.x /2 + 100, 200, 35, 35));
		cbOnlineLobby.setSelected(onlineLobby);
		cbOnlineLobby.set_click_callback(new Runnable() {
			@Override
			public void run() {
				onlineLobby = !onlineLobby;
				cbOnlineLobby.setSelected(onlineLobby);
				if(onlineLobby) {
					btnJoinText = "Online Lobby";
				}else {
					btnJoinText = Language.JOIN_GAME.get_text();
				}

				rebuild_gui();
			}
		});
		widgets.add(cbOnlineLobby);
		final TextField tfIp = new TextField(new Rectangle(0, 0, mm_tf_width, mm_tf_height));
		tfIp.set_text(tf_value_ip);
		tfIp.set_input_callback(new Runnable() {
			TextField textField = tfIp;

			@Override
			public void run() {
				tf_value_ip = textField.get_text();
			}
		});
		if(!onlineLobby) {
			widgets.add(tfIp);
		}
		final TextField tfName = new TextField(new Rectangle(0, 0, mm_tf_width, mm_tf_height));
		tfName.set_text(tf_value_name);
		tfName.set_input_callback(new Runnable() {
			TextField textField = tfName;

			@Override
			public void run() {
				tf_value_name = textField.get_text();
			}
		});
		widgets.add(tfName);

		final ColorPicker colorPicker = new ColorPicker(new Rectangle(0, 0, mm_tf_width, mm_tf_height));
		colorPicker.set_color(color_pkr_hue, 1.f, 0.9f);
		colorPicker.set_select_callback(new Runnable() {
			ColorPicker cp = colorPicker;

			@Override
			public void run() {
				color_pkr_hue = cp.get_hue();
				playerColor = cp.get_color();
			}
		});
		playerColor = colorPicker.get_color();
		widgets.add(colorPicker);

		final Label lblConnecting;
		lblConnecting = new Label("Try to Connect to: " + tf_value_ip,
				new Rectangle(window_size.x / 2, window_size.y - 200, 100, 50));
		lblConnecting.set_visible(false);

		Button btn = new Button(btnJoinText, new Rectangle(0, 0, mm_tf_width, mm_tf_height));
		btn.set_position(new Vector2((window_size.x - mm_tf_width) * 0.5f + 200,
				(window_size.y - (mm_tf_height + mm_tf_spacing) * 2) * 0.5f + (mm_tf_height + mm_tf_spacing) * 2));
		btn.set_fill_color(new Color(0.24f, 1.f, 0.24f, 0.4f));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				lblConnecting.set_visible(true);
				if (tf_value_ip.length() > 4 && tf_value_name.length() > 0) {
					//Entered wrong Ip or server is not online
					new Thread(new Runnable() {
						public void run() {
							if(onlineLobby) {

								framework.initOnlineGuestGame();
							}else {
								if (!framework.init_guest_game(tf_value_ip.trim(), tf_value_name.trim(), playerColor)) {
									System.out.println("Not accepted");
									tfIp.set_outline(Color.RED, 2);
									lblConnecting.set_text("Entered wrong IP or the server is not online");
								}
							}

						}
					}).start();
				} else {
					if (tf_value_ip.length() <= 4) {
						tfIp.set_outline(Color.RED, 2);
					}
					if (tf_value_name.length() == 0) {
						tfName.set_outline(Color.RED, 2);
					}
				}
			}

		});
		widgets.add(btn);

		for (int i = 0; i < widgets.size(); i++) {
			Widget temp_tf = widgets.get(i);
			temp_tf.set_position(new Vector2((window_size.x - mm_tf_width) * 0.5f + 200,
					(window_size.y - (mm_tf_height + mm_tf_spacing) * widgets.size()) * 0.5f
							+ (mm_tf_height + mm_tf_spacing) * i));
		}
		if(!onlineLobby) {
			Label lbl = new Label("Enter IP: ",
					new Rectangle((window_size.x - mm_tf_width) * 0.5f, tfIp.get_position().y, mm_tf_width, mm_tf_height));
			widgets.add(lbl);
		}
		Label lbl = new Label("Enter Name: ", new Rectangle((window_size.x - mm_tf_width) * 0.5f, tfName.get_position().y,
				mm_tf_width, mm_tf_height));
		widgets.add(lbl);
		widgets.add(lblConnecting);

		Button btnBack = new Button(Language.BACK.get_text(), new Rectangle(20, window_size.y - 70, 100, 50));
		btnBack.adjustWidth(5);
		btnBack.set_click_callback(new Runnable() {
			@Override
			public void run() {
				build_lobby();
			}
		});
		widgets.add(btnBack);

		//Online stuff
		Label lblOnlineLobby = new Label("Online lobby", new Rectangle((window_size.x - mm_tf_width) * 0.5f, cbOnlineLobby.get_position().y, mm_tf_width, mm_tf_height));
		widgets.add(lblOnlineLobby);
	}
	public void buildAllJoinableGamesWindow() {
		List<Packet.JoinableGame> allGames = allJoinableGames;
		Label lblHeader = new Label("There are " + allGames.size() + " joinable games online", new Rectangle(20, 20, 100,50));
		lblHeader.adjustWidth(10);
		widgets.add(lblHeader);
		ScrollContainer c = new ScrollContainer(this, new Rectangle(100, 100, window_size.x -100, window_size.y -100));
		int i = 0;
		for(final Packet.JoinableGame game : allGames) {
			Button btnGame = new Button("Game: "+ game.gameID + " - " + game.gameName, new Rectangle(100, 100 + 120*i, 300, 100));
			btnGame.set_click_callback(new Runnable() {
				@Override
				public void run() {
					core.joinGameLobby(game.gameID, tf_value_name, playerColor);
				}
			});
			c.addWidget(btnGame);
			i++;
		}
		c.calcBounds();
		widgets.add(c);
		Button btnBack = new Button(Language.BACK.get_text(), new Rectangle(20, window_size.y - 70, 100, 50));
		btnBack.adjustWidth(5);
		btnBack.set_click_callback(new Runnable() {
			@Override
			public void run() {
				build_lobby();
			}
		});
		widgets.add(btnBack);
	}
	public void build_guest_lobby_window() {
		destroy_widgets();
		mode = GUIMode.GUEST_LOBBY;

		if (allPossiblePlayer.size() > 0) {
			int i = 0;
			for (final Player p : allPossiblePlayer) {
				Button btnPName = new Button(p.getName(), new Rectangle(window_size.x / 2 - 100, 50 + 70 * i, 200, 60));
				btnPName.set_fill_color(p.getColor());
				btnPName.set_click_callback(new Runnable() {
					@Override
					public void run() {
						core.register_new_user(p.getName(), p.getColor());
					}
				});
				widgets.add(btnPName);
				i++;
			}
		} else {
			Label lbl = new Label("Successfully joined " +state.gameName + " Lobby with: ", new Rectangle(0, 0, 100, 100));
			widgets.add(lbl);
			int i = 0;
			for (String guestName : guests) {
				Label lblPlayer = new Label(guestName, new Rectangle(window_size.x / 2, 200 + 110 * i, 400, 100));
				Color color = new Color(0.4f, 0.4f, 0.4f, 0.4f);
				lblPlayer.set_fill_color(color);
				widgets.add(lblPlayer);
				i++;
			}

		}
		Button btnBack = new Button(Language.EXIT.get_text(), new Rectangle(20, window_size.y - 70, 100, 50));
		btnBack.adjustWidth(5);
		btnBack.set_click_callback(new Runnable() {
			@Override
			public void run() {
				//TODO inform server
				core.clientLeaveGame(id);
				resetData(false);
				build_lobby();
			}
		});
		widgets.add(btnBack);
	}

	public void build_host_lobby_window() {
		destroy_widgets();
		mode = GUIMode.HOST_LOBBY;
		final Label lblHostIp = new Label(serverIP, new Rectangle(window_size.x / 2, 0, 100, 30));
		widgets.add(lblHostIp);
		float column0 = 0;
		float column1 = window_size.x / 2 > 300 ? window_size.x / 2 : 300;
		float height_anchor = 10;
		float textfield_width = 200;
		float textfield_height = 50;
		float row_count = 0;

		if (savedGame != null) {
			Label lblGameName = new Label(Language.SETTINGS.get_text() + savedGame.getName(),
					new Rectangle(10, 10, 200, 50));
			lblGameName.set_text_color(Color.GREEN);
			widgets.add(lblGameName);

		} else {
			Label lbl = new Label(Language.SETTINGS.get_text() + ": ", new Rectangle(10,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);

			//Row0 ==> Settings

			row_count++;
			lbl = new Label(Language.MAP_SIZE.get_text() + ": ", new Rectangle(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.SEED.get_text() + ": ", new Rectangle(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.YOUR_NAME.get_text() + ": ", new Rectangle(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.YOUR_COLOR.get_text() + ": ", new Rectangle(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.RANDOM_HOUSES.get_text() + ": ", new Rectangle(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.RESOURCE_HOUSES.get_text() + ": ", new Rectangle(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.IS_CIRCLE.get_text(), new Rectangle(column0 + 180 + textfield_height + 5,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.LOCAL.get_text(), new Rectangle(column0 + 180 + textfield_height + 5,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);

			row_count = 2;
			final TextField tfMapSize = new TextField(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			tfMapSize.set_text_color(new Color(0.08f, 0.08f, 0.08f, 1.f));
			tfMapSize.set_text(tf_value_size);
			tfMapSize.set_input_callback(new Runnable() {
				TextField textField = tfMapSize;

				@Override
				public void run() {
					tf_value_size = textField.get_text();
				}
			});
			widgets.add(tfMapSize);

			final TextField tfSeed = new TextField(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count, textfield_width, textfield_height));
			tfSeed.set_text_color(new Color(0.08f, 0.08f, 0.08f, 1.f));
			tfSeed.set_text(tf_value_seed);
			tfSeed.set_input_callback(new Runnable() {
				TextField textField = tfSeed;

				@Override
				public void run() {
					tf_value_seed = textField.get_text();
				}
			});
			widgets.add(tfSeed);
			Button btnRandom = new Button(Language.RANDOM.get_text(), new Rectangle(column0 + 205 + textfield_width,
					height_anchor + (textfield_height + 10) * row_count++, 100, textfield_height));
			btnRandom.set_click_callback(new Runnable() {
				@Override
				public void run() {
					tf_value_seed = "" + (int) (Math.random() * Integer.MAX_VALUE);
					tfSeed.set_text(tf_value_seed);
				}
			});
			widgets.add(btnRandom);

			final TextField tfName = new TextField(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			tfName.set_text_color(new Color(0.08f, 0.08f, 0.08f, 1.f));
			tfName.set_text(tf_value_name);
			tfName.set_input_callback(new Runnable() {
				TextField textField = tfName;

				@Override
				public void run() {
					tf_value_name = textField.get_text();
				}
			});
			widgets.add(tfName);

			final ColorPicker colorPicker = new ColorPicker(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			colorPicker.set_color(color_pkr_hue, 1.f, 0.9f);
			colorPicker.set_select_callback(new Runnable() {
				ColorPicker cp = colorPicker;

				@Override
				public void run() {
					color_pkr_hue = cp.get_hue();
					playerColor = cp.get_color();
				}
			});
			playerColor = colorPicker.get_color();
			widgets.add(colorPicker);

			final TextField tfRandomHouses = new TextField(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			tfRandomHouses.set_text_color(new Color(0.08f, 0.08f, 0.08f, 1.f));
			tfRandomHouses.set_text(tf_value_random_houses);
			tfRandomHouses.set_input_callback(new Runnable() {
				TextField textField = tfRandomHouses;

				@Override
				public void run() {
					tf_value_random_houses = textField.get_text();
				}
			});
			widgets.add(tfRandomHouses);

			final TextField tfResourceHouses = new TextField(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			tfResourceHouses.set_text_color(new Color(0.08f, 0.08f, 0.08f, 1.f));
			tfResourceHouses.set_text(tf_value_resource_houses);
			tfResourceHouses.set_input_callback(new Runnable() {
				TextField textField = tfResourceHouses;

				@Override
				public void run() {
					tf_value_resource_houses = textField.get_text();
				}
			});
			widgets.add(tfResourceHouses);

			final Checkbox cbCircleMap = new Checkbox(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++ + textfield_height * .15f,
					textfield_height * .7f, textfield_height * .7f));
			cbCircleMap.setSelected(cb_value_is_circle);
			cbCircleMap.set_click_callback(new Runnable() {
				Checkbox cb = cbCircleMap;

				@Override
				public void run() {
					cb_value_is_circle = cb.isSelected();
				}
			});
			widgets.add(cbCircleMap);
			final Checkbox cbLocalgame = new Checkbox(new Rectangle(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++ + textfield_height * .15f,
					textfield_height * .7f, textfield_height * .7f));
			cbLocalgame.setSelected(cbValueIsLocal);
			cbLocalgame.set_click_callback(new Runnable() {
				Checkbox cb = cbLocalgame;

				@Override
				public void run() {
					cbValueIsLocal = cb.isSelected();
					if(!cbValueIsLocal) {
						serverIP = "Online";
						lblHostIp.set_text(serverIP);
						framework.publicizeGame();
					}else {
						framework.init_host_game();
						//serverIP = "Online";
						lblHostIp.set_text(serverIP);
					}
				}
			});
			widgets.add(cbLocalgame);
		}
		//Row1 ==> members
		Label lbl = new Label(Language.MEMBERS.get_text(), new Rectangle(column1, 10, 100, 100));
		widgets.add(lbl);

		if (savedGame != null) {
			int i = 0;
			for (Player p : savedGame.getPlayer()) {
				if (i == 0) {
					Label lblHost = new Label(p.getName(), new Rectangle(window_size.x / 2 - 500, 200, 400, 100));
					lblHost.set_fill_color(p.getColor());
					widgets.add(lblHost);
				} else {
					idxPlayer = i - 1;
					Label lblPlayer = new Label(p.getName(), new Rectangle(window_size.x / 2, 200 + 110 * i, 400, 100));
					Color color = new Color(0.4f, 0.4f, 0.4f, 0.4f);
					for (String guestName : guests) {
						if (guestName == p.getName()) {
							color = p.getColor();
						}
					}
					lblPlayer.set_fill_color(color);
					widgets.add(lblPlayer);
					i++;
				}

			}
			for (final String guest : guests) {
				boolean show = true;
				for (Player p : savedGame.getPlayer()) {
					if (guest == p.getName()) {
						show = false;
					}
				}
				if (show) {
					idxPlayer = i;
					lbl = new Label(guest, new Rectangle(camera.viewportWidth / 2 > 200 ? camera.viewportWidth / 2 : 200,
							200 + 110 * i, 400, 100));
					lbl.set_fill_color(new Color(0.98f, 0.4f, 0.4f, 0.78f));
					widgets.add(lbl);
					Button btnKickPlayer = new Button("Kick",
							new Rectangle(window_size.x / 2 + 300, 200 + 110 * i, 100, 100));
					btnKickPlayer.set_text_color(Color.RED);
					btnKickPlayer.set_click_callback(new Runnable() {
						@Override
						public void run() {
							((LocalCore) core).kickPlayer(guest);
							rebuild_gui();
						}
					});
					widgets.add(btnKickPlayer);
					i++;
				}

			}
		} else {
			for (int i = 0; i < guests.size(); i++) {
				idxPlayer = i;
				lbl = new Label(guests.get(i), new Rectangle(
						camera.viewportWidth / 2 > 200 ? camera.viewportWidth / 2 : 200, 200 + 110 * i, 400, 100));
				lbl.set_fill_color(new Color(0.4f, 0.4f, 0.4f, 0.35f));
				widgets.add(lbl);
				Button btnKickPlayer = new Button("Kick",
						new Rectangle(window_size.x / 2 + 300, 200 + 110 * i, 100, 100));
				btnKickPlayer.set_text_color(Color.RED);
				btnKickPlayer.set_click_callback(new Runnable() {
					@Override
					public void run() {
						((LocalCore) core).kickPlayer(guests.get(idxPlayer));
						guests.remove(idxPlayer);
						rebuild_gui();
					}
				});
				widgets.add(btnKickPlayer);
			}
		}

		Button btnStart = new Button(Language.START.get_text(),
				new Rectangle(camera.viewportWidth - 300, camera.viewportHeight - 200, 200, 100));
		if (savedGame == null) {
			btnStart.set_click_callback(new Runnable() {
				@Override
				public void run() {
					int islandSize;
					try {
						islandSize = tf_value_size.length() > 0 ? Integer.parseInt(tf_value_size) : 5;
					}catch(NumberFormatException e) {
						islandSize = 5;
					}
					int seed;
					try {
						 seed = tf_value_seed.length() > 0  ? Integer.parseInt(tf_value_seed): ((int) Math.random() * 100) + 1;
					}catch(NumberFormatException e) {
						seed = ((int) Math.random() * 100) + 1;
					}
					String user_name = tf_value_name.length() > 0 ? tf_value_name : "Anonymous";

					((LocalCore) core).changePlayerProps(0, user_name, playerColor);
					try {
						((LocalCore) core).create_new_map(islandSize, seed,
								new float[] { 0.f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f },
								cb_value_is_circle ? GeneratorType.CIRCLE : GeneratorType.HEXAGON, // something
								Integer.parseInt(tf_value_random_houses), Integer.parseInt(tf_value_resource_houses));
						((LocalCore) core).init_game();

						Vector2 newCameraPos = Map.index_to_position(new Vector2i(Map.map_size_x / 2, Map.map_size_y / 2));
						framework.camera.position.x = newCameraPos.x;
						framework.camera.position.y = newCameraPos.y;
						framework.update_view(false);
					}catch(NumberFormatException e) {
						System.err.println("Entered String instead of Number");
						tf_value_random_houses = "1";
						tf_value_resource_houses = "1";
					}
				}
			});
		} else {
			btnStart.set_click_callback(new Runnable() {
				@Override
				public void run() {
					if (guests.size() + 1 == savedGame.getPlayer().size()) {
						((LocalCore) core).startLoadedGame();
					}
				}
			});
		}

		widgets.add(btnStart);

		Button btnBack = new Button(Language.BACK.get_text(), new Rectangle(20, window_size.y - 70, 100, 50));
		btnBack.adjustWidth(5);
		btnBack.set_click_callback(new Runnable() {
			@Override
			public void run() {
				build_lobby();
			}
		});
		widgets.add(btnBack);
	}

	public void build_menu() {
		Label lblWindow = new Label("", new Rectangle(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(0.2f, 0.2f, 0.2f, 0.75f));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new Rectangle(window_size.x - 70, 25, 40, 40));
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
				mode = GUIMode.GAME;
				rebuild_gui();
			}
		});
		widgets.add(btnClose);

		if (menuMode == null || menuMode == MenuMode.MENU) {
			System.out.println(core.getClass().getName());
			System.out.println(core.getClass());
			if (core.getClass().getName() == "com.catangame.catan.core.LocalCore") {
				Button btnSave = new Button(Language.SAVE.get_text(),
						new Rectangle(window_size.x / 2 - 150, 200, 300, 50));
				btnSave.set_click_callback(new Runnable() {
					@Override
					public void run() {
						menuMode = MenuMode.SAVE;
						rebuild_gui();
					}
				});
				widgets.add(btnSave);
			}

			Button btnLobby = new Button(Language.EXIT.get_text(), new Rectangle(window_size.x / 2 - 150, 260, 300, 50));
			btnLobby.set_click_callback(new Runnable() {
				@Override
				public void run() {
					mode = GUIMode.LOBBY;
					state.mode = GameMode.main_menu;
					framework.reset_game();
					resetData(false);
					rebuild_gui();
				}
			});
			widgets.add(btnLobby);
		} else if (menuMode == MenuMode.SAVE) {
			final LocalFilehandler fileHandler = new LocalFilehandler();
			final TextField tfGameName = new TextField(new Rectangle(window_size.x - 800, window_size.y - 100, 300, 40));
			tfGameName.set_text(tf_game_name);
			tfGameName.set_input_callback(new Runnable() {
				@Override
				public void run() {
					tf_game_name = tfGameName.get_text();
				}
			});
			widgets.add(tfGameName);
			Button btnSaveGame = new Button(Language.SAVE.get_text(),
					new Rectangle(window_size.x - 400, window_size.y - 100, 150, 40));
			btnSaveGame.adjustWidth(5);
			btnSaveGame.set_click_callback(new Runnable() {
				@Override
				public void run() {
					show_informative_hint(Language.SAVED_GAME, "");
					((LocalCore) core).saveGame(tf_game_name);
					mode = GUIMode.GAME;
					menuMode = MenuMode.MENU;
					rebuild_gui();
				}
			});
			widgets.add(btnSaveGame);


			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					allGames = fileHandler.getAllGames();
					int i = 0;
					for (final SavedGame game : allGames) {
						Button btnGame = new Button(game.getName(),
								new Rectangle(window_size.x / 2 - 500, 100 + 80 * i, 500, 60));
						btnGame.set_click_callback(new Runnable() {
							@Override
							public void run() {
								tfGameName.set_text(game.getName());
							}
						});
						widgets.add(btnGame);
						i++;
					}
				}
			});


		}
	}

	public void build_load_window() {
		destroy_widgets();
		mode = GUIMode.LOAD;
		LocalFilehandler fileHandler = new LocalFilehandler();
		List<SavedGame> allGames = fileHandler.getAllGames();
		int i = 0;
		for (final SavedGame tempGame : allGames) {
			Button btnGame = new Button(tempGame.getName(),
					new Rectangle(window_size.x / 2 - 150, 200 + 80 * i, 400, 60));
			btnGame.set_click_callback(new Runnable() {
				@Override
				public void run() {
					mode = GUIMode.HOST_LOBBY;
					savedGame = tempGame;
					rebuild_gui();
					((LocalCore) core).loadGame(savedGame);
				}
			});
			widgets.add(btnGame);
			i++;
		}
		Button btnBack = new Button(Language.BACK.get_text(), new Rectangle(20, window_size.y - 70, 100, 50));
		btnBack.adjustWidth(5);
		btnBack.set_click_callback(new Runnable() {
			@Override
			public void run() {
				build_lobby();
			}
		});
		widgets.add(btnBack);
	}

	public void buildEndScreen() {
		destroy_widgets();
		int winnerID = 0; //TODO get winner ID from Core??
		for (Player p : this.player) {
			if (p.getScore() > this.player.get(winnerID).getScore()) {
				winnerID = p.getId();
			}
		}

		Label lblWinner = new Label(Language.WINNER.get_text(this.player.get(winnerID).getName()),
				new Rectangle(window_size.x / 2 - 100, 200, 200, 50));
		widgets.add(lblWinner);
		//Score board
		for (int i = 0; i < player.size(); i++) {
			Label lblPlayerScore = new Label(this.player.get(i).getName() + ": " + this.player.get(i).getScore(),
					new Rectangle(window_size.x / 2 - 100, 270 + 50 * i, 200, 50));
			lblPlayerScore.set_fill_color(player.get(i).getColor());
			widgets.add(lblPlayerScore);
		}
		//Button to Lobby
		Button btnLobby = new Button("Lobby", new Rectangle(window_size.x / 2 - 100, window_size.y - 100, 200, 50));
		btnLobby.set_click_callback(new Runnable() {
			@Override
			public void run() {
				state.mode = GameMode.main_menu;
				build_lobby();
				rebuild_gui();
			}
		});
		widgets.add(btnLobby);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) { // reset mouse position
			return check_on_click_widgets(framework.reverse_transform_position(screenX, screenY, camera));
		} else
			return false;
	}


	@Override
	public boolean keyTyped(char character) {
		if (activeTF != null) {
			activeTF.text_input(character);
			return true;
		} else
			return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (activeTF != null) {
			return activeTF.special_input(keycode);
		} else if (keycode == Keys.ESCAPE) {
			if (state.curr_action != LocalState.Action.idle) {
				switch_to_idle();
				return true;
			} else
				return false;
		}else  if(keycode == Keys.ENTER) {
			check_on_click_widgets(new Vector2(10, window_size.y - 68));
			//rebuild_gui();
			return true;
		}else
			return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(int i = 0; i < widgets.size(); i++) {
			Widget w = widgets.get(i);
			if(w.contains_cursor(framework.reverse_transform_position(screenX, screenY, camera))) {
				if(w.hasHover && !w.hovered) {
					w.enter();
				}
			}else {
				if(w.hasHover && w.hovered) {
					w.leave();
				}
			}
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(ScrollContainer sc : allScrollContainer) {
			if(sc != null && sc.isMouseInside(Gdx.input.getX(), Gdx.input.getY())) {
				sc.scrolled(amount);
				return true;
			}
		}
		return false;
	}

	void render(ShapeRenderer sr, SpriteBatch sb) {
		ArrayList<Widget> currWidgets = widgets;
		for (int i = 0; i < currWidgets.size(); i++) {
			try {
				currWidgets.get(i).addHoverEffect1();
				currWidgets.get(i).render(sr, sb);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(i);
			}

		}

	}

	// returns true if was on a widget
	private boolean check_on_click_widgets(Vector2 cursor_position) {
		boolean found_widget = false;
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
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
				//Neccessary for the trading window
				if (!(widget instanceof Label)) {
					//break;
				}
			}
		}
		if (found_widget) {
			return true;
		} else {
			if (activeTF != null) {
				activeTF.deactivate();
				activeTF = null;
				Gdx.input.setOnscreenKeyboardVisible(false);
			}
			return false;
		}
	}

	public void update_window_size(Vector2 size, OrthographicCamera camera) {
		window_size = size;
		this.camera = camera;
		rebuild_gui();
	}

	// mode switching functions
	public void switch_to_idle() {
		state.curr_action = LocalState.Action.idle;
		if (state.curr_player.equals(state.my_player_data.getName())) {
			state.isCurrentPlayer = true;
			enableAllButton(true);
			show_informative_hint(Language.DO_MOVE, "");
			rebuild_gui();
		} else {
			show_informative_hint(Language.OTHERS_MOVE, state.curr_player);
			state.isCurrentPlayer = false;
			enableAllButton(false);
			rebuild_gui();
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
		mode = GUIMode.GAME;
		rebuild_gui();
	}

	@Override
	public void show_trade_demand(TradeDemand tradeDemand) {
		tradeOffer = new TradeOffer();
		tradeOffer.setVendor_id(this.id);
		tradeOffer.setDemanderID(tradeDemand.get_demander_id());
		tradeOffer.setDemandedResources(tradeDemand.getOfferedResources());
		tradeOffer.setOfferedresources(tradeDemand.getWantedResources());
		this.tradeDemand = tradeDemand;
		mode = GUIMode.TRADE_VENDOR;
		rebuild_gui();
	}

	@Override
	public void addTradeOffer(TradeOffer tradeOffer) {
		allTradeOffer.add(tradeOffer);
		rebuild_gui();
	}

	@Override
	public void closeTradeWindow() {
		mode = GUIMode.GAME;
		tradeOffer = null;
		tradeDemand = null;
		allTradeOffer.clear();

		switch_to_idle();
		rebuild_gui();
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public void show_kicked(String name) {
		if(name == this.tf_value_name) {
			mode = GUIMode.LOBBY;
			resetData(false);
			rebuild_gui();
		}else {
			guests.remove(name);
		}

	}

	public void showIpInLobby(String ip) {
		this.serverIP = ip;
		rebuild_gui();
	}

	@Override
	public void showAllPossibleNames(List<Player> player) {
		for (Player p : player) {
			allPossiblePlayer.add(p);
		}
		rebuild_gui();
	}

	@Override
	public void showEndScreen(int winnerID, List<Player> player) {
		this.player = player;
		state.mode = GameMode.end_screen;
		mode = GUIMode.END_SCREEN;
		rebuild_gui();
	}

	@Override
	public void showDevelopmentCardWindow(DevCard card) {
		state.devCard = card;
		showDevelopmentCards = false;
		mode = GUIMode.DEV_CARD;
		rebuild_gui();
	}

	@Override
	public void showToMuchResourcesWindow(int numToRemove) {
		mode = GUIMode.TO_MUCH_RESOURCES;
		state.numToRemove = numToRemove;
		rebuild_gui();
	}

	@Override
	public void showMoveRobber() {
		show_informative_hint(Language.MOVE_ROBBER, "");
		state.curr_action = Action.moveRobber;
		enableAllButton(false);
	}

	public void enableAllButton(boolean enabled) {
		buttonsEnabled = enabled;
		btnBuildCity.set_enabled(enabled);
		btnBuildStreet.set_enabled(enabled);
		btnBuildVillage.set_enabled(enabled);
		btnBuyDevelopmentCard.set_enabled(enabled);
		btnFinishedMove.set_enabled(enabled);
		btnTrade.set_enabled(enabled);
	}

	@Override
	public void showSteelResource(List<Player> surroundingPlayers) {
		state.surroundingPlayers = surroundingPlayers;
		mode = GUIMode.STEEL_RESOURCE;
		buildSteelResource();
	}

	@Override
	public void showDemandDeclined(int id) {
		state.player_data.get(id).declinedOffer = true;
		rebuild_gui();
	}

	public void showAllJoinableGames(List<Packet.JoinableGame> list) {
		// TODO Auto-generated method stub
		this.allJoinableGames = list;
		mode = GUIMode.JOINABLE_GAMES;
		rebuild_gui();
	}

	public void showGuestLobby(String gameName) {
		mode = GUIMode.GUEST_LOBBY;
		state.gameName = gameName;
		rebuild_gui();
	}

	private void resetData(boolean textFields) {
		// gui data
		activeTF = null;
		showChatTf = false;
		showDevelopmentCards = false;
		messages = new LinkedList<Message>();
		allScrollContainer = new ArrayList<ScrollContainer>();

		// lobby
		savedGame = null;
		guests = new ArrayList<String>();
		allPossiblePlayer = new ArrayList<Player>();
		idxPlayer = 0;
		tf_value_seed = "" + (int) (Math.random() * Integer.MAX_VALUE);

		buttonsEnabled = true;

		//Trading
		tradeDemand = null;
		tradeOffer = null;
		allTradeOffer = new ArrayList<TradeOffer>();

		//Menu
		allGames = null;
		allJoinableGames = null;
		//End Screen
		player = null;

		if(textFields) {
			serverIP = "";
			tf_value_ip = "127.0.0.1";
			tf_value_name = "Anonymous";
			tf_value_size = "5";
			tf_value_random_houses = "1";
			tf_value_resource_houses = "1";
			cb_value_is_circle = false;
			cbValueIsLocal = true;
			lbl_value_info = "";
			lbl_value_dice = "0";
			tf_game_name = "";
			color_pkr_hue = (float) Math.random();
			playerColor = Color.RED;
			onlineLobby = false;
			btnJoinText = Language.JOIN_GAME.get_text();
		}
	}

	@Override
	public void addNewMessage(Message msg) {
		this.messages.add(msg);
		rebuild_gui();
	}

	public void showConnectionLost(String playerName) {
		this.mode = GUIMode.CONNECTION_LOST;
		this.lostConnectionPlayerName = playerName;
		if(mode == GUIMode.HOST_LOBBY) {
			guests.remove(playerName);
			rebuild_gui();
		}else {
			final PopUp p = new PopUp(Language.CONNECTION_LOST.get_text(playerName), new Rectangle(50, Gdx.graphics.getHeight()/2 - 125, Gdx.graphics.getWidth() - 100, 250));
			p.set_font(FontMgr.getFont(30));
			p.setFontColor(new com.badlogic.gdx.graphics.Color(158/255, 31/255, 31/255, .95f));
			widgets.add(p);
			Button btnWait = new Button(Language.WAIT.get_text(), new Rectangle(100, p.get_size().y - 60, 60, 50));
			btnWait.adjustWidth(10);
			btnWait.set_click_callback(new Runnable() {
				@Override
				public void run() {
					widgets.remove(p);
				}
			});
			p.addWidget(btnWait);
			Button btnBackToLobby = new Button(Language.BACK_TO_LOBBY.get_text(), new Rectangle(300, p.get_size().y - 60, 160, 50));
			btnBackToLobby.adjustWidth(10);
			btnBackToLobby.set_click_callback(new Runnable() {
				@Override
				public void run() {
					core.clientLeaveGame(id);
					mode = GUIMode.LOBBY;
					state.mode = GameMode.main_menu;
					framework.reset_game();
					resetData(false);
					rebuild_gui();
				}
			});
			p.addWidget(btnBackToLobby);
		}

	}
}
