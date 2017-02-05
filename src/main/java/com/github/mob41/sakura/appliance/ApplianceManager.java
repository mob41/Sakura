package com.github.mob41.sakura.appliance;

import java.util.List;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.power.PowerManager;
import com.github.mob41.sakura.power.PowerUse;

import java.util.ArrayList;

public class ApplianceManager {
	
	private List<Appliance> appliances;

	private final SakuraServer srv;
	
	public ApplianceManager(SakuraServer srv){
		this.srv = srv;
		appliances = new ArrayList<Appliance>(100);
	}
	
	public boolean registerDevice(Appliance appliance){
		return appliances.add(appliance);
	}
	
	public void unregisterDevice(int index){
		appliances.remove(index);
	}
	
	public void unregisterDevice(Appliance app){
		appliances.remove(app);
	}
	
	public int getIndex(String applianceName){
		for (int i = 0; i < appliances.size(); i++){
			if (appliances.get(i).getName().equals(applianceName)){
				return i;
			}
		}
		return -1;
	}
	
	public boolean isApplianceExist(String applianceName){
		return getIndex(applianceName) != -1;
	}
	
	public Appliance getAppliance(String applianceName){
		int index = getIndex(applianceName);
		
		return index == -1 ? null : getAppliance(index);
	}
	
	public Appliance getAppliance(int index){
		return appliances.get(index);
	}
	
	public List<Appliance> getAppliances(){
		return appliances;
	}
	
	public boolean turnOn(int index){
		Appliance appl = appliances.get(index);

		if (appl == null){
			return false;
		}
		
		if (appl.turnOn(srv)){
			PowerManager pm = srv.getPowerManager();
			if (pm.isPowerUseRegistered(appl.getName())){
				PowerUse powerUse = new PowerUse(appl.getName(), appl.getPowerUseWatts());
				pm.registerPowerUse(powerUse, true);
			} else {
				pm.setInUse(appl.getName(), true);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean turnOff(int index){
		Appliance appl = appliances.get(index);
		
		if (appl == null){
			return false;
		}
		
		if (appl.turnOff(srv)){
			PowerManager pm = srv.getPowerManager();
			if (pm.isPowerUseRegistered(appl.getName())){
				PowerUse powerUse = new PowerUse(appl.getName(), appl.getPowerUseWatts());
				pm.registerPowerUse(powerUse, false);
			} else {
				pm.setInUse(appl.getName(), false);
			}
			return true;
		} else {
			return false;
		}
	}
}
