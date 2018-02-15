package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

import data.Field;
import data.Resource;
import math.Vector2fMath;

public class Map {
	enum GeneratorType {
		HEXAGON, CIRCLE,
	}

	public final static float MAGIC_HEX_NUMBER = 0.866f;// height of a isosceles triangle with side length 1
	public final static int NUMBER_COUNT = 11;
	public static float border_size = 2.5f;
	public static float field_size = 100;
	public static float field_offset;
	public static float field_distance;
	public static float linear_offset_x;
	public static float diagonal_offset_x;
	public static float diagonal_offset_y;
	public static int map_size_x = 7;// should be 5+2*n
	public static int map_size_y;

	private Field[][] fields;
	private List<Vector2i> available_city_places = new LinkedList<Vector2i>();
	private List<Vector3i> available_street_places = new LinkedList<Vector3i>();

	public static void update_constants() {
		field_offset = field_size * 0.5f;
		field_distance = -((1 - MAGIC_HEX_NUMBER) * field_size - border_size);
		linear_offset_x = (Map.field_size + Map.field_distance) / 2.f;
		diagonal_offset_x = linear_offset_x * 0.5f;
		diagonal_offset_y = linear_offset_x * 0.866f;
		map_size_y = (int) (map_size_x / MAGIC_HEX_NUMBER);
	}

	// \p resource_ratio: percentage of a specific resource on the map. The order must be equal to the order in the Resource-class
	public void create_map(int size, int seed, int island_size, float[] resource_ratio, GeneratorType type) {
		Random rand = new Random(seed);
		/*if (size % 2 == 0)
			size++;*/
		map_size_x = size;
		map_size_y = (int) ((float) map_size_x / MAGIC_HEX_NUMBER);

		// pre-calculations
		float ratio_sum = 0.f;
		for (float r : resource_ratio)
			ratio_sum += r;
		int field_count = island_size;
		if (type == GeneratorType.HEXAGON) {
			for (int i = 1; i <= island_size / 2; i++)
				field_count += 2 * (island_size - i);
			//field_count = (int) Math.round(Math.pow((float) island_size / 2.f, 2.f) * 2.598f);
		} else if (type == GeneratorType.CIRCLE) {
			field_count = (int) (Math.PI * Math.pow((float) island_size / 2.f, 2.f) / MAGIC_HEX_NUMBER);
		}
		System.out.println("field_count: " + field_count);

		LinkedList<Resource> available_resources = new LinkedList<Resource>();
		for (Resource r : Resource.values()) {
			if (r != Resource.OCEAN) {
				int count = (int) ((float) field_count * resource_ratio[r.ordinal()] / ratio_sum) + 1;
				for (int i = 0; i < count; i++)
					available_resources.push(r);
			}
		}
		for (int i = available_resources.size(); i < field_count; i++)
			available_resources.push(Resource.values()[rand.nextInt(Resource.values().length - 1) + 1]);

		LinkedList<Byte> available_numbers = new LinkedList<Byte>();
		for (int i = 0; i < field_count; i++) {
			available_numbers.push((byte) (i % (NUMBER_COUNT - 1) + 2));
		}

		Vector2f island_center = Map.index_to_position(new Vector2i(map_size_x / 2, map_size_y / 2));

		this.fields = new Field[map_size_x][map_size_y];
		for (int x = 0; x < map_size_x; x++) {
			for (int y = 0; y < map_size_y; y++) {
				Vector2f pos = Map.index_to_position(new Vector2i(x, y));
				boolean is_land = false;
				if (type == GeneratorType.HEXAGON)
					is_land = is_inside_hexagon(island_center, island_size * (field_size + field_distance), pos);
				else if (type == GeneratorType.CIRCLE)
					is_land = (int) Math.sqrt(
							Math.pow(pos.x - island_center.x, 2) + Math.pow(pos.y - island_center.y, 2)) <= island_size
									* (field_size + field_distance) / 2;
				if (is_land) {
					if (available_resources.isEmpty()) {//HACK TODO
						available_resources.push(Resource.values()[rand.nextInt(Resource.values().length - 1) + 1]);
						System.out.println("HACK");
					}
					if (available_numbers.isEmpty()) {//HACK
						available_numbers.push((byte) (rand.nextInt(NUMBER_COUNT - 1) + 2));
						System.out.println("HACK");
					}
					int index_r = rand.nextInt(available_resources.size());
					int index_n = rand.nextInt(available_numbers.size());
					this.fields[x][y] = new Field(available_resources.get(index_r), available_numbers.get(index_n));
					available_resources.remove(index_r);
					available_numbers.remove(index_n);
				} else // is ocean
					this.fields[x][y] = new Field(Resource.OCEAN, (byte) 0);
			}
		}
		System.out.println("Available resources: " + available_resources.size());
		System.out.println("Available numbers: " + available_numbers.size());
	}

