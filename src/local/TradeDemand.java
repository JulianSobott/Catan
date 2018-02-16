package local;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import data.Resource;

public class TradeDemand implements Serializable{
	
	Map<Resource, Boolean> wantedResources = new HashMap<Resource, Boolean>();
	Map<Resource, Boolean> offeredResources = new HashMap<Resource, Boolean>();
	
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
}
