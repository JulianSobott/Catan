package local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import core.LocalCore;
import core.LocalFilehandler;
import core.Map;
import core.Player;
import core.Map.GeneratorType;
import data.DevelopmentCard;
import data.Language;
import data.Resource;
import data.SavedGame;
import local.LocalState.GameMode;
import local.TradeDemand.Vendor;
import local.gui.Button;
import local.gui.Checkbox;
import local.gui.ColorPicker;
import local.gui.Label;
import local.gui.TextField;
import local.gui.Widget;
import superClasses.Core;
import superClasses.UI;

public class LocalUI extends UI {
	enum GUIMode {
		LOBBY, JOIN, LOAD, MENU, GUEST_LOBBY, HOST_LOBBY, GAME, TRADE_DEMAND, TRADE_VENDOR, END_SCREEN;
	}

	//TODO Either implement this modes or delete this enum!!
	enum WindowMode {
		TRADE_VENDOR, TRADE_DEMANDER, SHOW_DEV_CARDS;
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
	private Vector2f window_size;
	private View view;

	// fonts
	private Font std_font;

	// gui data
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	private TextField activeTF;
	private boolean showDevelopmentCards = false;

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

	//Trading
	private TradeDemand tradeDemand;
	private TradeOffer tradeOffer;
	private List<TradeOffer> allTradeOffer = new ArrayList<TradeOffer>();

	private String serverIP = "";
	private String tf_value_ip = "127.0.0.1";
	private String tf_value_name = "Anonymous";
	private String tf_value_seed = "" + (int) (Math.random() * Integer.MAX_VALUE);
	private String tf_value_size = "5";
	private String tf_value_random_houses = "1";
	private String tf_value_resource_houses = "1";
	private boolean cb_value_is_circle = false;
	private String lbl_value_info = "";
	private String lbl_value_dice = "0";
	private String tf_game_name = "";
	private float color_pkr_hue = (float) Math.random();
	private Color hostPlayerColor = Color.RED;

