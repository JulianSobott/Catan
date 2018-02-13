package data;

import java.io.Serializable;

public class Field implements Serializable{
	public Resource resource;
	public byte number;

	public Field(Resource resource, byte number ){
		this.resource = resource;
		this.number = number;
	}
}