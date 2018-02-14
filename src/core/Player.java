package core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;

import data.Resource;

public class Player implements Serializable {
	private int id = 0;// TODO set id at registration
	private String name;
	private int score = 0;
	private Color color;
	Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	List<Building> buildings = new LinkedList<Building>();

	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
		for (Resource r : Resource.values()) {
			resources.put(r, 0);
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

	public Color getColor() {
		return color;
	}

	public int get_resources(Resource r) {
		return resources.get(r);
	}

	public void add_resource(Resource r, int count) {
		resources.put(r, resources.get(r) + count);
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
}
