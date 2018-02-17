package local;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import data.Resource;

public class TradeDemand implements Serializable{
	public enum Vendor{
		PLAYER,
		BANK,
		HARBOUR,
		COMPUTER;
	}
	private Vendor vendor;
	
	Map<Resource, Boolean> wantedResources = new HashMap<Resource, Boolean>();
	Map<Resource, Boolean> offeredResources = new HashMap<Resource, Boolean>();
	int demander_id;
	public TradeDemand() {
		
	}
	public void addWantedResource(Resource r) {
		wantedResources.put(r, true);
	}
	
	public void removeWantedResource(Resource r) {
		wantedResources.remove(r);
	}
	
	public Map<Resource, Boolean> getWantedResources(){
		return this.wantedResources;
	}
	public void addOfferedResource(Resource r) {
		offeredResources.put(r, true);
	}
	
	public void removeOfferedResource(Resource r) {
		offeredResources.remove(r);
	}
	
	public Map<Resource, Boolean> getOfferedResources(){
		return this.offeredResources;
	}
	public void set_demander_id(int i) {
		this.demander_id = i;
	}
	public int get_demander_id() {
		return this.demander_id;
	}
	
	public Vendor getVendor() {
		return vendor;
	}
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
}
