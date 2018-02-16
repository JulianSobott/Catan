package network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector3i;

import core.Building;
import core.Player;
import data.Field;
import data.Resource;
import local.LocalPlayer;
import local.LocalState.GameMode;

public class Packet implements Serializable {

	public static class TradeDemand implements Serializable {
		local.TradeDemand tradeDemand = new local.TradeDemand();
		int demander_id;
		public TradeDemand(local.TradeDemand tradeDemand) {
			this.tradeDemand = tradeDemand;
		}
		public TradeDemand(int demander_id, local.TradeDemand tradeDemand2) {
			this.tradeDemand = tradeDemand;
			this.demander_id = demander_id;
		}
		public local.TradeDemand getTradeDemand(){
			return this.tradeDemand;
		}
		public int getDemanderID() {
			return this.demander_id;
		}
	}

	public static class Scoreboard implements Serializable {
		List<LocalPlayer> player = new ArrayList<LocalPlayer>();

		public Scoreboard(List<LocalPlayer> player2) {
			this.player = player2;
		}

		public List<LocalPlayer> getPlayer() {
			return this.player;
		}
	}

	public static class CreateNewMap implements Serializable {
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

	public static class BuildRequest implements Serializable {
		private Building.Type buildingType;
		private Vector3i position;

		public BuildRequest(Building.Type buildingType, Vector3i position) {
			this.buildingType = buildingType;
			this.position = position;
		}

		public Building.Type getBuildingType() {
			return buildingType;
		}

		public Vector3i getPosition() {
			return this.position;
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

	public static class NewBuilding implements Serializable {
		private int id;
		private Building building;

		public NewBuilding(int id, Building building) {
			this.id = id;
			this.building = building;
		}

		public int getID() {
			return id;
		}

		public Building getBuilding() {
			return building;
		}
	}

	public static class SetCurrUser implements Serializable {
		private String player;

		public SetCurrUser(String player) {
			this.player = player;
		}

		public String getPlayer() {
			return player;
		}
	}

	public static class NewMode implements Serializable {
		private GameMode mode;

		public NewMode(GameMode mode) {
			this.mode = mode;
		}

		public GameMode getGameMode() {
			return this.mode;
		}
	}

	private Command cmd;
	Serializable data;

	String debugString;

	public Packet(Command cmd) {
		this.cmd = cmd;
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
