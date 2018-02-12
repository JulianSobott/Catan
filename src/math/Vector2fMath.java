package math;

import org.jsfml.system.Vector2f;

public class Vector2fMath {
	public static float length(Vector2f vec) {
		return (float) Math.sqrt(vec.x * vec.x + vec.y * vec.y);
	}

	public static float lengthSqr(Vector2f vec) {
		return vec.x * vec.x + vec.y * vec.y;
	}

	public static Vector2f norm(Vector2f vec) {
		return Vector2f.div(vec, length(vec));
	}

	// rotate with radiants
	public static Vector2f rotate(Vector2f vec, float rad) {
		float length = length(vec);
		float rotation = (float) Math.atan2(vec.x, vec.y) - rad;
		return new Vector2f((float) Math.sin(rotation) * length, (float) Math.cos(rotation) * length);
	}
}