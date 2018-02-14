package core;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

public class Building {
	public enum Type {
		STREET,
		VILLAGE,
		CITY,
	};
	private Type type;
	private Vector3i position;// 2D index in the layer

	Building(Type type, Vector3i position) {
		this.type = type;
		this.position = position;
	}

	public Type get_type() {
		return type;
	}
	
	public Vector3i get_position() {
		return position;
	}
}
