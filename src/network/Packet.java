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

	public static  class PlayerList implements Serializable {
		private List<Player> player;
		public PlayerList(List<Player> player) {
			this.player = player;
		}
		public List<Player> getPlayer(){
			return this.player;
		}
	}

	public static class TradeOffer implements Serializable {
		local.TradeOffer tradeOffer = new local.TradeOffer();
		public TradeOffer(local.TradeOffer tradeOffer) {
			this.tradeOffer = tradeOffer;
		}
		public local.TradeOffer getTradeOffer(){
			return this.tradeOffer;
		}
	}

	public static class TradeDemand implements Serializable {
		local.TradeDemand tradeDemand = new local.TradeDemand();
		public TradeDemand(local.TradeDemand tradeDemand) {
			this.tradeDemand = tradeDemand;
		}
		public local.TradeDemand getTradeDemand(){
			return this.tradeDemand;
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
	
	public static class ID implements Serializable{
		private int id;
		public ID(int id) {
			this.id = id;
		}
		public int getID() {
			return this.id;
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
		public Map<Resource, Integer> getResources() {
			return this.player.get_all_resources();
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
	public void setString(String str) {
		this.debugString = str;
	}
}
