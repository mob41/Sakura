package com.github.mob41.sakura.power;

public class PowerUse {

	private final String name;
	
	private final float wattsPerHour;
	
	private boolean inUse = false;
	
	public PowerUse(String name, float wattsPerHour){
		this.name = name;
		this.wattsPerHour = wattsPerHour;
	}
	
	public boolean inUse(){
		return inUse;
	}
	
	public void setInUse(boolean inUse){
		this.inUse = inUse;
	}
	
	public String getName(){
		return name;
	}
	
	public float getWattsPerHour(){
		return wattsPerHour;
	}
	
	public float getWattsPerMin(){
		return wattsPerHour / 60;
	}
	
	public float getWattsPerSec(){
		return getWattsPerMin() / 60;
	}
}
