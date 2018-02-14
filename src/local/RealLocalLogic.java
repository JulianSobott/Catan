package local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import core.Map;
import data.Field;
import data.Resource;
import local.LocalState.GameMode;
import network.Command;
import network.DataIfc;
import superClasses.LocalLogic;

// TODO name?
public class RealLocalLogic extends LocalLogic{
	// state, ui & connection
	LocalState state;
	DataIfc data_connection; 

	// fonts
	Font std_font;

	public RealLocalLogic() {
		state = new LocalState();
	}
	
	public void set_data_interface(DataIfc data_connection) {
		this.data_connection = data_connection;
	}
	
	void init(Font std_font) {
		this.std_font = std_font;
	}

	public void set_mode(GameMode new_mode) {
		state.mode  = new_mode;
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

	void render_map(RenderTarget target) {
		if (state.mode == GameMode.game) {
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
			for (java.util.Map.Entry<Byte, List<Vector2f>> number : state.field_numbers.entrySet()) {
				Text text = new Text("" + number.getKey(), std_font);
			  text.setCharacterSize(40 - Math.abs(number.getKey()-(Map.NUMBER_COUNT+4)/2)*4);
				text.setOrigin(text.getGlobalBounds().width * 0.5f, text.getGlobalBounds().height * 0.5f);

				for (Vector2f pos : number.getValue()) {
					text.setPosition(pos);
					target.draw(text);
				}
			}
		}
	}

	void mouse_click_input(Vector2f position) {
		// TODO
	}
  
	public void diceResult(byte dice_result) {
		// TODO Auto-generated method stub
		System.out.println("Dice result at Client: " + dice_result);
	}

	public void build(int idPlayer, Command buildType, Vector2i position) {
		// TODO Auto-generated method stub
	}


}