package com.catangame.catan.math;

public class Vector2i {
	final public int x, y;

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Vector2i add(Vector2i a, Vector2i b) {
		return new Vector2i(a.x + b.x, a.y + b.y);
	}

	public static Vector2i sub(Vector2i a, Vector2i b) {
		return new Vector2i(a.x - b.x, a.y - b.y);
	}

	public static boolean are_equal(Vector2i a, Vector2i b) {
		return a.x == b.x && a.y == b.y;
	}

}
