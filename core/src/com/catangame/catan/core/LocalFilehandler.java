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

import com.catangame.catan.data.Field;
import com.catangame.catan.data.SavedGame;

public class LocalFilehandler {
	private OutputStream outputStream = null;
	private ObjectOutputStream output = null;
	private InputStream inputStream = null;
	private ObjectInputStream input = null;
	
	private File folder;
	
	public LocalFilehandler() {
		
	}
	
	public List<SavedGame> getAllGames() {
		List<SavedGame> allSavedGames = new ArrayList<SavedGame>();
		System.out.println(new File(Paths.get("").toAbsolutePath().toString()).getParent());
		folder = new File(new File(Paths.get("").toAbsolutePath().toString()).getParent() + "/saves");
		for(final File file : folder.listFiles()) {
			if(!file.isDirectory()) {	
				System.out.println(file.getName().substring(file.getName().lastIndexOf('.')));
				if(file.getName().lastIndexOf('.') > 1 && file.getName().substring(file.getName().lastIndexOf('.')).equals(".catan")) {
					SavedGame savedGame = loadGame(file);
					allSavedGames.add(savedGame);
				}		
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
	
	public SavedGame loadGame(File file) {
		SavedGame game = null;
		//Init resources
		try {
			inputStream = new FileInputStream(file);
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
	}

}
