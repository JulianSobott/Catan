package com.catangame.catan.local;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.math.Vector3i;

import com.catangame.catan.core.Building;
import com.catangame.catan.core.Building.Type;
import com.catangame.catan.core.Map;
import com.catangame.catan.data.Field;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.LocalState.Action;
import com.catangame.catan.local.LocalState.GameMode;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.superClasses.GameLogic;

public class LocalGameLogic extends GameLogic {
	// state, ui & connection
	LocalState state;
	private Core core;
	private LocalUI ui;

	// fonts
	BitmapFont std_font;

	// textures
	Texture village_txtr;
	Texture city_txtr;
	Texture street_txtr;

	public LocalGameLogic() {
		state = new LocalState();
	}

	public void setCore(Core core) {
		this.core = core;
	}

	public void setUI(LocalUI ui) {
		this.ui = ui;
	}

	void init(BitmapFont std_font) {
		this.std_font = std_font;
		village_txtr = new Texture(Gdx.files.local("assets/res/village.png"));
		city_txtr = new Texture(Gdx.files.local("assets/res/city.png"));
		street_txtr = new Texture(Gdx.files.local("assets/res/street.png"));
	}

	@Override
	public void set_mode(GameMode new_mode) {
		state.mode = new_mode;
	}

	@Override
	public void update_new_map(Field[][] fields) {
		state.field_resources = new HashMap<Resource, List<Vector2>>();
		for (Resource res : Resource.values())
			state.field_resources.put(res, new ArrayList<Vector2>());
		state.field_numbers = new HashMap<Byte, List<Vector2>>();
		for (byte i = 2; i < Map.NUMBER_COUNT + 2; i++)
			state.field_numbers.put(i, new ArrayList<Vector2>());

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				if (fields[x][y] != null) {
					Vector2 pos = Map.index_to_position(new Vector2i(x, y));
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
			//java.util.Map<Building.Type, List<Vector2>> users_buildings = new HashMap<>();
			List<Vector2> villages = new LinkedList<Vector2>();
			List<Vector2> cities = new LinkedList<Vector2>();
			List<AbstractStreet> streets = new LinkedList<AbstractStreet>();
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

	@Override
	public void add_building(int user, Building building) {
		if (building.get_type() == Building.Type.VILLAGE) {
			state.villages.get(user).add(Map.index_to_building_position(building.get_position()));
		} else if (building.get_type() == Building.Type.CITY) {
			// remove old village
			Vector2 building_pos = Map.index_to_building_position(building.get_position());
			for (int i = 0; i < state.villages.get(user).size(); i++) {
				Vector2 pos = state.villages.get(user).get(i);
				if (building_pos.x == pos.x && building_pos.y == pos.y) {
					state.villages.get(user).remove(i);
					break;
				}
			}

			state.cities.get(user).add(building_pos);
		} else if (building.get_type() == Building.Type.STREET) {
			state.streets.get(user).add(new AbstractStreet(Map.index_to_building_position(building.get_position()),
					Map.layer_to_street_rotation(building.get_position().z)));
		}
	}

	void render_map(ShapeRenderer sr, SpriteBatch sb) {
		if (state.mode == GameMode.game) {
			// render fields
			sr.begin(ShapeType.Filled);
			sr.setColor(new Color(0.6f, 0.6f, 0.6f, 1.f));
			sr.rect((Map.field_size + Map.border_size) / 2.f,
					0.26795f * Map.field_size * Map.MAGIC_HEX_NUMBER + Map.border_size,
					Map.field_size * (Map.map_size_y - 2.21f) - Map.border_size,
					Map.field_size * ((float) Map.map_size_y - 1.2f) * Map.MAGIC_HEX_NUMBER);
			sr.end();

			for (java.util.Map.Entry<Resource, List<Vector2>> resource : state.field_resources.entrySet()) {
				for (Vector2 pos : resource.getValue()) {
					sr.begin(ShapeType.Filled);
					sr.setColor(resource.getKey().get_color());
					sr.ellipse(pos.x - Map.field_size / 2, pos.y - Map.field_size / 2, Map.field_size, Map.field_size,
							30, 6);
					sr.end();

					/*sr.begin(ShapeType.Line);
					sr.setColor(new Color(0.6f, 0.6f, 0.6f, 1.f));
					Gdx.gl.glLineWidth(Map.border_size*5);// TODO apply zoom level
					sr.ellipse(pos.x - Map.field_size / 2, pos.y - Map.field_size / 2, Map.field_size,
							Map.field_size, 30, 6);
					sr.end();*/
				}
			}
			// render field numbers
			for (java.util.Map.Entry<Byte, List<Vector2>> number : state.field_numbers.entrySet()) {
				/*Text text = new Text("" + number.getKey(), std_font);
				text.setCharacterSize((40 - Math.abs(number.getKey() - (Map.NUMBER_COUNT + 4) / 2) * 4) * 4);
				text.setOrigin(text.getGlobalBounds().width * 0.5f, text.getGlobalBounds().height * 0.5f);
				text.setScale(0.25f, 0.25f);*/

				GlyphLayout layout = new GlyphLayout(std_font, "" + number.getKey());

				for (Vector2 pos : number.getValue()) {
					sb.begin();
					sb.setColor(Color.WHITE);
					std_font.draw(sb, "" + number.getKey(), pos.x - layout.width / 2, pos.y - layout.height / 2);
					sb.end();
				}
			}

			// render buildings
			Sprite village_sprite = new Sprite(village_txtr);
			village_sprite.flip(false, true);
			Sprite city_Sprite = new Sprite(city_txtr);
			city_Sprite.flip(false, true);
			Sprite street_Sprite = new Sprite(street_txtr);
			street_Sprite.flip(false, true);
			/*Sprite village_sprite = new Sprite(village_txtr);
			village_sprite.flip(false, true);
			village_sprite.setScale(0.1f, 0.1f);
			village_sprite.*/

			sb.begin();
			for (java.util.Map.Entry<Integer, List<AbstractStreet>> ub : state.streets.entrySet()) {
				sb.setColor(state.player_data.get(ub.getKey()).getColor());
				for (AbstractStreet street : ub.getValue()) {
					sb.draw(street_Sprite, street.position.x, street.position.y,
							street_Sprite.getWidth() * 0.5f * 0.08f, street_Sprite.getHeight() * 0.5f * 0.08f,
							street_Sprite.getWidth(), street_Sprite.getHeight(), 0.08f, 0.08f, street.rotation);
					sr.begin(ShapeType.Line);
					sr.rect(street.position.x, street.position.y, street_Sprite.getWidth() * 0.5f * 0.08f,
							street_Sprite.getHeight() * 0.5f * 0.08f, street_Sprite.getWidth(),
							street_Sprite.getHeight(), 0.08f, 0.08f, street.rotation, Color.WHITE, Color.WHITE,
							Color.WHITE, Color.WHITE);
					sr.end();
				}
			}
			for (java.util.Map.Entry<Integer, List<Vector2>> ub : state.villages.entrySet()) {
				for (Vector2 pos : ub.getValue()) {
					sb.draw(village_sprite, pos.x - village_sprite.getWidth() * 0.05f,
							pos.y - village_sprite.getHeight() * 0.06f, village_sprite.getWidth() * 0.1f,
							village_sprite.getHeight() * 0.1f);
				}
			}
			for (java.util.Map.Entry<Integer, List<Vector2>> ub : state.cities.entrySet()) {
				for (Vector2 pos : ub.getValue()) {
					sb.draw(city_Sprite, pos.x - city_Sprite.getWidth() * 0.05f,
							pos.y - city_Sprite.getHeight() * 0.05f, city_Sprite.getWidth() * 0.1f,
							city_Sprite.getHeight() * 0.1f);
				}
			}
			sb.end();
		}
	}

	void mouse_click_input(Vector2 position) {
		if (state.curr_action == Action.build_village) {
			Vector3i pos = Map.position_to_settlement_index(position);
			core.buildRequest(id, Type.VILLAGE, pos);
			ui.switch_to_idle();
		} else if (state.curr_action == Action.build_city) {
			Vector3i pos = Map.position_to_settlement_index(position);
			core.buildRequest(id, Type.CITY, pos);
			ui.switch_to_idle();
		} else if (state.curr_action == Action.build_street) {
			Vector3i pos = Map.position_to_street_index(position);
			core.buildRequest(id, Type.STREET, pos);
			ui.switch_to_idle();
		}
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}
}