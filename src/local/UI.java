package local;

import java.util.ArrayList;
import local.LocalState.GameMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;
import org.jsfml.window.event.Event;

public class UI {
	// local state
	LocalState state;
	private LocalLogic logic;
	Game game;
	Vector2f window_size;
	
	
	private int numGuests = 0;
	// Start Window
	//private ArrayList<Button> buttons = new ArrayList<Button>(); TODO
	private ArrayList<Widget> widgets = new ArrayList<Widget>();

	private TextField activeTF;

	// fonts
	Font std_font;

	UI(LocalLogic logic, Game game) {
		this.logic = logic;
		this.state = logic.state;
		this.game = game;

		state.mode = GameMode.main_menu;
	}

	void init(Font std_font) {
		this.std_font = std_font;

		Button.set_default_font(std_font);
		Button.set_default_text_color(new Color(100, 70, 100));
		Button.set_default_outline_color(Color.BLACK);
		Button.set_default_outline_highlight_color(new Color(200, 140, 200));
		TextField.set_default_font(std_font);
		TextField.set_default_text_color(new Color(20, 20, 20));
		Label.set_default_font(std_font);
		Label.set_default_text_color(new Color(20, 50, 50));
		Label.set_default_fill_color(new Color(0,0,0,0));
		Label.set_default_outline_color(new Color(0, 0, 0, 0));
		Label.set_default_outline_highlight_color(new Color(200, 140, 200));
		build_lobby();
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
				// TODO DEBUG
				game.init_host_game();
				state.mode = GameMode.game;
				build_game_menu();
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
				//TODO transfer this to Start new Game
				game.init_host_game();
				build_host_lobby_window();
				System.out.println("load Game");
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
		widgets.add(btn);

		btn = new Button(Language.EXIT.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
				System.out.println("Exit game");
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

		Button btn = new Button(Language.NO_TEXT.get_text(), new FloatRect(20, 300, 250, 50));
		btn.set_text_size(30);
		widgets.add(btn);

		TextField tf = new TextField(new FloatRect(20, 360, 250, 50));
		tf.set_text_size(30);
		widgets.add(tf);
	}
	
	public void build_join_menu() {
		destroy_widgets();
		
		float mm_tf_width = 400;
		float mm_tf_height = 50;
		float mm_tf_spacing = 20;
		
		TextField tfIp = new TextField(new FloatRect(0, 0, mm_tf_width, mm_tf_height));
		tfIp.set_text_size(30);
		tfIp.setText("192.168.2.118");
		widgets.add(tfIp);
		
		TextField tfName = new TextField(new FloatRect(0,0, mm_tf_width, mm_tf_height));
		tfName.set_text_size(30);
		tfName.setText("Julian");
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
					game.init_guest_game(tfIp.get_text().trim(), tfName.get_text().trim());
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
		lbl = new Label(Language.SETTINGS.get_text(), new FloatRect(row0, 10, 100, 100));
		widgets.add(lbl);
		
		//Row1 ==> members
		lbl = new Label(Language.MEMBERS.get_text(), new FloatRect(row1, 10, 100, 100));
		widgets.add(lbl);
		
		Button btnStart = new Button(Language.START.get_text(), new FloatRect(window_size.x - 300,window_size.y - 200,200,100));
		btnStart.set_click_callback(new Runnable() {
			@Override
			public void run() {
				state.mode = GameMode.game;
				logic.messageStartGame();
			}			
		});
		widgets.add(btnStart);
	}
	
	public void build_game_surface() {
		destroy_widgets();
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
	}

	public LocalLogic getLogic() {// TODO rebind LocalDataServer to LocalLogic instead of ui (or both)
		return logic;
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