	public boolean is_inside_hexagon(Vector2f origin, float diameter, Vector2f position) {
		Vector2f delta = new Vector2f(Math.abs(position.x - origin.x) / diameter,
				Math.abs(position.y - origin.y) / diameter);
		return (delta.y <= 0.433f) && (0.433f * delta.x + 0.25f * delta.y <= 0.5f * 0.433f);
	}

	List<Vector2i> get_surrounding_fields(Vector2i city_pos) {
		List<Vector2i> ret = new ArrayList<Vector2i>();
		if (fields[city_pos.x][city_pos.y].resource != Resource.OCEAN)
			ret.add(new Vector2i(city_pos.x, city_pos.y));
		int upper_left_x = city_pos.y % 2 == 0 ? city_pos.x - 1 : city_pos.x;
		if (city_pos.y > 0 && (city_pos.y % 2 != 0 || city_pos.x > 0)
				&& fields[upper_left_x][city_pos.y - 1].resource != Resource.OCEAN) {
			ret.add(new Vector2i(upper_left_x, city_pos.y - 1));
		}
		if (city_pos.y > 0 && (city_pos.y % 2 == 0 || city_pos.x < map_size_x - 1)
				&& fields[upper_left_x + 1][city_pos.y - 1].resource != Resource.OCEAN) {
			ret.add(new Vector2i(upper_left_x + 1, city_pos.y - 1));
		}
		return ret;
	}

	public List<Field> get_surrounding_fields_objects(Building building) {
		Vector2i city_pos = new Vector2i(building.get_position().x, building.get_position().y);
		List<Vector2i> field_positions = get_surrounding_fields(city_pos);
		List<Field> surrounding_fields = new ArrayList<Field>();
		for (Vector2i f : field_positions) {
			surrounding_fields.add(fields[f.x][f.y]);
		}
		return surrounding_fields;
	}

