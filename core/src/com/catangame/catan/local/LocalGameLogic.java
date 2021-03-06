package com.catangame.catan.local;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.utils.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.catangame.catan.math.Vector2i;
import com.catangame.catan.math.Vector3i;

import com.catangame.catan.core.Building;
import com.catangame.catan.core.Building.Type;
import com.catangame.catan.core.Map;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.DevCardType;
import com.catangame.catan.data.Field;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.LocalState.Action;
import com.catangame.catan.local.LocalState.GameMode;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.superClasses.GameLogic;
import com.catangame.catan.utils.Clock;
import com.catangame.catan.utils.FontMgr;
import com.catangame.catan.utils.TextureMgr;

public class LocalGameLogic extends GameLogic {
	// state, ui & connection
	LocalState state;
	private Core core;
	private LocalUI ui;

	// fonts
	BitmapFont std_font;
	float fontScale = 0.25f;

	// textures
	Texture village_txtr;
	Texture city_txtr;
	Texture street_txtr;
	Texture robber_txtr;

	PolygonSpriteBatch polyBatch;
	PolygonSprite polySprite;
	float[] ellipseVertices = new float[6 * 3 * 2];

	public LocalGameLogic() {
		state = new LocalState();
	}

	public void setCore(Core core) {
		this.core = core;
	}

	public void setUI(LocalUI ui) {
		this.ui = ui;
	}

	void init() {
		std_font = FontMgr.getFont(120);
		std_font.getData().setScale(fontScale);
		village_txtr = new Texture(Gdx.files.internal("res/village.png"), true);
		village_txtr.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		city_txtr = new Texture(Gdx.files.internal("res/city.png"), true);
		city_txtr.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		street_txtr = new Texture(Gdx.files.internal("res/street.png"), true);
		street_txtr.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		robber_txtr = new Texture(Gdx.files.internal("res/robber.png"), true);
		robber_txtr.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
	}

	@Override
	public void set_mode(GameMode new_mode) {
		state.mode = new_mode;
	}

