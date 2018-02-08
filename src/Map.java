import java.util.Random;

class Map {
	public final static int number_count = 11;
	public final static float field_size = 100;
	public final static float field_offset = field_size*0.5f;

	Field[][] fields;

	void create_map(int size, int seed) {
		Random rand = new Random(seed);

		fields = new Field[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				fields[x][y] = new Field(Resource.values()[rand.nextInt(Resource.values().length)],
						(byte) (rand.nextInt(number_count) + 2));
			}
		}
	}

}