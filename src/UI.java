
public class UI {
	// local state
	LocalState state;
	LocalLogic logic;

	UI(LocalLogic logic) {
		this.logic = logic;
		this.state = logic.state;
	}

}