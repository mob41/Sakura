package com.github.mob41.sakura.appliance;

import com.github.mob41.sakura.api.SakuraServer;

public abstract class Appliance{
	
	public static final int NO_SOURCE = -1;

	public static final int SOURCE_VIRTUAL_CALCUATION = 0;
	
	public static final int SOURCE_DEVICE_SPECIFICATION = 1;
	
	public static final int SOURCE_PHYSICAL_CURRENT_MONITOR_DEVICE = 2;
	
	private final String lightName;
	
	private final float powerUseWatts;
	
	private final int powerUseCalcSource;
	
	public Appliance(String lightName, float powerUseWatts, int powerUseCalcSource){
		this.lightName = lightName;
		this.powerUseCalcSource = powerUseCalcSource;
		this.powerUseWatts = powerUseWatts;
	}
	
	public abstract boolean isTurnedOn();
	
	public abstract boolean turnOn(SakuraServer srv);
	
	public abstract boolean turnOff(SakuraServer srv);
	
	public float getPowerUseWatts(){
		return powerUseWatts;
	}
	
	public float getPowerUseKiloWatts(){
		return powerUseWatts / 1000;
	}
	
	public String getPowerUseCalcSource(){
		if (powerUseCalcSource == 0){
			return "Virtual calcuation";
		} else if (powerUseCalcSource == 1){
			return "Device specification";
		} else if (powerUseCalcSource == 2){
			return "Physical current monitoring device";
		} else if (powerUseCalcSource == 3){
			return "No source";
		} else {
			return "Unknown";
		}
	}
	
	public String getName(){
		return lightName;
	}
}