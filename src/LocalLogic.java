import java.util.ArrayList;
import java.util.HashMap;
import org.jsfml.system.Vector2f;

// TODO name?
class LocalLogic {
	LocalState state;

	LocalLogic() {
		state = new LocalState();
	}

	void update_new_map(Field[][] fields) {
		state.field_resources = new HashMap<>();
		for (Resource res : Resource.values())
			state.field_resources.put(res, new ArrayList<>());
		state.field_numbers = new HashMap<>();
		for (byte i = 2; i < Map.number_count + 2; i++)
			state.field_numbers.put(i, new ArrayList<>());

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				float pos_x = x * Map.field_size + Map.field_offset + (y % 2 != 0 ? Map.field_size / 2.f : 0),
						pos_y = y * Map.field_size * 0.866f + Map.field_offset;
				state.field_resources.get(fields[x][y].resource).add(new Vector2f(pos_x, pos_y));
				state.field_numbers.get(fields[x][y].number).add(new Vector2f(pos_x, pos_y));
			}
		}
	}
}