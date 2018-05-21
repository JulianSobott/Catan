package com.catangame.catan.data;

import java.util.List;

public enum DevCardType {
	KNIGHT(.2), //Move the robber and take a Card from a corresponding player
	POINT(.2), //Get 1||2 points
	FREE_RESOURCES(.2), //Get 2 free Resources
	FREE_STREETS(.2), //Get 2 free street
	MONOPOL(.2); //Get all Cards from all players from a specific resource
	
	double ratio;
	DevCardType(double ratio){
		this.ratio = ratio;
	}
	public double getRatio() {
		return this.ratio;
	}
}



