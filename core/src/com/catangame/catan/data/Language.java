package com.catangame.catan.data;
public enum Language {

	NO_TEXT("NO TEXT FOUND", "KEIN TEXT GEFUNDEN"),
	//Start Window
	CREATE_NEW_GAME("Create new Game", "Neues Spiel erstellen"),
	JOIN_GAME("Join game", "Spiel beitreten"),
	LOAD_GAME("Load game", "Spiel laden"),
	OPTIONS("Options", "Einstellungen"),
	EXIT("Exit game", "Spiel beenden"),
	BACK("Back", "Zur\u00FCck"),
	//Create game window 
	START("Start game", "Spiel starten"),
	RANDOM("Random", "Zuf\u00E4llig"),
	//Join game window
	JOIN("Join game", "Spiel beitreten"),
	//Lobby Window
	SETTINGS("Settings for the Game: ", "Einstellungen f\u00FCr das Spiel"),
	MEMBERS("Members", "Mitspieler"), 
	MAP_SIZE("Map size", "Karten Gr\u00F6\u00DFe"), 
	SEED("Seed", "Seed"),
	YOUR_NAME("Your name", "Dein Name"), 
	YOUR_COLOR("Your color", "Deine Farbe"),
	RANDOM_HOUSES("Random houses", "Zuf\u00E4llige h\u00E4user"), 
	RESOURCE_HOUSES("Resource houses", "Resourcenh\u00E4user"),
	IS_CIRCLE("Circle map", "Kreiskarte"),
	//In game
	MENU("MENU", "MEN\u00DC"), 
	DICE("Dice", "W\u00FCrfeln"),
	BUILD_VILLAGE("Village", "Siedlung"),
	BUILD_CITY("City", "Stadt"),
	BUILD_STREET("Street", "Stra\u00DFe"),
	BUILD_FREE_STREETS("Build {} free Streets", "Baue {} kostenlose Stra�en"),
	DO_MOVE("Make your move", "Mache deinen Zug"),
	OTHERS_MOVE("It's {}s move", "{} ist am Zug"),
	SELECT_BUILD_PLACE("Select a building site", "W\u00E4hle den Bauplatz aus"),
	FINISHED_MOVE("Finish move", "Zug beenden"),
	OF("of", "von"),
	WOOD("Wood", "Holz"),
	WOOL("Wool", "Wolle"),
	GRAIN("Grain", "Getreide"),
	CLAY("Clay", "Ton"),
	ORE("Ore", "Erz"), 
	
	TRADE("Trade", "Handeln"), 
	BANK("Bank", "Bank"),
	PLAYER("Player", "Spieler"),
	CMD_SELECT_WANTED("Select wanted resources", "W\00C4hle Resourcen die du willst"),
	CMD_SELECT_OFFERED("select resources that you offer", "W\00C4hle Resourcen, die du anbietest"),
	ALL_OFFERS("All offers", "Alle Angebote"),
	OFFER("Offer", "Angebot"),
	ACCEPT("Accept", "Annehmen"),
	TO_MUCH_RESOURCES("You have {} resources overmuch", "Du hast {} Resourcen zu viel"),
	SAVE("Save game", "Spiel speichern"), 
	DEVELOPMENT_CARD("Development card", "Entwicklungskarte"),
	SAVED_GAME("Successfully saved game", "Erfolgreich Spiel gespeichert"),
	KNIGHT("Knight", "Ritter"), 
	POINT("Point", "Punkt"),
	FREE_RESOURCES("2 free Resources", "2 kostenlose Resourcen"),
	FREE_STREETS("2 free streets","2 kostenlose Stra\00DFen"),
	MONOPOL("Monopol", "Monopol"),
	SEND("Send", "Abschicken"),
	MOVE_ROBBER("Move Robber", "R\u00E4uber versetzen"),
	//After Game
	WINNER("{} won this game", "{} hat gewonnen"),    ;


	
	// data
	String english;
	String german;

	// management

	enum Type {
		english, german,
	}

	static private Type curr_language = Type.german;

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

	// replaces "{}" with \p replacements
	public String get_text(String replacement) {
		String txt = curr_language == Type.english ? english : german;
		return txt.replace("{}", replacement);
	}
}
