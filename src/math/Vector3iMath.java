package math;

import org.jsfml.system.Vector3i;

public class Vector3iMath {
	public static boolean are_equal(Vector3i a, Vector3i b) {
		return a.x == b.x && a.y == b.y && a.z == b.z;
	}
}