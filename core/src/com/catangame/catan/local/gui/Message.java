package com.catangame.catan.local.gui;

import java.io.Serializable;

import com.catangame.catan.local.LocalPlayer;

public class Message implements Serializable{
	public String msg;
	public LocalPlayer sender;
	public int lines = 1;
	public Message(LocalPlayer sender, String msg) {
		this.sender = sender;
		this.msg = msg;
	}
	public Message(String msg) {
		this.msg = msg;
	}
	public void format(int length) {
		if(this.sender != null) {
			for(int i = 0; i < this.sender.getName().length()+ 3; i++) {
				this.msg = new StringBuilder(this.msg).insert(0, "  ").toString();
			}
		}
		if(this.msg.length() > length) {
			int numBreaks = this.msg.length() / length;
			this.lines = numBreaks+1;
			for(int i = 1; i <= numBreaks; i++) {
				this.msg = new StringBuilder(this.msg).insert(i*length, "\r\n").toString();
			}
		}
	}
}
