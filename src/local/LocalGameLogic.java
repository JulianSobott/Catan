package local;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

import core.Building;
import core.Map;
import core.Player;
import core.Building.Type;
import data.Field;
import data.Resource;
import local.LocalState.Action;
import local.LocalState.GameMode;
import network.Command;
import network.Networkmanager;
import superClasses.Core;
import superClasses.GameLogic;

// TODO name?
public class LocalGameLogic extends GameLogic{
	// state, ui & connection
	LocalState state;
	private Core core;

	// fonts
	Font std_font;

	// textures
	Texture village_txtr = new Texture();
	Texture city_txtr = new Texture();
	Texture street_txtr = new Texture();
	
	public LocalGameLogic() {
		state = new LocalState();
	}
	
	public void setCore(Core core) {
		this.core = core;
	}

	void init(Font std_font) {
		this.std_font = std_font;
		try {
			village_txtr.loadFromFile(Paths.get("res/village.png"));
			village_txtr.setSmooth(true);
			city_txtr.loadFromFile(Paths.get("res/city.png"));
			city_txtr.setSmooth(true);
			street_txtr.loadFromFile(Paths.get("res/street.png"));
			street_txtr.setSmooth(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void set_mode(GameMode new_mode) {
		state.mode = new_mode;
	}
	
	@Override
	public void update_new_map(Field[][] fields) {
		state.field_resources = new HashMap<>();
		for (Resource res : Resource.values())
			state.field_resources.put(res, new ArrayList<Vector2f>());
		state.field_numbers = new HashMap<>();
		for (byte i = 2; i < Map.NUMBER_COUNT + 2; i++)
			state.field_numbers.put(i, new ArrayList<Vector2f>());

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				if (fields[x][y] != null) {
					Vector2f pos = Map.index_to_position(new Vector2i(x, y));
					state.field_resources.get(fields[x][y].resource).add(pos);
					if (fields[x][y].number != 0)
						state.field_numbers.get(fields[x][y].number).add(pos);
				}
			}
		}
	}

	@Override
	public void update_buildings(java.util.Map<Integer, List<Building>> buildings) {
		for (java.util.Map.Entry<Integer, List<Building>> ub : buildings.entrySet()) {
			//java.util.Map<Building.Type, List<Vector2f>> users_buildings = new HashMap<>();
			List<Vector2f> villages = new LinkedList<>();
			List<Vector2f> cities = new LinkedList<>();
			List<AbstractStreet> streets = new LinkedList<>();
			for (Building b : ub.getValue()) {
				if (b.get_type() == Building.Type.VILLAGE)
					villages.add(Map.index_to_building_position(b.get_position()));
				else if (b.get_type() == Building.Type.CITY)
					cities.add(Map.index_to_building_position(b.get_position()));
				else if (b.get_type() == Building.Type.STREET)
					streets.add(new AbstractStreet(Map.index_to_building_position(b.get_position()),
							Map.layer_to_street_rotation(b.get_position().z)));
			}
			state.villages.put(ub.getKey(), villages);
			state.cities.put(ub.getKey(), cities);
			state.streets.put(ub.getKey(), streets);
		}
	}

	void render_map(RenderTarget target) {
		if (state.mode == GameMode.game) {
			// render fields
			for (java.util.Map.Entry<Resource, List<Vector2f>> resource : state.field_resources.entrySet()) {
				CircleShape shape = new CircleShape(Map.field_size * 0.5f, 6);
				shape.setFillColor(resource.getKey().get_color());
				shape.setOrigin(Map.field_size * 0.5f, Map.field_size * 0.5f);
				shape.setOutlineColor(new Color(150, 150, 150));
				shape.setOutlineThickness(Map.border_size);

				for (Vector2f pos : resource.getValue()) {
					shape.setPosition(pos);
					target.draw(shape);
				}
			}
			// render field numbers
			for (java.util.Map.Entry<Byte, List<Vector2f>> number : state.field_numbers.entrySet()) {
				Text text = new Text("" + number.getKey(), std_font);
				text.setCharacterSize((40 - Math.abs(number.getKey() - (Map.NUMBER_COUNT + 4) / 2) * 4) * 4);
				text.setOrigin(text.getGlobalBounds().width * 0.5f, text.getGlobalBounds().height * 0.5f);
				text.setScale(0.25f, 0.25f);

				for (Vector2f pos : number.getValue()) {
					text.setPosition(pos);
					target.draw(text);
				}
			}

			// render buildings
			Sprite village_sprite = new Sprite(village_txtr);
			village_sprite.setOrigin(village_sprite.getLocalBounds().width * 0.5f,
					village_sprite.getLocalBounds().height * 0.8f);
			village_sprite.setScale(0.1f, 0.1f);
			Sprite city_Sprite = new Sprite(city_txtr);
			city_Sprite.setOrigin(city_Sprite.getLocalBounds().width * 0.5f,
					city_Sprite.getLocalBounds().height * 0.8f);
			city_Sprite.setScale(0.1f, 0.1f);
			Sprite street_Sprite = new Sprite(street_txtr);
			street_Sprite.setOrigin(street_Sprite.getLocalBounds().width * 0.5f,
					street_Sprite.getLocalBounds().height * 0.5f);
			street_Sprite.setScale(0.1f, 0.1f);

			for (java.util.Map.Entry<Integer, List<Vector2f>> ub : state.villages.entrySet()) {
				village_sprite.setColor(state.player_data.get(ub.getKey()).getColor());
				for (Vector2f pos : ub.getValue()) {
					village_sprite.setPosition(pos);
					target.draw(village_sprite);
				}
			}
			for (java.util.Map.Entry<Integer, List<Vector2f>> ub : state.cities.entrySet()) {
				city_Sprite.setColor(state.player_data.get(ub.getKey()).getColor());
				for (Vector2f pos : ub.getValue()) {
					city_Sprite.setPosition(pos);
					target.draw(city_Sprite);
				}
			}
			for (java.util.Map.Entry<Integer, List<AbstractStreet>> ub : state.streets.entrySet()) {
				village_sprite.setColor(state.player_data.get(ub.getKey()).getColor());
				for (AbstractStreet street : ub.getValue()) {
					street_Sprite.setPosition(street.position);
					street_Sprite.setRotation(street.rotation);
					target.draw(street_Sprite);
				}
			}
		}
	}

	void mouse_click_input(Vector2f position) {
		if (state.curr_action == Action.build_village) {
			Vector2i pos = Map.position_to_city_index(position);
			//state.villages.get(id).add(Map.index_to_building_position(new Vector3i(pos.x, pos.y, 0)));
			core.buildRequest(id, Type.VILLAGE, pos);
			state.curr_action = Action.idle;
		}
	}

	public void build(int idPlayer, Command buildType, Vector2i position) {
		// TODO Auto-generated method stub
	}


}