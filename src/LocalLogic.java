import org.jsfml.system.Vector2i;

// TODO name?
class LocalLogic{
	LocalState state;

	LocalLogic() {
		state = new LocalState();
	}

	public void diceResult(byte diceresult) {
		// TODO Auto-generated method stub
		System.out.println("Dice result at Client: " + diceresult);
	}

	public void build(int idPlayer, Command buildType, Vector2i position) {
		// TODO Auto-generated method stub
		
	}

}