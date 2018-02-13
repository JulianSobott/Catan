package core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import data.Resource;

class Player {
	String name;
	int score = 0;
	Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	List<Building> buildings = new LinkedList<Building>();

	Player() {
		for( Resource r : Resource.values()) {
			resources.put(r, 0);
		}
	}
}
