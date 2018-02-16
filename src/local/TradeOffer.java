package local;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import data.Resource;

public class TradeOffer implements Serializable{
	private int demander_id;
	private int vendor_id;
	private Map<Resource, Integer> offeredResources = new HashMap<Resource, Integer>();
	private Map<Resource, Integer> demandedResources = new HashMap<Resource, Integer>();
	
	public void addOfferedResource(Resource r) {
		if(offeredResources.containsKey(r)) {
			offeredResources.put(r, offeredResources.get(r) + 1);
		}else {
			offeredResources.put(r, 1);
		}
	}
	
	public void substractOfferedResource(Resource r) {
		if(offeredResources.containsKey(r)) {
			if(offeredResources.get(r) <= 1) {
				offeredResources.remove(r);
			}else {
				offeredResources.put(r, offeredResources.get(r) - 1);
			}
		}
	}
	
	public Map<Resource, Integer> getOfferedResources(){
		return this.offeredResources;
	}
	public Map<Resource, Integer> getDemandedResources(){
		return this.demandedResources;
	}
	public void setDemandedResources(Map<Resource, Integer> demandedResources) {
		this.demandedResources = demandedResources;
	}
	public void setDemanderID(int id) {
		this.demander_id = id;
	}
	public int getDemanderID() {
		return this.demander_id;
	}

	public int getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(int vendor_id) {
		this.vendor_id = vendor_id;
	}
	
}
