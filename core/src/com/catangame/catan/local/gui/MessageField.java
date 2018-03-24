package com.catangame.catan.local.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.utils.FontMgr;

public class MessageField extends Widget{
	private Message msg;
	private BitmapFont font;
	private float senderWidth = 0;
	private String firstLine = "";
	private boolean formatted = false;
	
	public MessageField(Message msg, Rectangle bounds) {
		super(bounds);
		this.msg = msg;
		font = FontMgr.getFont(FontMgr.Type.OPEN_SANS_REGULAR, 12);
		if(msg.sender != null) {
			GlyphLayout senderContainer = new GlyphLayout(font, msg.sender.getName());
			senderWidth = senderContainer.width;	
		}
		
		
		if(!formatted && !msg.msg.contains("\n")) {
			format();
		}else {
			bounds.height = new GlyphLayout(font, msg.msg).height;
			bounds.width = new GlyphLayout(font, msg.msg).width;
		}
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
		//super.render(sr, sb);
		sb.begin();
		if(msg.sender != null) {
			font.setColor(msg.sender.getColor().gdx());
			font.draw(sb, msg.sender.getName(), bounds.x, bounds.y );
			font.setColor(Color.BLACK);
		}else {
			font.setColor(new Color(100,100,100, 255));
		}
		
		font.draw(sb, this.msg.firstLine, bounds.x + senderWidth + 10, bounds.y );
		font.draw(sb, msg.msg, bounds.x, bounds.y);
		sb.end();
	}
	@Override
	public void set_position(Vector2 pos) {
		update_bounds(new Rectangle(pos.x, pos.y, bounds.width, bounds.height));
	}

	@Override
	public void do_mouse_click(Vector2 pos) {	
	}

	public void set_font(BitmapFont font) {
		this.font = font;
		//format();
	}
	
	private void format() {
		formatted = true;
		float messageWidth =  new GlyphLayout(font, msg.msg).width;
		if(this.msg.sender != null) {
			int currentCut = 5;
			float lineWidth = 0;
			do {
				lineWidth =  new GlyphLayout(font, msg.msg.substring(0, currentCut)).width;
				currentCut = currentCut + 2;
			}while(lineWidth + senderWidth < bounds.width && currentCut < this.msg.msg.length()-1);
			this.firstLine = this.msg.msg.substring(0, currentCut);
			this.msg.firstLine = firstLine;
			this.msg.msg = "\r\n" + this.msg.msg.substring(currentCut);
		}
		
		int lastCut = 0;
		messageWidth = new GlyphLayout(font, msg.msg).width;
		if(bounds.width < messageWidth) {
			int numLines = Math.round(messageWidth / bounds.width);
			int currentCut = 0;
			for(int i  = 0; i < numLines; i++) {
				float lineWidth = messageWidth;
				do {
					lineWidth =  new GlyphLayout(font, msg.msg.substring(lastCut, currentCut)).width;
					currentCut = currentCut + 2;
				}while(lineWidth < bounds.width && currentCut < this.msg.msg.length()-1);
				lastCut = currentCut;
				this.msg.msg = new StringBuilder(this.msg.msg).insert(currentCut, "\r\n").toString();
			}
		}
		if(msg.sender != null) {
			bounds.height = new GlyphLayout(font, msg.msg).height + new GlyphLayout(font, msg.sender.getName()).height;
		}else {
			bounds.height = new GlyphLayout(font, msg.msg).height + 10;
		}
		
		bounds.width = new GlyphLayout(font, msg.msg).width + new GlyphLayout(font, msg.firstLine).width;
		formatted = true;
	}
	
	public float getHeight() {
		return this.bounds.height;
	}

}
