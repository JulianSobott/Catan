package core;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import data.Field;

public class LocalFilehandler {
	private OutputStream outputStream = null;
	private ObjectOutputStream output = null;
	private InputStream inputStream = null;
	private ObjectInputStream input = null;
	
	private Field[][] fields = null;
	private List<Player> player = null;
		
	public LocalFilehandler() {
		try {
			outputStream = new FileOutputStream("gameData");
			output = new ObjectOutputStream(outputStream);
			inputStream = new FileInputStream("gameData");
			input = new ObjectInputStream(inputStream);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveGame(Field[][] fields, List<Player> player) {
		//Map
		try {
			output.writeObject(fields);
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Player
		try {
			output.writeObject(player);
			output.flush();
			output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Saved Game!");
		//State
	}
	
	@SuppressWarnings("unchecked")
	public void loadGame() {
		try {
			for(;;) {
				Object o = input.readObject();
				Class c = o.getClass();
				System.out.println(c.getName());
				if(c.getName() == "Field") {
					fields = (Field[][]) o;
				}else if(c.getName() == "core.Map") {
					player = (List<Player>) o;
				}
				System.out.println("Load Data");
			}
		}catch(EOFException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Field[][] getFields() {
		return this.fields;
	}

	public List<Player> getPlayer() {
		return this.player;
	}
}
