package core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import data.Resource;

public class Player implements Serializable{
	private String name;
	private int score = 0;
	private int id;
	Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	List<Building> buildings = new LinkedList<Building>();

	Player(String name, int id) {
		this.name = name;
		this.id = id;
		for( Resource r : Resource.values()) {
			resources.put(r, 0);
		}
	}
	
	public int getID() {
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
	
}
