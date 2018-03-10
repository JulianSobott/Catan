package com.catangame.catan.data;
import com.catangame.catan.utils.Color;

public enum Resource {
	OCEAN(new Color(0, 0, 1, 0.4f)),// not a resource but is needed for the map
	WOOD(new Color(0.47f, 0.39f, 0, 1)),
	WOOL(new Color(0.2f, 0.63f, 0.2f, 1)),
	GRAIN(new Color(0.86f, 0.71f, 0, 1)),
	CLAY(new Color(0.63f, 0.31f, 0, 1)),
	ORE(new Color(0.47f, 0.47f, 0.47f, 1)),
	DESERT(new Color(0 , 0, 0, 251));

	// data
	private Color color;

	Resource(Color color) {
		this.color = color;
	}

	public Color get_color() {
		return color;
	}
}
