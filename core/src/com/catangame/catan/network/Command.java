package com.catangame.catan.network;
public enum Command {
	DICE_RESULT,
	TRADE_DEMAND,
	TRADE_OFFER,
	ADD_TRADE_OFFER,
	ACCEPT_OFFER,
	CLOSE_TRADE_WINDOW,
	FIELD,
	SCORE,
	NEXT_TURN,
	SHOW_ACTUAL_PLAYER,
	STRING,
	BUY_DEVELOPMENT_CARD,
	PLAY_DEVELOPMENTCARD,
	//Pre Game Stuff
	PLAYER_DATA,
	NEW_MAP,
	BUILD_REQUEST,
	UPDATE_BUILDINGS,
	NEW_BUILDING,
	NAME,
	START_GAME, 
	INIT_SCOREBOARD, 
	SET_CURR_USER,
	SET_MODE, 
	SET_ID, 
	SHOW_KICKED, 
	SHOW_ALL_POSSIBLE_NAMES, 
	SHOW_NEW_MEMBER, 
	//After Game
	END_SCREEN;
	
}