	// creates internal list of available houses after the map was created
	void calculate_available_places() {
		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				if (!get_surrounding_fields(new Vector2i(x, y)).isEmpty()) {
					available_city_places.add(new Vector2i(x, y));
				}
			}
		}

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				// north
				if (fields[x][y].resource != Resource.OCEAN || y > 0 && (y % 2 == 1 || x > 0)
						&& fields[y % 2 == 0 ? x - 1 : x][y - 1].resource != Resource.OCEAN) {
					available_street_places.add(new Vector3i(x, y, 1));
				}
				// east
				if (fields[x][y].resource != Resource.OCEAN || y > 0 && (y % 2 == 0 || x < fields.length - 1)
						&& fields[y % 2 == 1 ? x + 1 : x][y - 1].resource != Resource.OCEAN) {
					available_street_places.add(new Vector3i(x, y, 2));
				}
				// west
				if (fields[x][y].resource != Resource.OCEAN || (x > 0) && fields[x - 1][y].resource != Resource.OCEAN) {
					available_street_places.add(new Vector3i(x, y, 3));
				}
			}
		}
	}

	List<Vector2i> add_random_cities(int seed, int house_count) {
		Random rand = new Random(seed);

		List<Vector2i> new_cities = new ArrayList<Vector2i>();
		for (int i = 0; i < house_count; i++) {
			int index = rand.nextInt(available_city_places.size());
			new_cities.add(available_city_places.get(index));
			available_city_places.remove(index);
		}
		return new_cities;
	}

	public boolean is_city_place_available(Vector2i pos) {
		for (Vector2i ap : available_city_places) {
			if (ap.x == pos.x && ap.y == pos.y) {
				return true;
			}
		}
		return false;
	}

	public boolean is_street_place_available(Vector3i pos) {
		for (Vector3i ap : available_street_places) {
			if (ap.x == pos.x && ap.y == pos.y && ap.z == pos.z) {
				return true;
			}
		}
		return false;
	}

	public void build_city(Vector2i pos) {
		for (Vector2i ap : available_city_places) {
			if (ap.x == pos.x && ap.y == pos.y) {
				available_city_places.remove(ap);
				return;
			}
		}
	}

	public void build_street(Vector3i pos) {
		for (int i = 0; i < available_street_places.size(); i++) {
			Vector3i ap = available_street_places.get(i);
			if (ap.x == pos.x && ap.y == pos.y && ap.z == pos.z) {
				available_street_places.remove(i);
				return;
			}
		}
	}

	public Field[][] getFields() {
		return fields;
	}

	// position <-> building index mapping

	public static Vector2f index_to_position(Vector2i index) {
		return index_to_position(index.x, index.y);
	}

	public static Vector2f index_to_position(float x, float y) {
		float pos_x = x * (field_size + field_distance) + field_offset
				+ (y % 2 != 0 ? (field_size + field_distance) / 2.f : 0),
				pos_y = y * (field_size + field_distance) * Map.MAGIC_HEX_NUMBER + field_offset;
		return new Vector2f(pos_x, pos_y);
	}

	public static Vector2i position_to_index(Vector2f position) {
		int y = Math.round((position.y - field_offset) / (Map.MAGIC_HEX_NUMBER * (field_size + field_distance)));
		int x = Math.round((position.x - (field_offset + (y % 2 != 0 ? (field_size + field_distance) / 2.f : 0)))
				/ (field_size + field_distance));
		return new Vector2i(x, y);
	}

	public static float layer_to_street_rotation(int layer) {
		return layer == 1 ? -30 : layer == 2 ? 30 : layer == 3 ? 90 : 0;
	}

	public static Vector2f index_to_city_position(Vector2i index) {
		return index_to_building_position(new Vector3i(index.x, index.y, 0));
	}

	// the z-component contains the layer
	public static Vector2f index_to_building_position(Vector3i index) {
		if (index.z == 1)
			return Vector2f.add(Map.index_to_position(index.x, index.y),
					new Vector2f(-diagonal_offset_x, -diagonal_offset_y));
		else if (index.z == 2)
			return Vector2f.add(Map.index_to_position(index.x, index.y),
					new Vector2f(diagonal_offset_x, -diagonal_offset_y));
		else if (index.z == 3)
			return Vector2f.add(Map.index_to_position(index.x, index.y), new Vector2f(-linear_offset_x, 0));
		else// city/village
			return Vector2f.add(Map.index_to_position(index.x, index.y), new Vector2f(0, -Map.field_size / 2.f));
	}

	// the z-component contains the layer
	public static Vector3i position_to_street_index(Vector2f position) {
		// calculate index in all layers
		Vector2i north_index = position_to_index(
				new Vector2f(position.x + diagonal_offset_x, position.y + diagonal_offset_y));
		Vector2i east_index = position_to_index(
				new Vector2f(position.x - diagonal_offset_x, position.y + diagonal_offset_y));
		Vector2i west_index = position_to_index(new Vector2f(position.x + linear_offset_x, position.y));

		// calculate normalized position
		Vector2f north_position = Vector2f.add(Map.index_to_position(north_index),
				new Vector2f(-diagonal_offset_x, -diagonal_offset_y));
		Vector2f east_position = Vector2f.add(Map.index_to_position(east_index),
				new Vector2f(diagonal_offset_x, -diagonal_offset_y));
		Vector2f west_position = Vector2f.add(Map.index_to_position(west_index), new Vector2f(-linear_offset_x, 0));

		// determine distance from real to normalized position
		float north_length = Vector2fMath.length(Vector2f.sub(north_position, position));
		float east_length = Vector2fMath.length(Vector2f.sub(east_position, position));
		float west_length = Vector2fMath.length(Vector2f.sub(west_position, position));

		// return the nearest index
		if (north_length < east_length) {
			if (north_length < west_length)
				return new Vector3i(north_index.x, north_index.y, 1);
			else
				return new Vector3i(west_index.x, west_index.y, 3);
		} else {
			if (west_length < east_length)
				return new Vector3i(west_index.x, west_index.y, 3);
			else
				return new Vector3i(east_index.x, east_index.y, 2);
		}
	}

	public static Vector2i position_to_city_index(Vector2f position) {
		return Map.position_to_index(new Vector2f(position.x, position.y + Map.field_size / 2.f));
	}

}