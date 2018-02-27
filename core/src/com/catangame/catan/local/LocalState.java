package com.catangame.catan.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import com.catangame.catan.core.Player;
import com.catangame.catan.data.Resource;

public class LocalState {
	// runtime information
	public enum GameMode {
		main_menu, game,

	}

	GameMode mode;

	public enum Action {
		idle,
		build_village,
		build_city,
		build_street,
	}
	Action curr_action = Action.idle;// describes the current action of the user;

	List<LocalPlayer> player_data = new ArrayList<LocalPlayer>();
	Player my_player_data = new Player("Anonymous", 0, Color.BLUE);
	String curr_player;

	Map<Resource, List<Vector2>> field_resources = new HashMap<Resource, List<Vector2>>();// maps resource fields to their positions on the board
	Map<Byte, List<Vector2>> field_numbers = new HashMap<Byte, List<Vector2>>();// maps field numbers to their positions on the board
	Map<Integer, List<Vector2>> villages = new HashMap<Integer, List<Vector2>>();// maps players to building types to positions
	Map<Integer, List<Vector2>> cities = new HashMap<Integer, List<Vector2>>();// maps players to building types to positions
	Map<Integer, List<AbstractStreet>> streets = new HashMap<Integer, List<AbstractStreet>>();// maps players to building types to positions

}