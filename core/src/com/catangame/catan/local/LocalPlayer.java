package com.catangame.catan.local;

import com.catangame.catan.data.Resource;
import com.catangame.catan.utils.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// This class should be used in "local" environment instead of core.Player
public class LocalPlayer implements Serializable{
	private String name;
	private int score = 0;
	private Color color;
	private Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	private int ID;
	
	public LocalPlayer(String name, int score, Color color, int ID) {
		this.name = name;
		this.score = score;
		this.color = color;
		this.ID = ID;
	}
	
	public String getName() {
		return this.name;
	}

	public int getScore() {
		return this.score;
	}

	public Color getColor() {
		return color;
	}
	
	public void setResources(Map<Resource, Integer> resources) {
		this.resources = resources;
	}
	
	public Map<Resource, Integer> getResources(){
		return this.resources;
	}

	public int getID() {
		return this.ID;
	}
}