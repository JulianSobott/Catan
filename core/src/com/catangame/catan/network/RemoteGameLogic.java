package com.catangame.catan.network;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.Building;
import com.catangame.catan.data.Field;
import java.util.List;
import java.util.Map;
import com.catangame.catan.local.LocalState.GameMode;
import com.catangame.catan.superClasses.GameLogic;

public class RemoteGameLogic extends GameLogic {
	private Server server;
	
	public RemoteGameLogic(Server server) {
		this.server = server;
	}

	@Override
	public void update_new_map(Field[][] fields) {
		server.message_to_client(id, new Packet(Command.NEW_MAP, new Packet.New_Map(fields)));
	}

	@Override
	public void set_mode(GameMode mode) {
		server.message_to_client(id, new Packet(Command.SET_MODE, new Packet.NewMode(mode)));
	}

	@Override
	public void update_buildings(Map<Integer, List<Building>> buildings) {
		server.message_to_client(id, new Packet(Command.UPDATE_BUILDINGS, new Packet.UpdateBuildings(buildings)));
	}

	@Override
	public void add_building(int user, Building building) {
		server.message_to_client(id, new Packet(Command.NEW_BUILDING, new Packet.NewBuilding(user, building)));
	}

	@Override
	public void setID(int id) {
		this.id = id;
		server.message_to_client(id, new Packet(Command.SET_ID, new Packet.ID(id)));
	}

	@Override
	public void setRobberPosition(Vector2 robberPosition) {
		server.message_to_client(id, new Packet(Command.MOVE_ROBBER, new Packet.Position(robberPosition)));
	}

}
