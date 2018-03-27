package com.catangame.catan.superClasses;

import com.catangame.catan.network.Networkmanager;
import com.catangame.catan.network.Packet;

public abstract class Server extends Networkmanager{

	@Override
	public void closeAllResources() {
		// TODO Auto-generated method stub
		
	}
	
	public abstract void message_from_client(int id, Packet packet);
	
	public abstract void message_to_client(int id, Packet packet);

	public abstract void set_id_last_joined(int id);

	public abstract void remove_client(int id);

}
