import org.jsfml.system.Vector2i;

class Core {
	// data server
	LocalDataServer data_server;


	// map
	Map map = new Map();

	Core( LocalDataServer data_server ) {
		this.data_server = data_server;
	}

	public void dice() {
		// TODO Auto-generated method stub
		
	}

	public void buildRequest(int id, Command buildType, Vector2i position) {
		
	}
}
