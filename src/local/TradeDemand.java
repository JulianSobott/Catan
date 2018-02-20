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
	
	Map<Resource, Integer> wantedResources = new HashMap<Resource, Integer>();
	Map<Resource, Integer> offeredResources = new HashMap<Resource, Integer>();
	int demander_id;
	public TradeDemand() {
		
	}
	public void addWantedResource(Resource r) {
		if(wantedResources.containsKey(r)) {
			wantedResources.put(r, wantedResources.get(r) + 1);
		}else {
			wantedResources.put(r, 1);
		}
	}
	
	public void removeWantedResource(Resource r) {
		if(wantedResources.containsKey(r)) {
			if(wantedResources.get(r) <= 1) {
				wantedResources.remove(r);
			}else {
				wantedResources.put(r, wantedResources.get(r) - 1);
			}
		}
	}
	
	public Map<Resource, Integer> getWantedResources(){
		return this.wantedResources;
	}
	public void addOfferedResource(Resource r) {
		offeredResources.put(r, 1);
	}
	
	public void removeOfferedResource(Resource r) {
		offeredResources.remove(r);
	}
	
	public Map<Resource, Integer> getOfferedResources(){
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
