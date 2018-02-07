enum Language {

	HELLO_WORLD("Hello world", "Hallo Welt");


	
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