	//End Screen 
	private List<Player> player;
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
		} else if (mode == GUIMode.TRADE_DEMAND) {
			build__demander_trade_window();
		} else if (mode == GUIMode.TRADE_VENDOR) {
			build_vendor_trade_window();
		} else if (mode == GUIMode.LOAD) {
			build_load_window();
		} else if (mode == GUIMode.MENU) {
			build_menu();
		}else if(mode == GUIMode.END_SCREEN) {
			buildEndScreen();
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
				framework.init_host_game();
				build_host_lobby_window();
			}
		});
		widgets.add(btn);

		btn = new Button(Language.JOIN_GAME.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				build_join_menu();
			}
		});
		widgets.add(btn);

		btn = new Button(Language.LOAD_GAME.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				framework.init_host_game();
				build_load_window();
			}
		});

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
		float cards_width = 120;
		float orientation_anchor = (window_size.x / 2) - 200;
		Widget.set_default_outline_color(Color.WHITE);
		Iterator<Entry<Resource, Integer>> it = state.my_player_data.get_all_resources().entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			java.util.Map.Entry<Resource, Integer> pair = (java.util.Map.Entry<Resource, Integer>) it.next();
			Resource r = pair.getKey();
			int num = pair.getValue();
			Label lblResource = new Label(Language.valueOf(r.name()).get_text() + ":\n" + num,
					new FloatRect(5, 200 + 100 * i, cards_width, 80));
			lblResource.set_fill_color(r.get_color());
			widgets.add(lblResource);
			i++;
		}
		Widget.set_default_outline_color(Color.TRANSPARENT);

		//player Development Cards
		Button btnShowDevelopmentCards = new Button(Language.DEVELOPMENT_CARD.get_text(),
				new FloatRect(5, 100, 220, 50));
		btnShowDevelopmentCards.adjustWidth(5);
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
			for (final DevelopmentCard card : state.my_player_data.getDevelopmentCards()) {
				Button btnCard = new Button(card.toString(), new FloatRect(205, 100 + 75 * i, 300, 70));
				btnCard.set_fill_color(new Color(50, 77, 172, 230));
				btnCard.set_click_callback(new Runnable() {
					@Override
					public void run() {
						core.playCard(id, card);
					}
				});
				widgets.add(btnCard);
				i++;
			}
		}

		// finished move button
		btnFinishedMove = new Button(Language.FINISHED_MOVE.get_text(),
				new FloatRect(window_size.x - 155, window_size.y - 155, 150, 70));
		btnFinishedMove.adjustWidth(5);
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

		btnBuyDevelopmentCard = new Button(Language.DEVELOPMENT_CARD.get_text(), new FloatRect(
				orientation_anchor + (buttons_width + 5) * pos_count++, window_size.y - 80, buttons_width + 100, 70));
		btnBuyDevelopmentCard.adjustWidth(5);
		btnBuyDevelopmentCard.set_click_callback(new Runnable() {
			@Override
			public void run() {
				core.buyDevelopmentCard(id);
			}
		});
		widgets.add(btnBuyDevelopmentCard);
		//dice result
		lblDiceResult = new Label(lbl_value_dice, new FloatRect(10, 10, 50, 50));
		lblDiceResult.set_fill_color(new Color(170, 170, 170));
		widgets.add(lblDiceResult);

		// info label
		lblInfo = new Label(lbl_value_info, new FloatRect(10, window_size.y - 50, 100, 50));
		widgets.add(lblInfo);

		//Trade Button
		btnTrade = new Button(Language.TRADE.get_text(), new FloatRect(100, 10, 70, 40));
		btnTrade.adjustWidth(5);
		btnTrade.set_text_size(25);
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
		Button btnMenu = new Button("...", new FloatRect(window_size.x / 2 - 25, 10, 80, 40));
		btnMenu.set_text_size(100);
		btnMenu.set_text_position(window_size.x / 2 - 10, -60);
		btnMenu.set_fill_color(new Color(250, 250, 250, 40));
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
		Label lblWindow = new Label("", new FloatRect(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(50, 50, 50, 190));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new FloatRect(window_size.x - 70, 25, 40, 40));
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
				mode = GUIMode.GAME;
				core.closeTrade();
				rebuild_gui();
			}
		});
		widgets.add(btnClose);
		if (tradeDemand.getVendor() == null) {
			Button btnAskBank = new Button("Bank", new FloatRect(200, window_size.y / 4, 200, 100));
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
				if (playerResources.get(r) >= 4) {
					enabled = true;
					break;
				} else {
					enabled = false;
				}
			}
			btnAskBank.set_enabled(enabled);
			widgets.add(btnAskBank);
			Button btnAskPlayer = new Button("Player", new FloatRect(500, window_size.y / 4, 200, 100));
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
			Label lblWantedResources = new Label("Select Wanted Resopurces", new FloatRect(50, 100, 200, 50));
			lblWantedResources.set_text_color(Color.WHITE);
			widgets.add(lblWantedResources);
			
			float start1 = 30;
			float containerWidth1 = window_size.x / 4;
			float lblWidth1 = containerWidth1 / 3;
			float btnWidth1 = containerWidth1 / 6 - 5;
			float lblHeight = 50;
			for (final Resource r : Resource.values()) {
				if (r != Resource.OCEAN) {
				
					final Button btnWantedResource = new Button((Language.valueOf(r.toString()).toString()),
							new FloatRect(start1, 170 + (lblHeight + btnSpace) * i, lblWidth1, lblHeight));
					if (tradeDemand.getWantedResources().containsKey(r)) {
						btnWantedResource.set_fill_color(r.get_color());
						btnWantedResource.set_text_color(Color.WHITE);
						btnWantedResource.set_outline_color(Color.GREEN);
					} else {
						Color c = r.get_color();
						btnWantedResource.set_text_color(Color.BLACK);
						btnWantedResource.set_fill_color(new Color(c.r, c.g, c.b, 100));
						btnWantedResource.set_outline_color(new Color(0, 0, 0, 0));
					}
					btnWantedResource.set_click_callback(new Runnable() {
						@Override
						public void run() {
							if (tradeDemand.getWantedResources().containsKey(r)) {
								Color c = r.get_color();
								btnWantedResource.set_fill_color(new Color(c.r, c.g, c.b, 100));
								btnWantedResource.set_text_color(Color.BLACK);
								btnWantedResource.set_outline_color(new Color(0, 0, 0, 0));
								tradeDemand.removeWantedResource(r);
							} else {
								btnWantedResource.set_fill_color(r.get_color());
								btnWantedResource.set_text_color(Color.WHITE);
								btnWantedResource.set_outline_color(Color.GREEN);
								tradeDemand.addWantedResource(r);
							}
							rebuild_gui();
						}
					});
					widgets.add(btnWantedResource);
					
					
					Button btnMinus = new Button("-",
							new FloatRect(start1 + btnSpace + lblWidth1, 190 + (lblHeight + btnSpace) * i, btnWidth1, 30));
					btnMinus.set_text_position(start1 + btnSpace + lblWidth1 + btnWidth1 / 2,
							190 + (lblHeight + btnSpace) * i);
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
					Label lblNumresources = new Label(num, new FloatRect(start1 + btnSpace * 2 + lblWidth1 + btnWidth1,
							180 + (lblHeight + btnSpace) * i, btnWidth1, 50));
					lblNumresources.set_fill_color(Color.WHITE);
					widgets.add(lblNumresources);
					Button btnPlus = new Button("+", new FloatRect(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2,
							190 + (lblHeight + btnSpace) * i, btnWidth1, 30));
					btnPlus.set_text_position(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2.5f,
							195 + (lblHeight + btnSpace) * i);
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
			float start2 = window_size.x / 4+20;
			Label lblOfferedResources = new Label("Select Resources That you offer", new FloatRect(370, 100, 300, 50));
			lblOfferedResources.set_text_color(Color.WHITE);
			widgets.add(lblOfferedResources);
			for (final Resource r : Resource.values()) {

				if (r != Resource.OCEAN && state.my_player_data.get_resources(r) >= 1
						&& tradeDemand.getVendor() == Vendor.PLAYER
						|| r != Resource.OCEAN && state.my_player_data.get_resources(r) >= 4
								&& tradeDemand.getVendor() == Vendor.BANK) {
					String resourceString = (Language.valueOf(r.toString()).toString()) + ": "
							+ state.my_player_data.get_resources(r);
					final Button btnOfferedResource = new Button(resourceString,
							new FloatRect(start2, (btnHeight + btnSpace) * i + 200, 150, btnHeight));
					if (tradeDemand.getOfferedResources().containsKey(r)) {
						btnOfferedResource.set_fill_color(r.get_color());
						btnOfferedResource.set_text_color(Color.WHITE);
						btnOfferedResource.set_outline_color(Color.RED);
					} else {
						Color c = r.get_color();
						btnOfferedResource.set_fill_color(new Color(c.r, c.g, c.b, 100));
						btnOfferedResource.set_outline_color(new Color(0, 0, 0, 0));
					}
					btnOfferedResource.set_click_callback(new Runnable() {
						@Override
						public void run() {
							if (tradeDemand.getOfferedResources().containsKey(r)) {
								Color c = r.get_color();
								btnOfferedResource.set_fill_color(new Color(c.r, c.g, c.b, 100));
								btnOfferedResource.set_text_color(Color.BLACK);
								btnOfferedResource.set_outline_color(new Color(0, 0, 0, 0));
								tradeDemand.removeOfferedResource(r);
							} else {
								btnOfferedResource.set_fill_color(r.get_color());
								btnOfferedResource.set_text_color(Color.WHITE);
								btnOfferedResource.set_outline_color(Color.RED);
								tradeDemand.addOfferedResource(r);
							}
						}
					});
					widgets.add(btnOfferedResource);
					i++;
				}
			}

			Label lblAllOffers = new Label("All Offers", new FloatRect(window_size.x / 2, 150, 300, 50));
			lblAllOffers.set_text_color(Color.WHITE);
			widgets.add(lblAllOffers);
			//show all offers
			i = 0;
			for (final TradeOffer offer : allTradeOffer) {
				//Offer Label
				Label lblOfferID = new Label("Offer " + i,
						new FloatRect(window_size.x / 2, 200 + (110 + 20) * i, 300, 50));
				lblOfferID.set_text_color(state.player_data.get(offer.getVendor_id()).getColor());
				widgets.add(lblOfferID);
				Label lblOfferContainer = new Label("",
						new FloatRect(window_size.x / 2, 200 + (110 + 20) * i, window_size.x / 2 - 30, 110));
				lblOfferContainer.set_fill_color(new Color(255, 255, 255, 80)); //TODO Maybe change to player color
				widgets.add(lblOfferContainer);
				//Demanded resources?? Neccessary??

				//Offered resources
				int j = 0;
				Label lblOfferedResource;
				for (Resource r : offer.getOfferedResources().keySet()) {
					lblOfferedResource = new Label(r.toString() + ": " + offer.getOfferedResources().get(r),
							new FloatRect(window_size.x / 2 + 150 * j, 250 + (110 + 20) * i, 150, 50));
					lblOfferedResource.set_fill_color(r.get_color());
					lblOfferedResource.set_text_size(30);
					widgets.add(lblOfferedResource);
					j++;
				}
				//Button accept
				Button btnAccept = new Button("Accept",
						new FloatRect(window_size.x - 110, 250 + (110 + 20) * i, 80, 50));
				btnAccept.set_click_callback(new Runnable() {
					@Override
					public void run() {
						java.util.Map<Resource, Integer> demandedResources = new HashMap<Resource, Integer>();
						for (Resource r : tradeDemand.getWantedResources().keySet()) {
							demandedResources.put(r, tradeDemand.getWantedResources().get(r));
						}
						offer.setDemandedResources(demandedResources);
						core.acceptOffer(offer);
					}
				});
				widgets.add(btnAccept);
				Button btnReject = new Button("X", new FloatRect(window_size.x - 80, 200 + (110 + 20) * i, 50, 30));
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
				i++;
			}
			//Button Send demand
			Button btnSendDemand = new Button("Send demand",
					new FloatRect(window_size.x - 300, window_size.y - 100, 200, 70));
			btnSendDemand.set_fill_color(Color.GREEN);
			btnSendDemand.set_click_callback(new Runnable() {
				@Override
				public void run() {
					core.new_trade_demand(tradeDemand);
				}
			});
			widgets.add(btnSendDemand);
		}

	}

	public void build_vendor_trade_window() {
		Label lblWindow = new Label("", new FloatRect(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(50, 50, 50, 190));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new FloatRect(window_size.x - 70, 25, 40, 40));
		btnClose.set_click_callback(new Runnable() {
			@Override
			public void run() {
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
		Label lblAllWantedResources = new Label("Player want these resources: ", new FloatRect(30, 40, 200, 50));
		lblAllWantedResources.set_text_color(Color.WHITE);
		widgets.add(lblAllWantedResources);
		for (Resource r : tradeDemand.wantedResources.keySet()) {
			Label lblWantedResource = new Label(r.toString(),
					new FloatRect((btnResourceWidth + btnSpace) * i + 50, 100, btnResourceWidth, 50));
			lblWantedResource.set_fill_color(r.get_color());
			widgets.add(lblWantedResource);
			i++;
		}
		//Show all offered Resources
		Label allOfferedResources = new Label("Player Offers these resources for trading: ",
				new FloatRect(window_size.x / 2 + 90, 40, 200, 50));
		allOfferedResources.set_text_color(Color.WHITE);
		widgets.add(allOfferedResources);
		i = 0;
		for (Resource r : tradeDemand.offeredResources.keySet()) {
			Label lblWantedResource = new Label(r.toString(), new FloatRect(
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
				new FloatRect(start1, 200, 300, 50));
		lblWantedResourcesFromDemander.set_text_color(Color.WHITE);
		widgets.add(lblWantedResourcesFromDemander);
		i = 0;
		for (final Resource r : Resource.values()) {
			if (tradeDemand.offeredResources.containsKey(r)) {
				Label lblResource = new Label(r.toString(),
						new FloatRect(start1, 270 + (lblHeight + btnSpace) * i, lblWidth1, lblHeight));
				lblResource.set_text_color(r.get_color());
				widgets.add(lblResource);
				Button btnMinus = new Button("-",
						new FloatRect(start1 + btnSpace + lblWidth1, 290 + (lblHeight + btnSpace) * i, btnWidth1, 30));
				btnMinus.set_text_position(start1 + btnSpace + lblWidth1 + btnWidth1 / 2,
						290 + (lblHeight + btnSpace) * i);
				btnMinus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.substractOfferedResource(r);
						rebuild_gui();
					}
				});
				widgets.add(btnMinus);
				String num = "0";
				if (tradeOffer.getOfferedResources().containsKey(r)) {
					num = tradeOffer.getOfferedResources().get(r).toString();
				}
				Label lblNumresources = new Label(num, new FloatRect(start1 + btnSpace * 2 + lblWidth1 + btnWidth1,
						280 + (lblHeight + btnSpace) * i, btnWidth1, 50));
				lblNumresources.set_fill_color(Color.WHITE);
				widgets.add(lblNumresources);
				Button btnPlus = new Button("+", new FloatRect(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2,
						290 + (lblHeight + btnSpace) * i, btnWidth1, 30));
				btnPlus.set_text_position(start1 + btnSpace * 3 + lblWidth1 + btnWidth1 * 2.5f,
						295 + (lblHeight + btnSpace) * i);
				btnPlus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.addOfferedResource(r);
						rebuild_gui();
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
				new FloatRect(start, 200, 300, 50));
		lblOfferedResourcesFromMe.set_text_color(Color.WHITE);
		widgets.add(lblOfferedResourcesFromMe);
		i = 0;
		for (final Resource r : Resource.values()) {
			if (tradeDemand.wantedResources.containsKey(r)) {
				Label lblResource = new Label(r.toString(),
						new FloatRect(start, 270 + (lblHeight + btnSpace) * i, lblWidth, lblHeight));
				lblResource.set_text_color(r.get_color());
				widgets.add(lblResource);
				Button btnMinus = new Button("-",
						new FloatRect(start + btnSpace + lblWidth, 290 + (lblHeight + btnSpace) * i, btnWidths, 30));
				btnMinus.set_text_position(start + btnSpace + lblWidth + btnWidths / 2,
						290 + (lblHeight + btnSpace) * i);
				btnMinus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.substractWantedResource(r);
						rebuild_gui();
					}
				});
				widgets.add(btnMinus);
				String num = Integer.toString(tradeDemand.getWantedResources().get(r));
				if (tradeOffer.getDemandedResources().containsKey(r)) {
					num = tradeOffer.getDemandedResources().get(r).toString();
				}
				Label lblNumresources = new Label(num, new FloatRect(start + btnSpace * 2 + lblWidth + btnWidths,
						280 + (lblHeight + btnSpace) * i, btnWidths, 50));
				lblNumresources.set_fill_color(Color.WHITE);
				widgets.add(lblNumresources);
				Button btnPlus = new Button("+", new FloatRect(start + btnSpace * 3 + lblWidth + btnWidths * 2,
						290 + (lblHeight + btnSpace) * i, btnWidths, 30));
				btnPlus.set_text_position(start + btnSpace * 3 + lblWidth + btnWidths * 2.5f,
						295 + (lblHeight + btnSpace) * i);
				btnPlus.set_click_callback(new Runnable() {
					@Override
					public void run() {
						tradeOffer.addWantedResource(r);
						rebuild_gui();
					}
				});
				widgets.add(btnPlus);
				i++;
			}
		}

		//Send offers
		//All own Resources
		i = 0;
		for (Resource r : Resource.values()) {
			if (r != Resource.OCEAN) {
				String str = Language.valueOf(r.toString()).get_text() + ": " + state.my_player_data.get_resources(r);
				Label lblResource = new Label(str, new FloatRect(40 + i * 130, window_size.y - 110, 110, 70));
				lblResource.set_fill_color(r.get_color());
				widgets.add(lblResource);
				i++;
			}
		}
		//Show all own offers (if multiple are made)
		Label lblAllOffers = new Label("All own offers", new FloatRect(window_size.x / 2, 150, 300, 50));
		lblAllOffers.set_text_color(Color.WHITE);
		widgets.add(lblAllOffers);
		i = 0;
		for (TradeOffer offer : allTradeOffer) {
			//Offer Label
			Label lblOfferID = new Label("Offer " + i, new FloatRect(window_size.x / 2, 200 + (150) * i, 300, 50));
			lblOfferID.set_text_color(state.player_data.get(offer.getVendor_id()).getColor());
			lblOfferID.set_text_size(23);
			widgets.add(lblOfferID);
			Label lblOfferContainer = new Label("",
					new FloatRect(window_size.x / 2, 200 + (150 + 3) * i, window_size.x / 2 - 30, 150));
			lblOfferContainer.set_fill_color(new Color(255, 255, 255, 80)); //TODO Maybe change to player color
			lblOfferContainer.setOutlineColor(Color.BLACK);
			widgets.add(lblOfferContainer);

			//Offered resources
			int j = 0;
			Label lblOfferedResource;
			for (Resource r : offer.getOfferedResources().keySet()) {
				lblOfferedResource = new Label(r.toString() + ": " + offer.getOfferedResources().get(r),
						new FloatRect(window_size.x / 2 + 120 * j, 250 + (150) * i, 120, 50));
				lblOfferedResource.set_fill_color(r.get_color());
				lblOfferedResource.set_text_size(23);
				lblOfferedResource.set_text_color(Color.RED);
				widgets.add(lblOfferedResource);
				j++;
			}
			//Wanted Resources
			j = 0;
			Label lblWantedResource;
			for (Resource r : offer.getDemandedResources().keySet()) {
				lblWantedResource = new Label(r.toString() + ": " + offer.getDemandedResources().get(r),
						new FloatRect(window_size.x / 2 + 120 * j, 300 + (150) * i, 120, 50));
				lblWantedResource.set_fill_color(r.get_color());
				lblWantedResource.set_text_size(23);
				lblWantedResource.set_text_color(Color.GREEN);
				widgets.add(lblWantedResource);
				j++;
			}
			i++;
		}
		//Button Send offer
		Button btnSendOffer = new Button("Send offer",
				new FloatRect(window_size.x - 300, window_size.y - 110, 200, 70));
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
				tradeOffer.setDemandedResources(t.getWantedResources());
				rebuild_gui();
			}
		});
		widgets.add(btnSendOffer);
	}

	public void build_join_menu() {
		destroy_widgets();
		mode = GUIMode.JOIN;

		float mm_tf_width = 400;
		float mm_tf_height = 50;
		float mm_tf_spacing = 20;

		final TextField tfIp = new TextField(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
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

		final TextField tfName = new TextField(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
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

		final ColorPicker colorPicker = new ColorPicker(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		colorPicker.set_color(color_pkr_hue, 1.f, 0.9f);
		colorPicker.set_select_callback(new Runnable() {
			ColorPicker cp = colorPicker;

			@Override
			public void run() {
				color_pkr_hue = cp.get_hue();
				hostPlayerColor = cp.get_color();
			}
		});
		hostPlayerColor = colorPicker.get_color();
		widgets.add(colorPicker);

		final Label lblConnecting;
		lblConnecting = new Label("Try to Connect to: " + tfIp.get_text(),
				new FloatRect(window_size.x / 2, window_size.y - 200, 100, 50));
		lblConnecting.set_visible(false);

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

		for (int i = 0; i < widgets.size(); i++) {
			Widget temp_tf = widgets.get(i);
			temp_tf.set_position(new Vector2f((window_size.x - mm_tf_width) * 0.5f + 200,
					(window_size.y - (mm_tf_height + mm_tf_spacing) * widgets.size()) * 0.5f
							+ (mm_tf_height + mm_tf_spacing) * i));
		}

		Label lbl = new Label("Enter IP: ",
				new FloatRect((window_size.x - mm_tf_width) * 0.5f, tfIp.get_position().y, mm_tf_width, mm_tf_height));
		widgets.add(lbl);
		lbl = new Label("Enter Name: ", new FloatRect((window_size.x - mm_tf_width) * 0.5f, tfName.get_position().y,
				mm_tf_width, mm_tf_height));
		widgets.add(lbl);
		widgets.add(lblConnecting);

		Button btnBack = new Button(Language.BACK.get_text(), new FloatRect(20, window_size.y - 70, 100, 50));
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
				Button btnPName = new Button(p.getName(), new FloatRect(window_size.x / 2 - 100, 50 + 70 * i, 200, 60));
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
			Label lbl = new Label("Successfully joined Lobby with: ", new FloatRect(0, 0, 100, 100));
			widgets.add(lbl);
			int i = 0;
			for (String guestName : guests) {
				Label lblPlayer = new Label(guestName, new FloatRect(window_size.x / 2, 200 + 110 * i, 400, 100));
				Color color = new Color(100, 100, 100, 100);
				lblPlayer.set_fill_color(color);
				widgets.add(lblPlayer);
				i++;
			}

		}
	}

	public void build_host_lobby_window() {
		destroy_widgets();
		mode = GUIMode.HOST_LOBBY;
		Label lblHostIp = new Label(serverIP, new FloatRect(window_size.x / 2, 0, 100, 30));
		widgets.add(lblHostIp);
		float column0 = 0;
		float column1 = window_size.x / 2 > 300 ? window_size.x / 2 : 300;
		float height_anchor = 10;
		float textfield_width = 200;
		float textfield_height = 50;
		float row_count = 0;

		if (savedGame != null) {
			Label lblGameName = new Label(Language.SETTINGS.get_text() + savedGame.getName(),
					new FloatRect(10, 10, 200, 50));
			lblGameName.set_text_color(Color.GREEN);
			widgets.add(lblGameName);

		} else {
			Label lbl = new Label(Language.SETTINGS.get_text() + ": ", new FloatRect(10,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);

			//Row0 ==> Settings

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
			lbl = new Label(Language.RANDOM_HOUSES.get_text() + ": ", new FloatRect(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.RESOURCE_HOUSES.get_text() + ": ", new FloatRect(column0,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);
			lbl = new Label(Language.IS_CIRCLE.get_text(), new FloatRect(column0 + 180 + textfield_height + 5,
					height_anchor + (textfield_height + 10) * row_count++ - 5, textfield_width, textfield_height));
			widgets.add(lbl);

			row_count = 2;
			final TextField tfMapSize = new TextField(new FloatRect(column0 + 200,
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

			final TextField tfSeed = new TextField(new FloatRect(column0 + 200,
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
					tf_value_seed = "" + (int) (Math.random() * Integer.MAX_VALUE);
					tfSeed.set_text(tf_value_seed);
				}
			});
			widgets.add(btnRandom);

			final TextField tfName = new TextField(new FloatRect(column0 + 200,
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

			final ColorPicker colorPicker = new ColorPicker(new FloatRect(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			colorPicker.set_color(color_pkr_hue, 1.f, 0.9f);
			colorPicker.set_select_callback(new Runnable() {
				ColorPicker cp = colorPicker;

				@Override
				public void run() {
					color_pkr_hue = cp.get_hue();
					hostPlayerColor = cp.get_color();
				}
			});
			hostPlayerColor = colorPicker.get_color();
			widgets.add(colorPicker);

			final TextField tfRandomHouses = new TextField(new FloatRect(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			tfRandomHouses.set_text_color(new Color(20, 20, 20));
			tfRandomHouses.set_text(tf_value_random_houses);
			tfRandomHouses.set_input_callback(new Runnable() {
				TextField textField = tfRandomHouses;

				@Override
				public void run() {
					tf_value_random_houses = textField.get_text();
				}
			});
			widgets.add(tfRandomHouses);

			final TextField tfResourceHouses = new TextField(new FloatRect(column0 + 200,
					height_anchor + (textfield_height + 10) * row_count++, textfield_width, textfield_height));
			tfResourceHouses.set_text_color(new Color(20, 20, 20));
			tfResourceHouses.set_text(tf_value_resource_houses);
			tfResourceHouses.set_input_callback(new Runnable() {
				TextField textField = tfResourceHouses;

				@Override
				public void run() {
					tf_value_resource_houses = textField.get_text();
				}
			});
			widgets.add(tfResourceHouses);

			final Checkbox cbCircleMap = new Checkbox(new FloatRect(column0 + 200,
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
		}
		//Row1 ==> members
		Label lbl = new Label(Language.MEMBERS.get_text(), new FloatRect(column1, 10, 100, 100));
		widgets.add(lbl);

		if (savedGame != null) {
			int i = 0;
			for (Player p : savedGame.getPlayer()) {
				if (i == 0) {
					Label lblHost = new Label(p.getName(), new FloatRect(window_size.x / 2 - 500, 200, 400, 100));
					lblHost.set_fill_color(p.getColor());
					widgets.add(lblHost);
				} else {
					idxPlayer = i - 1;
					Label lblPlayer = new Label(p.getName(), new FloatRect(window_size.x / 2, 200 + 110 * i, 400, 100));
					Color color = new Color(100, 100, 100, 100);
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
					lbl = new Label(guest, new FloatRect(view.getSize().x / 2 > 200 ? view.getSize().x / 2 : 200,
							200 + 110 * i, 400, 100));
					lbl.set_fill_color(new Color(250, 100, 100, 200));
					widgets.add(lbl);
					Button btnKickPlayer = new Button("Kick",
							new FloatRect(window_size.x / 2 + 300, 200 + 110 * i, 100, 100));
					btnKickPlayer.set_text_color(Color.RED);
					btnKickPlayer.set_click_callback(new Runnable() {
						@Override
						public void run() {
							((LocalCore) core).kickPlayer(guest);
							guests.remove(guest);
							rebuild_gui();
						}
					});
					widgets.add(btnKickPlayer);
					widgets.add(btnKickPlayer);
					i++;
				}

			}
		} else {
			for (int i = 0; i < guests.size(); i++) {
				idxPlayer = i;
				lbl = new Label(guests.get(i), new FloatRect(view.getSize().x / 2 > 200 ? view.getSize().x / 2 : 200,
						200 + 110 * i, 400, 100));
				lbl.set_fill_color(new Color(100, 100, 100, 90));
				widgets.add(lbl);
				Button btnKickPlayer = new Button("Kick",
						new FloatRect(window_size.x / 2 + 300, 200 + 110 * i, 100, 100));
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
				new FloatRect(view.getSize().x - 300, view.getSize().y - 200, 200, 100));
		if (savedGame == null) {
			btnStart.set_click_callback(new Runnable() {
				@Override
				public void run() {
					int islandSize = tf_value_size.length() > 0 ? Integer.parseInt(tf_value_size) : 5;
					int seed = tf_value_seed.length() > 0 ? Integer.parseInt(tf_value_seed)
							: ((int) Math.random() * 100) + 1;
					String user_name = tf_value_name.length() > 0 ? tf_value_name : "Anonymous";
					Color user_color = hostPlayerColor;

					((LocalCore) core).changePlayerProps(0, user_name, user_color);
					((LocalCore) core).create_new_map(islandSize, seed,
							new float[] { 0.f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f },
							cb_value_is_circle ? GeneratorType.CIRCLE : GeneratorType.HEXAGON, // something
							Integer.parseInt(tf_value_random_houses), Integer.parseInt(tf_value_resource_houses));
					((LocalCore) core).init_game();

					framework.game_view
							.setCenter(Map.index_to_position(new Vector2i(Map.map_size_x / 2, Map.map_size_y / 2)));
					framework.update_view();

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

		Button btnBack = new Button(Language.BACK.get_text(), new FloatRect(20, window_size.y - 70, 100, 50));
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
		Label lblWindow = new Label("", new FloatRect(30, 30, window_size.x - 60, window_size.y - 60));
		lblWindow.set_fill_color(new Color(50, 50, 50, 190));
		widgets.add(lblWindow);
		Button btnClose = new Button("X", new FloatRect(window_size.x - 70, 25, 40, 40));
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
			if (core.getClass().getName() == "core.LocalCore") {
				Button btnSave = new Button(Language.SAVE.get_text(),
						new FloatRect(window_size.x / 2 - 150, 200, 300, 50));
				btnSave.set_click_callback(new Runnable() {
					@Override
					public void run() {
						menuMode = MenuMode.SAVE;
						rebuild_gui();
					}
				});
				widgets.add(btnSave);
			}
		} else if (menuMode == MenuMode.SAVE) {
			LocalFilehandler fileHandler = new LocalFilehandler();
			final TextField tfGameName = new TextField(new FloatRect(window_size.x - 800, window_size.y - 100, 300, 40));
			tfGameName.set_text(tf_game_name);
			tfGameName.set_input_callback(new Runnable() {
				@Override
				public void run() {
					tf_game_name = tfGameName.get_text();
				}
			});
			widgets.add(tfGameName);
			Button btnSaveGame = new Button(Language.SAVE.get_text(),
					new FloatRect(window_size.x - 400, window_size.y - 100, 150, 40));
			btnSaveGame.set_click_callback(new Runnable() {
				@Override
				public void run() {
					System.out.println("Button Save");
					((LocalCore) core).saveGame(tf_game_name);
				}
			});
			widgets.add(btnSaveGame);

			List<SavedGame> allGames = fileHandler.getAllGames();
			int i = 0;
			for (final SavedGame game : allGames) {
				Button btnGame = new Button(game.getName(),
						new FloatRect(window_size.x / 2 - 500, 100 + 80 * i, 500, 60));
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
	}

	public void build_load_window() {
		destroy_widgets();
		mode = GUIMode.LOAD;
		LocalFilehandler fileHandler = new LocalFilehandler();
		List<SavedGame> allGames = fileHandler.getAllGames();
		int i = 0;
		for (final SavedGame tempGame : allGames) {
			Button btnGame = new Button(tempGame.getName(),
					new FloatRect(window_size.x / 2 - 150, 200 + 80 * i, 400, 60));
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
	}
	
	public void buildEndScreen() {
		destroy_widgets();
		int winnerID = 0; //TODO get winner ID from Core??
		for(Player p : this.player) {
			if(p.getScore() > this.player.get(winnerID).getScore()) {
				winnerID = p.getId();
			}
		}
		
		Label lblWinner = new Label(Language.WINNER.get_text(this.player.get(winnerID).getName()), new FloatRect(window_size.x /2 - 100, 200, 200, 50));
		widgets.add(lblWinner);
		//Score board
		for (int i = 0; i < player.size(); i++) {
			Label lblPlayerScore = new Label(
					this.player.get(i).getName() + ": " + this.player.get(i).getScore(),
					new FloatRect(window_size.x/2 - 100, 270 + 50 * i, 200, 50));
			lblPlayerScore.set_fill_color(player.get(i).getColor());
			widgets.add(lblPlayerScore);
		}
		//Button to Lobby
		Button btnLobby = new Button("Lobby", new FloatRect(window_size.x /2 - 100, window_size.y- 100, 200, 50));
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
		ArrayList<Widget> currWidgets = widgets;
		for (int i = 0; i < currWidgets.size(); i++) {
			try {
				currWidgets.get(i).render(target);
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(i);
			}		
		}
	}

	// returns true if was on a widget
	private boolean check_on_click_widgets(Vector2f cursor_position) {
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
					break;
				}
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
			btnTrade.set_enabled(false);
			btnBuyDevelopmentCard.set_enabled(false);
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
		tradeOffer.setDemandedResources(tradeDemand.getWantedResources());
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
	public void show_kicked() {
		mode = GUIMode.LOBBY;
		rebuild_gui();
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

}
