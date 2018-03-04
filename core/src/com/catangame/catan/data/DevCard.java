package com.catangame.catan.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DevCard implements Serializable{
	public enum Type{
		KNIGHT, //Move the robber and take a Card from a corresponding player
		POINT, //Get 1||2 points
		FREE_RESOURCES, //Get 2 free Resources
		FREE_STREETS, //Get 2 free street
		MONOPOL, //Get all Cards from all players from a specific resource 
	}
	
	public Type type;
	public Serializable data;
	
	public DevCard(Type type, Serializable data) {
		this.type = type;
		this.data = data;
	}
	
	public DevCard(Type type) {
		this.type = type;
	}

	public static class Knight implements Serializable{
		
	}
	
	public static class Point implements Serializable{
		
	}
	
	public static class FreeResources implements Serializable{
		public Map<Resource, Integer> newResources;
		public int remainedFreeresources = 2; //TODO Maybe add this to constructor
		public FreeResources() {
			newResources = new HashMap<Resource, Integer>();
		}
		public void addResource(Resource r) {
			if(newResources.containsKey(r)) {
				newResources.put(r, newResources.get(r) + 1);
			}else {
				newResources.put(r, 1);
			}
		}
	}
	
	public static class FreeStreets implements Serializable{
		
	}
	
	public static class Monopol implements Serializable{
		
	}
	
}
