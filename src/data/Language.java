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
	MEMBERS("MEMBERS", "Mitspieler"), 
	MAP_SIZE("Map size", "Karten Größe"), 
	SEED("Seed", "Seed"),
	YOUR_NAME("Your name", "Dein Name"),
	//In game
	DICE("Dice", "Würfeln"),
	BUILD_VILLAGE("Build village", "Siedlung bauen"),
	BUILD_CITY("Build city", "Stadt bauen"),
	BUILD_STREET("Build street", "Stra�e bauen"), 
	FINISHED_TURN("Finish turn", "Zug beenden");


	
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
