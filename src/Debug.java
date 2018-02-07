

public class Debug {

	public static void main(String[] args) {
		LocalDataServer server = new LocalDataServer();
		RemoteDataClient client = new RemoteDataClient("192.168.2.118");
		Packet p = new Packet("Some Code");
		client.sendMessage(p);
		server.messageTo(0, "Message From Server to Client");
	}

}
