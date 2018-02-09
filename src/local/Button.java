package local;

import java.io.IOException;
import java.nio.file.Paths;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Button extends Widget{
	FloatRect button;
	private ClickEvent clickEvent;
	private String text;
	private static int heightMenuButtons = 100;
	private static int widthMenuButtons = 400;
	public Button(String text) {
		super(0, 0, widthMenuButtons, heightMenuButtons);
		this.text = text;
	}
	
	public void render(RenderTarget target) {
		Font font = new Font();
		try {
			font.loadFromFile(Paths.get("res/Ancient Modern Tales.otf"));
		} catch(IOException ex) {
		    //Failed to load font
		    ex.printStackTrace();
		}
		RectangleShape rs= new RectangleShape(new Vector2f(this.width, this.height));
		rs.move(new Vector2f(this.x, this.y));
		button = rs.getGlobalBounds();
		Text t = new Text(this.text, font);
		FloatRect rect= t.getLocalBounds();
		t.setOrigin(rect.width / 2, rect.height / 2);
		t.setPosition(this.x + this.width/2 , this.y + this.height/2);
		//t.move(new Vector2f(this.x, this.y));
		t.setColor(new Color(100, 70, 100));
		target.draw(rs);
		target.draw(t);
	}
	

	public boolean checkClicked(Vector2f mouseClick) {
		if(button.contains(mouseClick)) {
			this.clickEvent.handle();
			return true;
		}else {
			return false;
		}
	}
	
	
	public String getText() {
		return this.text;
	}

	public void setOnClick(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
	}
	
	public void setPosition(Vector2f pos) {
		this.x = (int) pos.x;
		this.y = (int) pos.y;
	}
	
	public void setPosition(int idx) {
		this.x = 0;
		this.y = (this.height + 20) * idx;
	}
	
	public static int getHeightMenuButtons() {
		return heightMenuButtons;
	}
	
	public static int getWidthMenuButtons() {
		return widthMenuButtons;
	}
}
