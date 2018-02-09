package local;

import java.util.ArrayList;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import local.LocalState.GameMode;

public class UI {
	// local state
	LocalState state;
	private LocalLogic logic;
	Game game;
	Vector2f window_size;

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
			}
		});
		widgets.add(btn);

		btn = new Button(Language.LOAD_GAME.get_text(), new FloatRect(0, 0, mm_button_width, mm_button_height));
		btn.set_click_callback(new Runnable() {
			@Override
			public void run() {
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