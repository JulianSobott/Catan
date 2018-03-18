package com.catangame.catan.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.catangame.catan.utils.Color;
import com.catangame.catan.math.Vector3i;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.Building;
import com.catangame.catan.core.Player;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.Field;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.LocalPlayer;
import com.catangame.catan.local.LocalState.GameMode;

public class Packet implements Serializable {
	
	private static final long serialVersionUID = 10001L;
	
	public static class Position implements Serializable {
		private static final long serialVersionUID = 10002L;
		public Vector position;
		public Position(Vector position) {
			this.position = position;
		}
	}

	public static class Resouces implements Serializable {
		private static final long serialVersionUID = 10003L;
		public Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
		public Resouces(Map<Resource, Integer> resources) {
			this.resources = resources;
		}
	}

	public static class Num implements Serializable {
		private static final long serialVersionUID = 10004L;
		public Number num;
		public Num(Number num) {
			this.num = num;
		}
	}

	public static  class PlayerList implements Serializable {
		private static final long serialVersionUID = 10005L;
		private List<Player> player;
		public PlayerList(List<Player> player) {
			this.player = player;
		}
		public List<Player> getPlayer(){
			return this.player;
		}
	}

	public static class TradeOffer implements Serializable {
		private static final long serialVersionUID = 10006L;
		com.catangame.catan.local.TradeOffer tradeOffer = new com.catangame.catan.local.TradeOffer();
		public TradeOffer(com.catangame.catan.local.TradeOffer tradeOffer) {
			this.tradeOffer = tradeOffer;
		}
		public com.catangame.catan.local.TradeOffer getTradeOffer(){
			return this.tradeOffer;
		}
	}

	public static class TradeDemand implements Serializable {
		private static final long serialVersionUID = 10007L;
		com.catangame.catan.local.TradeDemand tradeDemand = new com.catangame.catan.local.TradeDemand();
		public TradeDemand(com.catangame.catan.local.TradeDemand tradeDemand) {
			this.tradeDemand = tradeDemand;
		}
		public com.catangame.catan.local.TradeDemand getTradeDemand(){
			return this.tradeDemand;
		}
	}

	public static class Scoreboard implements Serializable {
		private static final long serialVersionUID = 10008L;
		List<LocalPlayer> player = new ArrayList<LocalPlayer>();

		public Scoreboard(List<LocalPlayer> player2) {
			this.player = player2;
		}

		public List<LocalPlayer> getPlayer() {
			return this.player;
		}
	}

	public static class CreateNewMap implements Serializable {
		private static final long serialVersionUID = 10009L;
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
		private static final long serialVersionUID = 10010L;
		private byte result;

		public DiceResult(byte diceResult) {
			this.result = diceResult;
		}

		public byte getDiceResult() {
			return this.result;
		}
	}

	public static class BuildRequest implements Serializable {
		private static final long serialVersionUID = 10011L;
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
		private static final long serialVersionUID = 10012L;
		private int id;
		public ID(int id) {
			this.id = id;
		}
		public int getID() {
			return this.id;
		}
	}
	public static class Name implements Serializable {
		private static final long serialVersionUID = 10013L;
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
		private static final long serialVersionUID = 10014L;
		public Field[][] fields;
		public java.util.Map<Vector2, Resource> harbours;

		public New_Map(Field[][] fields, java.util.Map<Vector2, Resource> harbours) {
			this.fields = fields;
			this.harbours = harbours;
		}

		public Field[][] getFields() {
			return this.fields;
		}
	}

	public static class PlayerData implements Serializable {
		private static final long serialVersionUID = 10015L;
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
		private static final long serialVersionUID = 10016L;
		private Map<Integer, List<Building>> buildings;

		public UpdateBuildings(Map<Integer, List<Building>> buildings) {
			this.buildings = buildings;
		}

		public Map<Integer, List<Building>> getBuildings() {
			return this.buildings;
		}
	}

	public static class NewBuilding implements Serializable {
		private static final long serialVersionUID = 10017L;
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
		private static final long serialVersionUID = 10018L;
		private String player;

		public SetCurrUser(String player) {
			this.player = player;
		}

		public String getPlayer() {
			return player;
		}
	}

	public static class NewMode implements Serializable {
		private static final long serialVersionUID = 10019L;
		private GameMode mode;

		public NewMode(GameMode mode) {
			this.mode = mode;
		}

		public GameMode getGameMode() {
			return this.mode;
		}
	}
	
	public static class Developmentcard implements Serializable{
		private static final long serialVersionUID = 10020L;
		private DevCard card;
		public Developmentcard(DevCard card2) {
			this.card = card2;
		}
		public DevCard getCard() {
			return this.card;
		}
	}
	
	public static class ListData implements Serializable{
		public List list;
		public ListData(List list) {
			this.list = list;
		}
	}
	private Command cmd;
	public Serializable data;
	public int sender; //Own ClientGameID
	public int receiver; //0 = Host, else ClientGameID

	String debugString;

	public Packet(Command cmd) {
		this.cmd = cmd;
	}

	public Packet(Command cmd, Serializable data) {
		this.cmd = cmd;
		this.data = data;
	}
	
	public Packet(int sender, int receiver, Command cmd, Serializable data) {
		this.sender = sender;
		this.receiver = receiver;
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
