package network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2i;
import core.Building;
import core.Player;
import data.Field;

import local.LocalPlayer;
import local.LocalState.GameMode;

public class Packet implements Serializable {

	public static class Scoreboard implements Serializable {
		List<LocalPlayer> player = new ArrayList<LocalPlayer>();

		public Scoreboard(List<LocalPlayer> player2) {
			this.player = player2;
		}

		public List<LocalPlayer> getPlayer() {
			return this.player;
		}
	}
	
	public static class CreateNewMap implements Serializable{
		private int mapSize;
		private int seed;
		
		public CreateNewMap(int mapSize, int seed) {
			this.mapSize = mapSize;
			this.seed = seed;
		}
		public int getMapSize() {
			return this.mapSize;
		}
		public int getSeed() {
			return this.seed;
		}
	}
	public static class DiceResult implements Serializable {
		private byte result;

		public DiceResult(byte diceResult) {
			this.result = diceResult;
		}

		public byte getDiceResult() {
			return this.result;
		}
	}

	public static class Build implements Serializable {
		private Vector2i position;
		private int idPlayer = 0;

		public Build(Vector2i position) {
			this.position = position;
		}

		public Build(int idPlayer, Vector2i position) {
			this.position = position;
			this.idPlayer = idPlayer;
		}

		public Vector2i getPosition() {
			return this.position;
		}

		public int getIdPlayer() {
			return this.idPlayer;
		}
	}

	public static class Trade implements Serializable {
		//TODO implement parameters
		public Trade() {
		}
	}

	public static class Name implements Serializable {
		private String name;
		private Color color;

		public Name(String name, Color color) {
			this.name = name;
			this.color = color;
		}

		public String getName() {
			return this.name;
		}

		public Color getColor() {
			return this.color;
		}
	}

	public static class New_Map implements Serializable {
		private Field[][] fields;

		public New_Map(Field[][] fields) {
			this.fields = fields;
		}

		public Field[][] getFields() {
			return this.fields;
		}
	}
	
	public static class PlayerData implements Serializable {
		private Player player;

		public PlayerData(Player player) {
			this.player = player;
		}

		public Player getPlayer() {
			return this.player;
		}
	}

	public static class UpdateBuildings implements Serializable {
		private Map<Integer, List<Building>> buildings;

		public UpdateBuildings(Map<Integer, List<Building>> buildings) {
			this.buildings = buildings;
		}

		public Map<Integer, List<Building>> getBuildings() {
			return this.buildings;
		}
	}
	public static class NEW_MODE implements Serializable {
		private GameMode mode;

		public NEW_MODE(GameMode mode) {
			this.mode = mode;
		}

		public GameMode getgameMode() {
			return this.mode;
		}
	}
	
	private Command cmd;
	Serializable data;

	String debugString;

	public Packet(Command cmd) {
		this.cmd = cmd;
		this.data = new Build(new Vector2i(1, 1));
	}

	public Packet(Command cmd, Serializable data) {
		this.cmd = cmd;
		this.data = data;
	}
	

	public Command getCommand() {
		return this.cmd;
	}

	public Packet(String str) {
		this.cmd = Command.STRING;
		this.debugString = str;
	}

	public String getDebugString() {
		return this.debugString;
	}
}
