package local;

import org.jsfml.graphics.Color;
import java.io.Serializable;

// This class should be used in "local" environment instead of core.Player
public class LocalPlayer implements Serializable{
	private String name;
	private int score = 0;
	private Color color;

	public LocalPlayer(String name, int score, Color color) {
		this.name = name;
		this.score = score;
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}

	public int getScore() {
		return this.score;
	}

	public Color getColor() {
		return color;
	}
}