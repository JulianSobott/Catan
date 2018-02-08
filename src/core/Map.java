package core;
import java.util.Random;

import local.Field;
import local.Resource;

public class Map {
	public final static int number_count = 11;
	public final static float field_size = 100;
	public final static float field_offset = field_size*0.5f;
	public final static float field_distance = -11;
	public static int map_size = 5;

	private Field[][] fields;

	public void create_map(int size, int seed) {
		Random rand = new Random(seed);
		map_size = size;

		this.fields = new Field[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				this.fields[x][y] = new Field(Resource.values()[rand.nextInt(Resource.values().length)],
						(byte) (rand.nextInt(number_count) + 2));
			}
		}
	}

	public Field[][] getFields() {
		return fields;
	}


}