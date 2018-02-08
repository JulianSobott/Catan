package local;
import org.jsfml.graphics.Color;

public enum Resource {
	WOOD(new Color(120, 100, 0)),
	WOOL(new Color(50, 180, 50)),
	GRAIN(new Color(255, 220, 0)),
	CLAY(new Color(180, 90, 0)),
	ORE(new Color(120, 120, 120));

	// data
	Color color;

	Resource(Color color) {
		this.color = color;
	}
}
