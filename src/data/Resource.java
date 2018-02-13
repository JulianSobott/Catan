package data;
import org.jsfml.graphics.Color;

public enum Resource {
	OCEAN(new Color(0, 0, 255, 50)),// not a resource but is needed for the map
	WOOD(new Color(120, 100, 0)),
	WOOL(new Color(50, 180, 50)),
	GRAIN(new Color(255, 220, 0)),
	CLAY(new Color(180, 90, 0)),
	ORE(new Color(120, 120, 120));

	// data
	private Color color;

	Resource(Color color) {
		this.color = color;
	}

	public Color get_color() {
		return color;
	}
}
