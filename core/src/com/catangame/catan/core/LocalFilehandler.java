package com.catangame.catan.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.catangame.catan.data.Field;
import com.catangame.catan.data.SavedGame;
import com.catangame.catan.utils.TextureMgr;

public class LocalFilehandler {
	private OutputStream outputStream = null;
	private ObjectOutputStream output = null;
	private InputStream inputStream = null;
	private ObjectInputStream input = null;
	
	String folderPath = "../saves";
	
	private File folder;
	
	public LocalFilehandler() {
		System.out.println("filehandle");
		System.out.println(Gdx.files.local("").parent());
		if(!Gdx.files.local(folderPath).exists()) {
			Gdx.files.local("../saves").mkdirs();
		}
	}
	
	public List<SavedGame> getAllGames() {
		List<SavedGame> allSavedGames = new ArrayList<SavedGame>();		
		FileHandle[] games = Gdx.files.local(folderPath).list();
		for(final FileHandle game : games) {
			System.out.println(game.extension());
			if(game.extension().equals("catan")) {
				SavedGame savedGame = loadGame(game);
				allSavedGames.add(savedGame);
			}		
		}
		return allSavedGames;
	}
	
	public void saveGame(SavedGame game) {
		try {
			outputStream = new FileOutputStream(new File(Paths.get("").toAbsolutePath().toString()).getParent() + "/saves/"+game.getName()+".catan");
			output = new ObjectOutputStream(outputStream);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			output.writeObject(game);
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Saved Game!");
	}
	
	public SavedGame loadGame(FileHandle file) {
		if(file.extension().equals("catan")) {
			SavedGame game = null;
			//Init resources
			try {
				inputStream = file.read();
				input = new ObjectInputStream(inputStream);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Load game
			try {
				game = (SavedGame) input.readObject();
			}catch(EOFException e) {
				System.out.println("Finished Loading gameData");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return game;
		}else {
			return null;
		}
		
	}

}
