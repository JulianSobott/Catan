package com.catangame.catan.math;

public class Vector3i {
	final public int x, y, z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector3i add(Vector3i a, Vector3i b) {
		return new Vector3i(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	public static Vector3i sub(Vector3i a, Vector3i b) {
		return new Vector3i(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	public static boolean are_equal(Vector3i a, Vector3i b) {
		return a.x == b.x && a.y == b.y && a.z == b.z;
	}
}
