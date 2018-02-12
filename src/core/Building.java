package core;

import org.jsfml.system.Vector2i;

public class Building {
	public enum Type {
		STREET,
		VILLAGE,
		CITY,
	};
	private Type type;
	private int map_layer; // defines to which map layer the building belongs
	private Vector2i position;// 2D index in the layer

	Building(Type type, int map_layer, Vector2i position) {
		this.type = type;
		this.map_layer = map_layer;
		this.position = position;
	}

	Type get_type() {
		return type;
	}
	int get_map_layer() {
		return map_layer;
	}
	Vector2i get_position() {
		return position;
	}
}
