package data;
public enum Language {

	NO_TEXT("NO TEXT FOUND", "KEIN TEXT GEFUNDEN"),
	//Start Window
	CREATE_NEW_GAME("Create new Game", "Neues Spiel erstellen"),
	JOIN_GAME("Join game", "Spiel beitreten"),
	LOAD_GAME("Load game", "Spiel laden"),
	OPTIONS("Options", "Einstellungen"),
	EXIT("Exit game", "Spiel beenden"),
	//Create game window 
	START("Start game", "Spiel starten"),
	//Join game window
	JOIN("Join game", "Spiel beitreten"),
	//Lobby Window
	SETTINGS("Settings for the Game: ", "Einstellungen für das Spiel"),
	MEMBERS("Members", "Mitspieler"), 
	MAP_SIZE("Map size", "Karten Größe"), 
	SEED("Seed", "Seed"),
	YOUR_NAME("Your name", "Dein Name"),
	//In game
	DICE("Dice", "Würfeln"),
	BUILD_VILLAGE("Village", "Siedlung"),
	BUILD_CITY("City", "Stadt"),
	BUILD_STREET("Street", "Straße"),
	DO_MOVE("Make your move", "Mache deinen Zug"),
	SELECT_BUILD_PLACE("Select a building site", "Wähle den Bauplatz aus"),
	FINISHED_MOVE("Finish move", "Zug beenden"),
	OF("of", "von"),
	WOOD("Wood", "Holz"),
	WOOL("Wool", "Wolle"),
	GRAIN("Grain", "Getreide"),
	CLAY("Clay", "Ton"),
	ORE("Ore", "Erz");


	
	// data
	String english;
	String german;

	// management

	enum Type {
		english, german,
	}

	static private Type curr_language = Type.english;

	Language(String english, String german) {
		this.english = english;
		this.german = german;
	}

	void set_language(Type type) {
		curr_language = type;
	}

	public String get_text() {
		return curr_language == Type.english ? english : german;
	}
}
