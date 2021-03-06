package com.catangame.catan.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.catangame.catan.utils.Color;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.DevCard.Type;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.LocalPlayer;

public class Player implements Serializable {
	private int id = 0;
	private String name;
	private int score = 0;
	private Color color;
	protected Action action;
	Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	List<Building> buildings = new LinkedList<Building>();
	List<DevCard> developmentCards = new ArrayList<DevCard>();
	public List<Resource> harbours = new ArrayList<Resource>();
	private int numKnights = 0;
	private boolean longestStreet = false;
	
	public enum Action{
		MOVING_ROBBER,
	}

	public Player(String name, int id, Color color) {
		this.name = name;
		this.color = color;
		this.id = id;
		for (Resource r : Resource.values()) {
			if(r != Resource.OCEAN && r != Resource.DESERT){
				resources.put(r, 0);
			}		
		}
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return this.score;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public int get_resources(Resource r) {
		return resources.get(r);
	}
	public Map<Resource, Integer> get_all_resources(){
		return this.resources;
	}
	public void add_resource(Resource r, int count) {
		resources.put(r, resources.get(r) + count);
	}

	public void take_resource(Resource r, int count) {
		resources.put(r, resources.get(r) - count);
	}
	public void addScore(int score) {
		this.score += score;
	}
	public void update_score() {
		score = 0;
		for (Building b : buildings) {
			if (b.get_type() == Building.Type.VILLAGE) {
				score++;
			} else if (b.get_type() == Building.Type.CITY) {
				score += 2;
			}
		}
	}

	public void addDevelopmentCard(DevCard card) {
		this.developmentCards.add(card);
	}
	
	public List<DevCard> getDevelopmentCards(){
		return this.developmentCards;
	}

	public void addKnight() {
		this.numKnights++;
	}
	
	public int getNumKnights() {
		return this.numKnights;
	}
	
	public void setLongestStreet(boolean hasLongest) {
		this.longestStreet = hasLongest;
		this.score += hasLongest ? 1 : -1;
	}
	public boolean hasLongestStreet() {
		return this.longestStreet;
	}
	public LocalPlayer toLocalPlayer() {
		return new LocalPlayer(name, score, color, id);

	}
}
