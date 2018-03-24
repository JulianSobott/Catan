package com.catangame.catan.local.gui;

import java.io.Serializable;

import com.catangame.catan.local.LocalPlayer;

public class Message implements Serializable{
	public String msg;
	public String firstLine = "";
	public LocalPlayer sender;
	public Message(LocalPlayer sender, String msg) {
		this.sender = sender;
		this.msg = msg;
	}
	public Message(String msg) {
		this.msg = msg;
	}
}
