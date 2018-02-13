package network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2i;

import data.Field;

import local.LocalPlayer;

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

	public static class DiceResult implements Serializable {
		private byte result;

		public DiceResult(byte result) {
			this.result = result;
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

		public Name(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
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
