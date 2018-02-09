package local;
import java.util.ArrayList;

import org.jsfml.graphics.Color;
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
	
	//__Lobby Stuff__
	//Start Window
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	private TextField activeTF;
	private Button btnCreatNewGame;
	private Button btnJoinGame;
	private Button btnLoadGame;
	private Button btnOptions;
	private Button btnExitGame;
	//Create Game Window
	private Button btnStartGame;
	
	//joinGame Window
	private Button btnJoin;
	
	
	// fonts
	Font std_font;

	UI(LocalLogic logic, Game game) {
		this.logic = logic;
		this.state = logic.state;
		this.game = game;
	}

	void init(Font std_font) {
		this.std_font = std_font;

		// DEBUG
		game.init_host_game();

	}
	
	public void initLobby(RenderTarget target) {
		this.btnCreatNewGame = new Button(Language.CREATE_NEW_GAME.get_text());
		this.btnCreatNewGame.setOnClick(new ClickEvent() {
			@Override
			public void handle() {
				System.out.println("Start new game");
				openStartNewGame();
			}
		});
		this.btnCreatNewGame.setPosition(0);
		buttons.add(btnCreatNewGame);
		widgets.add(btnCreatNewGame);
		
		this.btnJoinGame = new Button(Language.JOIN_GAME.get_text());
		this.btnJoinGame.setOnClick(new ClickEvent() {
			@Override
			public void handle() {
				System.out.println("Join Game");				
			}
		});
		this.btnJoinGame.setPosition(1);
		buttons.add(btnJoinGame);
		widgets.add(btnJoinGame);
		
		this.btnLoadGame = new Button(Language.LOAD_GAME.get_text());
		this.btnLoadGame.setOnClick(new ClickEvent() {
			@Override
			public void handle() {
				System.out.println("load Game");
			}
		});
		this.btnLoadGame.setPosition(2);
		buttons.add(btnLoadGame);
		widgets.add(btnLoadGame);
		
		this.btnOptions = new Button(Language.OPTIONS.get_text());
		this.btnOptions.setOnClick(new ClickEvent() {
			@Override
			public void handle() {
				System.out.println("Options");				
			}
		});
		this.btnOptions.setPosition(3);
		buttons.add(btnOptions);
		widgets.add(btnOptions);
		
		this.btnExitGame = new Button(Language.EXIT.get_text());
		this.btnExitGame.setOnClick(new ClickEvent() {
			@Override
			public void handle() {
				System.out.println("Exit game");
			}
		});
		this.btnExitGame.setPosition(4);
		buttons.add(btnExitGame);
		widgets.add(btnExitGame);
	}

	boolean handle_event(View view, Event evt) {
		Vector2f mouseClick;
		if (evt.type == Event.Type.MOUSE_BUTTON_PRESSED) {
			if (evt.asMouseButtonEvent().button == Mouse.Button.LEFT) { // reset mouse position
				mouseClick = new Vector2f((float) evt.asMouseButtonEvent().position.x - view.getSize().x/2 + view.getCenter().x, 
											(float) evt.asMouseButtonEvent().position.y - view.getSize().y/2 + view.getCenter().y);
				checkOnClickWidgets(mouseClick);
			}
		}

		return false;
	}

	void update() {

	}

	void render(RenderTarget target) {
		for(Widget widget : widgets) {
			widget.render(target);
		}
	}

	public LocalLogic getLogic() {
		return logic;
	}

	private void checkOnClickWidgets(Vector2f mouseClick) {
		for(Widget widget : widgets) {
			if(widget.checkClicked(mouseClick)) {
				if(widget.toString().contains("TextField")) {
					activeTF = (TextField) widget;
				}
				break;
			}
		}
	}

}