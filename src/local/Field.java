package local;

import java.io.Serializable;

public class Field implements Serializable{
	Resource resource;
	byte number;

	public Field(Resource resource, byte number ){
		this.resource = resource;
		this.number = number;
	}
}