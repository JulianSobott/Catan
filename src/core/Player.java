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
	Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	List<Building> buildings = new LinkedList<Building>();

	Player(String name) {
		this.name = name;
		for( Resource r : Resource.values()) {
			resources.put(r, 0);
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getScore() {
		return this.score;
	}
}
