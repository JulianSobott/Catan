package local;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

public class TextField extends Widget{
	FloatRect textField;
	private String text = "";
	private boolean isActive = false;
	private int borderThickness = 1;
	
	
	public TextField() {
		super(500, 10, 200, 25);
	}

	@Override
	public void render(RenderTarget target) {
		Font font = new Font();
		try {
			font.loadFromFile(Paths.get("res/Ancient Modern Tales.otf"));
		} catch(IOException ex) {
		    //Failed to load font
		    ex.printStackTrace();
		}
		Text t = new Text(this.text, font);
		FloatRect rect= t.getLocalBounds();
		t.setOrigin(-5, 20);
		t.setPosition(this.x , this.y + this.height/2);
		t.setScale(.8f, .8f);
		//t.move(new Vector2f(this.x, this.y));
		t.setColor(new Color(100, 70, 100));
		
		RectangleShape rs = new RectangleShape(new Vector2f(this.width, this.height));
		rs.move(new Vector2f(this.x, this.y));
		textField = rs.getGlobalBounds();
		rs.setFillColor(Color.WHITE);
		if(isActive) {
			rs.setOutlineColor(Color.RED);
		}else {
			rs.setOutlineColor(Color.BLACK);
		}
		rs.setOutlineThickness(borderThickness);
		target.draw(rs);
		target.draw(t);
	}
	
	public void setOnClick() {
		this.text = "Clicked";
	}
	
	public boolean checkClicked(Vector2f mouseClick) {
		if(textField.contains(mouseClick)) {
			this.isActive = true;
			return true;
		}else {
			return false;
		}
	}
	
	public void addChar(Keyboard.Key key, boolean shift) {
		if(key.toString().length() == 1) {
			if(shift) {
				this.text += key.toString();
			}else {
				this.text += key.toString().toLowerCase();
			}	
		}else if(key.toString().contains("NUM")){
			this.text += key.toString().substring(3, 4);
		}else {
			switch(key.toString()) {
			case "BACKSPACE":
				if(this.text != null && this.text.length() > 0) {
					this.text = text.substring(0, text.length()-1);
				}
				break;
			case "SPACE":
				this.text += " ";
				break;
			case "LSHIFT":
				break;
			case "RSHIFT":
				break;
			case "COMMA":
				this.text += ",";
				break;
			case "PERIOD":
				this.text += ".";
				break;
			default:
				this.text += ":-)";
			}	
		}
	}
	
	
	
}
