package com.catangame.catan.utils;

import java.util.ArrayList;
import java.util.List;

import com.catangame.catan.core.Building;
import com.catangame.catan.math.Vector3i;

public class StreetNode {
	private List<StreetNode> children = new ArrayList<>();
	private List<StreetNode> parents = new ArrayList<>();
	private com.catangame.catan.core.Building street;
	private int level = 0;
	
	public StreetNode(Building street) {
		this.street = street;
	}
	
	public StreetNode(Building street, StreetNode parent) {
		this.street = street;
		addParent(parent);
	}
	
	public void addParent(StreetNode parent) {
		this.parents.add(parent);
		this.level = parent.getLevel() + 1;
	}
	
	public void addChild(Building b) {
		StreetNode child = new StreetNode(b);
		child.addParent(this);
		this.children.add(child);
	}
	
	public void addChild(StreetNode childNode) {
		childNode.addParent(this);
		this.children.add(childNode);
	}
	
	public void addChildren(List<StreetNode> children) {
		for(StreetNode node : children) {
			this.children.add(node);
		}
	}
	
	public boolean isRoot() {
		return (this.parents.size() == 0);
	}
	
	public boolean isEnd() {
		return (this.children.size() == 0);
	}
	
	public Building getStreet() {
		return this.street;
	}
	
	public List<StreetNode> getChildren(){
		return this.children;
	}
	
	public int getLevel() {
		return this.level;
	}
	public StreetNode singleContains(StreetNode child1){
		for(StreetNode c : this.children) {
			if(c.isEnd() && Vector3i.are_equal(c.getStreet().get_position(), child1.getStreet().get_position())) {
				return c;
			}
		}
		return null;
	}
}
