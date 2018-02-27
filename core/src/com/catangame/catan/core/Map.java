package com.catangame.catan.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.math.Vector3i;

import com.catangame.catan.data.Field;
import com.catangame.catan.data.Resource;

public class Map {
	public enum GeneratorType {
		HEXAGON, CIRCLE,
	}

	// Settlement & street layer in z-coordinate
	public final static int LAYER_NORTH_STMT = 0;
	public final static int LAYER_SOUTH_STMT = 1;
	public final static int LAYER_NORTH_STREET = 2;
	public final static int LAYER_EAST_STREET = 3;
	public final static int LAYER_WEST_STREET = 4;

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
	private List<Vector3i> available_village_places = new LinkedList<Vector3i>();
	private List<Vector3i> available_street_places = new LinkedList<Vector3i>();
	private List<Vector3i> built_villages = new LinkedList<Vector3i>();

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
			if (i % (NUMBER_COUNT) + 2 == 7)
				available_numbers.push((byte) (rand.nextInt(NUMBER_COUNT) + 2));
			else
				available_numbers.push((byte) (i % (NUMBER_COUNT) + 2));
		}

		Vector2 island_center = Map.index_to_position(new Vector2i(map_size_x / 2, map_size_y / 2));

		this.fields = new Field[map_size_x][map_size_y];
		for (int x = 0; x < map_size_x; x++) {
			for (int y = 0; y < map_size_y; y++) {
				Vector2 pos = Map.index_to_position(new Vector2i(x, y));
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
						available_numbers.push((byte) (rand.nextInt(NUMBER_COUNT) + 2));
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

	public boolean is_inside_hexagon(Vector2 origin, float diameter, Vector2 position) {
		Vector2 delta = new Vector2(Math.abs(position.x - origin.x) / diameter,
				Math.abs(position.y - origin.y) / diameter);
		return (delta.y <= 0.433f) && (0.433f * delta.x + 0.25f * delta.y <= 0.5f * 0.433f);
	}

	List<Vector2i> get_surrounding_fields(Vector3i settlement_pos) {
		List<Vector2i> ret = new ArrayList<Vector2i>();
		if (fields[settlement_pos.x][settlement_pos.y].resource != Resource.OCEAN)
			ret.add(new Vector2i(settlement_pos.x, settlement_pos.y));
		if (settlement_pos.z == LAYER_NORTH_STMT ? settlement_pos.y > 0 : settlement_pos.y < map_size_y - 1) {
			int left_x = settlement_pos.y % 2 == 0 ? settlement_pos.x - 1 : settlement_pos.x;
			int left_y = settlement_pos.z == LAYER_NORTH_STMT ? settlement_pos.y - 1 : settlement_pos.y + 1;
			if (settlement_pos.y > 0 && (settlement_pos.y % 2 != 0 || settlement_pos.x > 0)
					&& fields[left_x][left_y].resource != Resource.OCEAN) {
				ret.add(new Vector2i(left_x, left_y));
			}
			if (settlement_pos.y > 0 && (settlement_pos.y % 2 == 0 || settlement_pos.x < map_size_x - 1)
					&& fields[left_x + 1][left_y].resource != Resource.OCEAN) {
				ret.add(new Vector2i(left_x + 1, left_y));
			}
		}
		return ret;
	}

	public List<Field> get_surrounding_field_objects(Building building) {
		List<Vector2i> field_positions = get_surrounding_fields(building.get_position());
		List<Field> surrounding_fields = new ArrayList<Field>();
		for (Vector2i f : field_positions) {
			surrounding_fields.add(fields[f.x][f.y]);
		}
		return surrounding_fields;
	}

	// returns a list of all possible building sites nearby a building
	List<Vector3i> get_nearby_building_sites(Vector3i position) {
		List<Vector3i> ret = new ArrayList<Vector3i>();
		int left_x = position.y % 2 == 0 ? position.x - 1 : position.x;

		if (position.z == LAYER_NORTH_STMT) {
			ret.add(new Vector3i(position.x, position.y, LAYER_NORTH_STREET));
			ret.add(new Vector3i(position.x, position.y, LAYER_EAST_STREET));
			ret.add(new Vector3i(left_x + 1, position.y - 1, LAYER_WEST_STREET));
			ret.add(new Vector3i(left_x, position.y - 1, LAYER_SOUTH_STMT));
			ret.add(new Vector3i(left_x + 1, position.y - 1, LAYER_SOUTH_STMT));
			ret.add(new Vector3i(position.x, position.y - 2, LAYER_SOUTH_STMT));
		} else if (position.z == LAYER_SOUTH_STMT) {
			ret.add(new Vector3i(left_x, position.y + 1, LAYER_EAST_STREET));
			ret.add(new Vector3i(left_x + 1, position.y + 1, LAYER_NORTH_STREET));
			ret.add(new Vector3i(left_x + 1, position.y + 1, LAYER_WEST_STREET));
			ret.add(new Vector3i(left_x, position.y + 1, LAYER_NORTH_STMT));
			ret.add(new Vector3i(left_x + 1, position.y + 1, LAYER_NORTH_STMT));
			ret.add(new Vector3i(position.x, position.y + 2, LAYER_NORTH_STMT));
		} else if (position.z == LAYER_NORTH_STREET) {
			ret.add(new Vector3i(position.x, position.y, LAYER_NORTH_STMT));
			ret.add(new Vector3i(position.x, position.y, LAYER_WEST_STREET));
			ret.add(new Vector3i(position.x, position.y, LAYER_EAST_STREET));
			ret.add(new Vector3i(position.x - 1, position.y, LAYER_EAST_STREET));
			ret.add(new Vector3i(left_x + 1, position.y - 1, LAYER_WEST_STREET));
			ret.add(new Vector3i(left_x, position.y - 1, LAYER_SOUTH_STMT));
		} else if (position.z == LAYER_EAST_STREET) {
			ret.add(new Vector3i(position.x, position.y, LAYER_NORTH_STMT));
			ret.add(new Vector3i(position.x, position.y, LAYER_NORTH_STREET));
			ret.add(new Vector3i(position.x + 1, position.y, LAYER_WEST_STREET));
			ret.add(new Vector3i(left_x + 1, position.y - 1, LAYER_WEST_STREET));
			ret.add(new Vector3i(left_x + 1, position.y - 1, LAYER_SOUTH_STMT));
			ret.add(new Vector3i(position.x + 1, position.y, LAYER_NORTH_STREET));
		} else if (position.z == LAYER_WEST_STREET) {
			ret.add(new Vector3i(position.x, position.y, LAYER_NORTH_STREET));
			ret.add(new Vector3i(left_x, position.y + 1, LAYER_EAST_STREET));
			ret.add(new Vector3i(left_x, position.y + 1, LAYER_NORTH_STREET));
			ret.add(new Vector3i(left_x, position.y + 1, LAYER_NORTH_STMT));
			ret.add(new Vector3i(position.x - 1, position.y, LAYER_EAST_STREET));
			ret.add(new Vector3i(left_x, position.y - 1, LAYER_SOUTH_STMT));
		}
		return ret;
	}

	// creates internal list of available houses after the map was created
	void calculate_available_places() {
		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				// north
				if (!get_surrounding_fields(new Vector3i(x, y, 0)).isEmpty()) {
					available_village_places.add(new Vector3i(x, y, LAYER_NORTH_STMT));
				}
				// south
				if (!get_surrounding_fields(new Vector3i(x, y, 1)).isEmpty()) {
					available_village_places.add(new Vector3i(x, y, LAYER_SOUTH_STMT));
				}
			}
		}

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				// north
				if (fields[x][y].resource != Resource.OCEAN || y > 0 && (y % 2 == 1 || x > 0)
						&& fields[y % 2 == 0 ? x - 1 : x][y - 1].resource != Resource.OCEAN) {
					available_street_places.add(new Vector3i(x, y, LAYER_NORTH_STREET));
				}
				// east
				if (fields[x][y].resource != Resource.OCEAN || y > 0 && (y % 2 == 0 || x < fields.length - 1)
						&& fields[y % 2 == 1 ? x + 1 : x][y - 1].resource != Resource.OCEAN) {
					available_street_places.add(new Vector3i(x, y, LAYER_EAST_STREET));
				}
				// west
				if (fields[x][y].resource != Resource.OCEAN || (x > 0) && fields[x - 1][y].resource != Resource.OCEAN) {
					available_street_places.add(new Vector3i(x, y, LAYER_WEST_STREET));
				}
			}
		}
	}

	List<Vector3i> add_random_cities(int seed, int house_count) {
		Random rand = new Random(seed);

		List<Vector3i> new_cities = new ArrayList<Vector3i>();
		for (int i = 0; i < house_count; i++) {
			int index = rand.nextInt(available_village_places.size());
			new_cities.add(available_village_places.get(index));
			built_villages.add(available_village_places.get(index));
			available_village_places.remove(index);
		}
		return new_cities;
	}

	public boolean is_village_place_available(Vector3i pos) {
		for (Vector3i ap : available_village_places) {
			if (ap.x == pos.x && ap.y == pos.y && ap.z == pos.z) {
				return true;
			}
		}
		return false;
	}

	public boolean is_city_place_available(Vector3i pos) {
		for (Vector3i ap : built_villages) {
			if (ap.x == pos.x && ap.y == pos.y && ap.z == pos.z) {
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

	public void build_village(Vector3i pos) {
		for (Vector3i ap : available_village_places) {
			if (ap.x == pos.x && ap.y == pos.y) {
				available_village_places.remove(ap);
				built_villages.add(pos);
				return;
			}
		}
	}

	public void build_city(Vector3i pos) {
		build_village(pos);// TODO
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

	// field index to position
	public static Vector2 index_to_position(Vector2i index) {
		return index_to_position(index.x, index.y);
	}

	// field index to position
	public static Vector2 index_to_position(float x, float y) {//TODO check
		float pos_x = x * (field_size + field_distance) + field_offset
				+ (y % 2 != 0 ? (field_size + field_distance) / 2.f : 0),
				pos_y = y * (field_size + field_distance) * Map.MAGIC_HEX_NUMBER + field_offset;
		return new Vector2(pos_x, pos_y);
	}

	// any position in a field to the fields index
	public static Vector2i position_to_index(Vector2 position) {
		int y = Math.round((position.y - field_offset) / (Map.MAGIC_HEX_NUMBER * (field_size + field_distance)));
		int x = Math.round((position.x - (field_offset + (y % 2 != 0 ? (field_size + field_distance) / 2.f : 0)))
				/ (field_size + field_distance));
		return new Vector2i(x, y);
	}

	public static float layer_to_street_rotation(int layer) {
		return layer == LAYER_NORTH_STREET ? -30
				: layer == LAYER_EAST_STREET ? 30 : layer == LAYER_WEST_STREET ? 90 : 0;
	}

	// the z-component contains the layer
	public static Vector2 index_to_building_position(Vector3i index) {
		if (index.z == LAYER_NORTH_STMT)
			return Map.index_to_position(index.x, index.y).add(new Vector2(0, -Map.field_size / 2.f));
		else if (index.z == LAYER_SOUTH_STMT)
			return Map.index_to_position(index.x, index.y).add( new Vector2(0, Map.field_size / 2.f));
		else if (index.z == LAYER_NORTH_STREET)
			return Map.index_to_position(index.x, index.y).add(
					new Vector2(-diagonal_offset_x, -diagonal_offset_y));
		else if (index.z == LAYER_EAST_STREET)
			return Map.index_to_position(index.x, index.y).add(
					new Vector2(diagonal_offset_x, -diagonal_offset_y));
		else if (index.z == LAYER_WEST_STREET)
			return Map.index_to_position(index.x, index.y).add( new Vector2(-linear_offset_x, 0));
		else {
			System.err.println("ERROR: undefined building layer '" + index.z + "' in index_to_building_position()");
			return Map.index_to_position(index.x, index.y);
		}
	}

	// the z-component contains the layer
	public static Vector3i position_to_street_index(Vector2 position) {
		// calculate index in all layers
		Vector2i north_index = position_to_index(
				new Vector2(position.x + diagonal_offset_x, position.y + diagonal_offset_y));
		Vector2i east_index = position_to_index(
				new Vector2(position.x - diagonal_offset_x, position.y + diagonal_offset_y));
		Vector2i west_index = position_to_index(new Vector2(position.x + linear_offset_x, position.y));

		// calculate normalized position
		Vector2 north_position = Map.index_to_position(north_index).add(
				new Vector2(-diagonal_offset_x, -diagonal_offset_y));
		Vector2 east_position = Map.index_to_position(east_index).add(
				new Vector2(diagonal_offset_x, -diagonal_offset_y));
		Vector2 west_position = Map.index_to_position(west_index).add( new Vector2(-linear_offset_x, 0));

		// determine distance from real to normalized position
		float north_length = north_position.sub(position).len();
		float east_length = east_position.sub(position).len();
		float west_length = west_position.sub(position).len();

		// return the nearest index
		if (north_length < east_length) {
			if (north_length < west_length)
				return new Vector3i(north_index.x, north_index.y, LAYER_NORTH_STREET);
			else
				return new Vector3i(west_index.x, west_index.y, LAYER_WEST_STREET);
		} else {
			if (west_length < east_length)
				return new Vector3i(west_index.x, west_index.y, LAYER_WEST_STREET);
			else
				return new Vector3i(east_index.x, east_index.y, LAYER_EAST_STREET);
		}
	}

	public static Vector3i position_to_settlement_index(Vector2 position) {
		// calculate index in all layers
		Vector2i north_index = position_to_index(new Vector2(position.x, position.y + Map.field_size / 2.f));
		Vector2i south_index = position_to_index(new Vector2(position.x, position.y - Map.field_size / 2.f));

		// calculate normalized position
		Vector2 north_position = Map.index_to_position(north_index).add(
				new Vector2(0, -Map.field_size / 2.f));
		Vector2 south_position = Map.index_to_position(south_index).add(
				new Vector2(0, Map.field_size / 2.f));

		// determine distance from real to normalized position
		float north_length = north_position.sub(position).len();
		float south_length = south_position.sub(position).len();

		// return the nearest index
		if (north_length < south_length)
			return new Vector3i(north_index.x, north_index.y, LAYER_NORTH_STMT);
		else
			return new Vector3i(south_index.x, south_index.y, LAYER_SOUTH_STMT);
	}

	public void set_fields(Field[][] fields) {
		this.fields = fields;
	}

}