package com.catangame.catan.data;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.math.Vector2i;

public class Harbour {
	enum Type{
		three4One, two4One;
	}

	public Vector2 position;
	public Type type;
	public Resource resource;
	
}
