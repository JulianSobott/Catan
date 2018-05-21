package com.catangame.catan.local;

import com.badlogic.gdx.math.Vector2;

// just for rendering
public class AbstractStreet {
	Vector2 position;
	float rotation;

	public AbstractStreet(Vector2 position, float rotation) {
		this.position = position;
		this.rotation = rotation;
	}
}
