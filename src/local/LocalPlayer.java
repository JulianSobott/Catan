package local;

import java.io.Serializable;

// This class should be used in "local" environment instead of core.Player
public class LocalPlayer implements Serializable{
	private String name;
	private int score = 0;

	public LocalPlayer(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return this.name;
	}

	public int getScore() {
		return this.score;
	}
}