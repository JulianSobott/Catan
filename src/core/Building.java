package core;

import java.io.Serializable;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

public class Building implements Serializable {
	public enum Type {
		STREET, VILLAGE, CITY,
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

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Building))
			return false;
		Building otherMyClass = (Building) other;

		if (otherMyClass.type == type && otherMyClass.position.x == position.x && otherMyClass.position.y == position.y
				&& otherMyClass.position.z == position.z)
			return true;
		else
			return false;
	}
}
