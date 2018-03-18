package com.catangame.catan.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.LocalCore;
import com.catangame.catan.superClasses.Server;
import com.catangame.catan.utils.Color;

public class RemoteServer extends Server {
	
	private Socket socket;
	private String serverIP = "127.0.0.1";
	private final int PORT = 56789;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private LocalCore core;
	
	private boolean connectionToServer = false;
	
	public RemoteServer(LocalCore core) {
		this.core = core;
		try {
			this.socket = new Socket(this.serverIP, PORT);
			try {
				output = new ObjectOutputStream(this.socket.getOutputStream());
				input = new ObjectInputStream(this.socket.getInputStream());
				connectionToServer = true;
			}catch(IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Create new Online Game
		try {
			output.writeObject(new Packet(Command.CREATE_NEW_GAME));
			output.flush();
			output.reset();
		} catch (IOException e) {
			System.err.println("Cant Create new Game");
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void message_from_client(int id, Packet packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void message_to_client(int id, Packet packet) {
		try {
			output.writeObject(new Packet(0, id, packet.getCommand(), packet.data));
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(connectionToServer && input != null) {
					try {
						final Packet packet;
						packet = (Packet) input.readObject();
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								messageFromServer(packet);
							}
						});	
					}catch(IOException e) {
						e.printStackTrace();
						System.err.println("Connection to Server closed (ClientInputListener Line 26)");
						connectionToServer = false;
					}catch(ClassNotFoundException e) {
						System.err.println("Object is from unknown Class");
						e.printStackTrace();
					}
				}
				return;
			}
		}).start();	
	}

	private void messageFromServer(Packet packet) {
		switch (packet.getCommand()) {
		case STRING:
			System.out.println("Server reached Message: " + packet.getDebugString());
			break;
		case NAME:
			String name = ((Packet.Name) packet.data).getName();
			Color color = ((Packet.Name) packet.data).getColor();
			core.register_new_user(name, color);
			break;
		case BUILD_REQUEST:
			core.buildRequest(packet.sender, ((Packet.BuildRequest) packet.data).getBuildingType(),
					((Packet.BuildRequest) packet.data).getPosition());
			break;
		case NEXT_TURN:
			core.nextTurn(packet.sender);
			break;
		case TRADE_DEMAND:
			core.new_trade_demand(((Packet.TradeDemand) packet.data).getTradeDemand());
			break;
		case TRADE_OFFER:
			core.new_trade_offer(((Packet.TradeOffer) packet.data).getTradeOffer());
			break;
		case ACCEPT_OFFER:
			core.acceptOffer(((Packet.TradeOffer) packet.data).getTradeOffer());
			break;
		case CLOSE_TRADE_WINDOW:
			core.closeTrade();
			break;
		case DEMAND_DECLINED:
			core.declineTradeDemand(((Packet.ID) packet.data).getID());
			break;
		case BUY_DEVELOPMENT_CARD:
			core.buyDevelopmentCard(packet.sender);
			break;
		case PLAY_DEVELOPMENTCARD:
			core.playCard(packet.sender, ((Packet.Developmentcard) packet.data).getCard());
			break;
		case TAKE_RESOURCE:
			core.removeResources(packet.sender, ((Packet.Resouces) packet.data).resources);
			break;
		case MOVE_ROBBER:
			core.moveRobber(packet.sender, (Vector2) ((Packet.Position) packet.data).position);
			break;
		case STEEL_RESOURCE:
			core.stealResource(packet.sender, (int) ((Packet.Num) packet.data).num);
			break;
		default:
			System.err.println("Unknown Command reached Server: " + packet.getCommand());
		}
	}

	@Override
	public void set_id_last_joined(int id) {
		try {
			output.writeObject(new Packet(Command.ID_LAST_JOINED, new Packet.ID(id)));
			output.flush();
			output.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void remove_client(int id) {
		// TODO Auto-generated method stub
		
	}

}
