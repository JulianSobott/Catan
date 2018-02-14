package local;

import org.jsfml.system.Vector2f;

// just for rendering
public class AbstractStreet {
	Vector2f position;
	float rotation;

	public AbstractStreet(Vector2f position, float rotation) {
		this.position = position;
		this.rotation = rotation;
	}
}
