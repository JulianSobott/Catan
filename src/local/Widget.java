package local;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public abstract class Widget {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	public Widget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public abstract void render(RenderTarget target);

	public abstract boolean checkClicked(Vector2f mouseClick);
}
