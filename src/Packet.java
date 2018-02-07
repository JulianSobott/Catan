

import java.io.Serializable;

public class Packet implements Serializable{
	private String code;
	
	public Packet(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
}