	@Override
	public void update_new_map(Field[][] fields , java.util.Map<Vector2, Resource> harbours) {
		state.harbours = harbours;
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
					else {
						if(fields[x][y].resource == Resource.DESERT)
							state.robberPosition = Map.index_to_position(x, y);
					}
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
			if(state.villages.containsKey(user)) {
				state.villages.get(user).add(Map.index_to_building_position(building.get_position()));
			}else {
				List<Vector2> villages = new LinkedList<Vector2>();
				villages.add(Map.index_to_building_position(building.get_position()));
				state.villages.put(user, villages);
			}

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
			for (java.util.Map.Entry<Resource, List<Vector2>> resource : state.field_resources.entrySet()) {
				for (Vector2 pos : resource.getValue()) {
					if (resource.getKey() != Resource.OCEAN) {
						sr.begin(ShapeType.Filled);
						sr.setColor(new Color(0.6f, 0.6f, 0.6f, 1.f).gdx());
						sr.ellipse(pos.x - Map.field_size / 2 - Map.border_size,
								pos.y - Map.field_size / 2 - Map.border_size, Map.field_size + Map.border_size * 2,
								Map.field_size + Map.border_size * 2, 30, 6);
						sr.end();

					}

					sr.begin(ShapeType.Filled);
					Gdx.gl.glEnable(GL20.GL_BLEND);
					Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					sr.setColor(resource.getKey().get_color().gdx());
					sr.ellipse(pos.x - Map.field_size / 2, pos.y - Map.field_size / 2, Map.field_size, Map.field_size,
							30, 6);
					sr.end();
					/*
					Gdx.gl.glDisable(GL20.GL_BLEND);
					Texture texture = TextureMgr.getTexture("clay");
					Pixmap pixMap = new Pixmap(Gdx.files.local("assets/res/woodField.png"));
					if(resource.getKey() == Resource.WOOD)
						pixMap = new Pixmap(Gdx.files.local("assets/res/woodField.png"));
					if(resource.getKey() == Resource.GRAIN)
						pixMap = new Pixmap(Gdx.files.local("assets/res/grainField.png"));
					if(resource.getKey() == Resource.OCEAN)
						pixMap = new Pixmap(Gdx.files.local("assets/res/oceanField.png"));
					if(resource.getKey() == Resource.WOOL)
						pixMap = new Pixmap(Gdx.files.local("assets/res/woolField.png"));
					if(resource.getKey() == Resource.CLAY)
						pixMap = new Pixmap(Gdx.files.local("assets/res/clayField.png"));
					if(resource.getKey() == Resource.ORE)
						pixMap = new Pixmap(Gdx.files.local("assets/res/oreField.png"));

					TextureRegion region = new TextureRegion(new Texture(pixMap));
					Gdx.gl.glEnable(GL20.GL_BLEND);
					Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					sb.begin();

					//sb.draw(texture, pos.x- Map.field_size / 2, pos.y- Map.field_size / 2, Map.field_size, Map.field_size);
					sb.draw(region, pos.x- Map.field_size / 2, pos.y- Map.field_size / 2, Map.field_size / 2, Map.field_size / 2, Map.field_size, Map.field_size, 1f, 1f, 180f);
					sb.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					//PolygonRegion region = createFieldRegion(pos.x - Map.field_size / 2, pos.y - Map.field_size / 2, resource.getKey());
					//polySprite = new PolygonSprite(region);
					/*polySprite.setOrigin(0, 0);
					polyBatch = new PolygonSpriteBatch();
					polyBatch.setProjectionMatrix(sb.getProjectionMatrix());
					polyBatch.begin();
					//polySprite.draw(polyBatch);
					polyBatch.end();*/

				}
			}

			// render field numbers
			for (java.util.Map.Entry<Byte, List<Vector2>> number : state.field_numbers.entrySet()) {
				std_font.getData().setScale(
						(1.5f - (float) Math.abs(number.getKey() - (Map.NUMBER_COUNT + 4) / 2) * 0.17f) * fontScale);
				GlyphLayout layout = new GlyphLayout(std_font, "" + number.getKey());

				for (Vector2 pos : number.getValue()) {
					sb.begin();
					sb.setColor(Color.WHITE.gdx());
					std_font.draw(sb, "" + number.getKey(), pos.x - layout.width / 2, pos.y - layout.height / 2);
					sb.end();
				}
			}
			std_font.getData().setScale(fontScale);// reset

			// render buildings
			Sprite village_sprite = new Sprite(TextureMgr.getTexture("village"));
			village_sprite.flip(false, true);
			Sprite city_Sprite = new Sprite(TextureMgr.getTexture("city"));
			city_Sprite.flip(false, true);
			Sprite street_Sprite = new Sprite(TextureMgr.getTexture("street"));
			street_Sprite.flip(false, true);

			sb.begin();
			for (java.util.Map.Entry<Integer, List<AbstractStreet>> ub : state.streets.entrySet()) {
				sb.setColor(state.player_data.get(ub.getKey()).getColor().gdx());
				for (AbstractStreet street : ub.getValue()) {
					sb.draw(street_Sprite, street.position.x - street_Sprite.getWidth() * 0.5f * 0.08f,
							street.position.y - street_Sprite.getHeight() * 0.5f * 0.08f,
							street_Sprite.getWidth() * 0.5f * 0.08f, street_Sprite.getHeight() * 0.5f * 0.08f,
							street_Sprite.getWidth() * 0.08f, street_Sprite.getHeight() * 0.08f, 1.f, 1.f,
							street.rotation);
				}
			}
			for (java.util.Map.Entry<Integer, List<Vector2>> ub : state.villages.entrySet()) {
				sb.setColor(state.player_data.get(ub.getKey()).getColor().gdx());
				for (Vector2 pos : ub.getValue()) {
					sb.draw(village_sprite, pos.x - village_sprite.getWidth() * 0.05f,
							pos.y - village_sprite.getHeight() * 0.06f, village_sprite.getWidth() * 0.1f,
							village_sprite.getHeight() * 0.1f);
				}
			}
			for (java.util.Map.Entry<Integer, List<Vector2>> ub : state.cities.entrySet()) {
				sb.setColor(state.player_data.get(ub.getKey()).getColor().gdx());
				for (Vector2 pos : ub.getValue()) {
					sb.draw(city_Sprite, pos.x - city_Sprite.getWidth() * 0.05f,
							pos.y - city_Sprite.getHeight() * 0.05f, city_Sprite.getWidth() * 0.1f,
							city_Sprite.getHeight() * 0.1f);
				}
			}
			//Render Robber
			Sprite spriteRobber = new Sprite(robber_txtr);
			spriteRobber.flip(false, true);
			//Vector2 pos = Map.index_to_position(state.robberPosition.x , state.robberPosition.y);
			sb.setColor(Color.BLACK.gdx());
			sb.draw(spriteRobber, state.robberPosition.x - spriteRobber.getWidth() * 0.05f,
					state.robberPosition.y - spriteRobber.getHeight() * 0.05f, spriteRobber.getWidth() * 0.1f,
					spriteRobber.getHeight() * 0.1f);
			sb.end();

			for(Entry<Vector2, Resource> entry : state.harbours.entrySet()) {
				if(entry.getValue() == null) { // null = 3 for 1
					sr.begin(ShapeType.Filled);
					sr.setColor(new Color(0, 0, 0, 250).gdx());
					sr.ellipse(Map.index_to_position(entry.getKey().x, entry.getKey().y).x - Map.field_size / 2 + 30,
							Map.index_to_position(entry.getKey().x, entry.getKey().y).y - Map.field_size / 2 + 30, Map.field_size - 60,
							Map.field_size - 60,  30, 6);
					sr.end();
				}else {
					sr.begin(ShapeType.Filled);
					sr.setColor(entry.getValue().get_color().gdx());
					sr.ellipse(Map.index_to_position(entry.getKey().x, entry.getKey().y).x - Map.field_size / 2 + 30,
							Map.index_to_position(entry.getKey().x, entry.getKey().y).y - Map.field_size / 2 + 30, Map.field_size - 60,
							Map.field_size - 60,  30, 6);
					sr.end();
				}

			}
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
		}else if(state.curr_action == Action.moveRobber) {
			core.moveRobber(id, position);
		}else if(state.devCard != null) {
			if(state.devCard.type.equals(DevCardType.FREE_STREETS)) {
				if(((DevCard.FreeStreets) state.devCard.data).remainedFreeStreets >= 1) {
					Vector3i pos = Map.position_to_street_index(position);
					((DevCard.FreeStreets) state.devCard.data).addPosition(pos);
					((DevCard.FreeStreets) state.devCard.data).remainedFreeStreets--;
					core.playCard(id, state.devCard);
				}
			}
		}
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void setRobberPosition(Vector2 robberPosition) {
		state.robberPosition = robberPosition;
		state.curr_action = null;
		ui.switch_to_idle();
		ui.enableAllButton(state.isCurrentPlayer);
	}

