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
import com.catangame.catan.utils.FontMgr.Type;

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
		
		
		if(!formatted && !msg.msg.contains("\r\n") && msg.msg.length() > 0) {
			format();
		}else {
			bounds.height = new GlyphLayout(font, msg.msg).height + new GlyphLayout(font, msg.firstLine).height;
			if(this.msg.sender != null) {
				bounds.width = new GlyphLayout(font, msg.firstLine).width + new GlyphLayout(font, msg.sender.getName()).width + 10;
			}else {
				bounds.width = new GlyphLayout(font, msg.firstLine).width;
			}
			
		}
	}
	
	@Override
	public void render(ShapeRenderer sr, SpriteBatch sb) {
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
		int currentCut = 0;
		float lineWidth = 0;
		while(lineWidth + senderWidth < bounds.width && currentCut < this.msg.msg.length()-1) {
			lineWidth =  new GlyphLayout(font, msg.msg.substring(0, currentCut)).width;
			currentCut = currentCut + 1;
		};
		this.firstLine = this.msg.msg.substring(0, currentCut+1);
		this.msg.firstLine = firstLine;
		this.msg.msg = "\r\n" + this.msg.msg.substring(currentCut+1);
		if(msg.msg.length() == 2) {
			this.msg.msg = "";
		}
		
		int lastCut = 0;
		if(this.msg.msg != null) {
			messageWidth = new GlyphLayout(font, msg.msg).width;
			if(bounds.width < messageWidth) {
				int numLines = Math.round(messageWidth / bounds.width);
				currentCut = 0;
				for(int i  = 0; i < numLines; i++) {
					lineWidth = 0;
					while(lineWidth < bounds.width && currentCut < this.msg.msg.length()-1) {
						lineWidth =  new GlyphLayout(font, msg.msg.substring(lastCut, currentCut)).width;
						currentCut = currentCut + 1;
					};
					lastCut = currentCut;
					this.msg.msg = new StringBuilder(this.msg.msg).insert(currentCut+1, "\r\n").toString();
				}
			}
		}
		
		if(msg.sender != null) {
			bounds.height = new GlyphLayout(font, msg.msg).height + new GlyphLayout(font, msg.sender.getName()).height;
			bounds.width = new GlyphLayout(font, msg.sender.getName()).width + 10 + new GlyphLayout(font, msg.firstLine).width;
		}else {
			bounds.height = new GlyphLayout(font, msg.msg).height;
			bounds.width = new GlyphLayout(font, msg.firstLine).width;
		}
		
		formatted = true;
	}
	
	public float getHeight() {
		return this.bounds.height;
	}
	
	public void setSenderName(String name) {
		this.msg.sender.setName(name);
		format();
	}

}
