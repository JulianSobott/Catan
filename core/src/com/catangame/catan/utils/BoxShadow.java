package com.catangame.catan.utils;

public class BoxShadow {
	
	public BoxShadow boxshadow = null;
	public Color color;
	public int h_offset = 0;
	public int v_offset = 0;
	public int spread = 0;
	
	public BoxShadow(Color color, int spread){
		this.color = color;
		this.spread = spread;
	}
	
	public BoxShadow(Color color, int spread, int h_offset, int v_offset){
		this.color = color;
		this.spread = spread;
		this.h_offset = h_offset;
		this.v_offset = v_offset;
	}

	public BoxShadow(Color color, int spread, int h_offset, int v_offset, BoxShadow boxShadow) {
		this.color = color;
		this.spread = spread;
		this.h_offset = h_offset;
		this.v_offset = v_offset;
		this.boxshadow = boxShadow;
	}
}