	public void resetGame() {
		state.cities = new java.util.HashMap<Integer, List<Vector2>>();
		state.streets = new java.util.HashMap<Integer, List<AbstractStreet>>();
		state.villages = new java.util.HashMap<Integer, List<Vector2>>();
	}

	PolygonRegion createFieldRegion(float x, float y, Resource r) { //coordinates for left bottom corner of invisible rect
		createFieldVertices(x, y, Map.field_size + Map.border_size * 2, Map.field_size + Map.border_size * 2);
		return  new PolygonRegion(new TextureRegion(TextureMgr.getTexture(r.toString().toLowerCase())),
				 this.ellipseVertices,
				 new short[] {
						 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18
				 });
	}

	void createFieldVertices(float x, float y, float width, float height) {
		float angle = 2 * MathUtils.PI / 6;
		float rotation = MathUtils.PI * 30 / 180f;
		float sin = MathUtils.sin(rotation);
		float cos = MathUtils.cos(rotation);
		float cx = x + width / 2, cy = y + height / 2;
		float x1 = width * 0.5f;
		float y1 = 0;
		for(int i = 0; i < 6; i++) {
			addVertex2FieldEllipse(cx + cos * x1 - sin * y1, cy + sin * x1 + cos * y1, i * 3);
			addVertex2FieldEllipse(cx, cy, i * 3 + 1);
			x1 = (width * 0.5f * MathUtils.cos((i + 1) * angle));
			y1 = (height * 0.5f * MathUtils.sin((i + 1) * angle));
			addVertex2FieldEllipse(cx + cos * x1 - sin * y1, cy + sin * x1 + cos * y1, i * 3 + 2);
		}

	}

	void addVertex2FieldEllipse(float x, float y, int idxVertex) {
		this.ellipseVertices[idxVertex * 2 + 0] = x;
		this.ellipseVertices[idxVertex * 2 + 1] = y;
	}
}
