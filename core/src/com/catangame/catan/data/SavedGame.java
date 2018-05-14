package com.catangame.catan.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.Player;

public class SavedGame implements Serializable{
	
	private static final long serialVersionUID = 4212023987438728657L;
	private List<Player> player = null;
	private Field[][] fields = null;
	private Date date;
	private String name;
	private int current_player;
	private Map<Vector2, Resource> harbours;

	public SavedGame(Field[][] fields, List<Player> player, Date date, int current_player, Map<Vector2, Resource> harbours) {
		this.fields = fields;
		this.player = player;
		this.date = date;
		this.current_player = current_player;
		this.harbours = harbours;
	}

	public List<Player> getPlayer() {
		return player;
	}

	public void setPlayer(List<Player> player) {
		this.player = player;
	}

	public Field[][] getFields() {
		return fields;
	}

	public void setFields(Field[][] fields) {
		this.fields = fields;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getCurrentPlayer() {
		return this.current_player;
	}
	
	public Map<Vector2, Resource> getHarbours(){
		return this.harbours;
	}
}
