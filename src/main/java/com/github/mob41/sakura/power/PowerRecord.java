package com.github.mob41.sakura.power;

import java.util.Calendar;

public class PowerRecord {

	private final Calendar recTime;
	
	private final int targetHour;
	
	private final PowerUse[] powerUses;
	
	public PowerRecord(PowerUse[] powerUses){
		recTime = Calendar.getInstance();
		targetHour = recTime.get(Calendar.HOUR_OF_DAY);
		this.powerUses = powerUses;
	}
	
	public Calendar getRecTime(){
		return recTime;
	}
	
	public int getTargetHour(){
		return targetHour;
	}
	
	public float[] getPowerUseRatioPercentages(){
		float[] arr = new float[powerUses.length];
		float totalwatts = 0;
		for (PowerUse use : powerUses){
			totalwatts += use.getWattsPerHour();
		}
		for (int i = 0; i < powerUses.length; i++){
			arr[i] = powerUses[i].getWattsPerHour() / totalwatts * 100; 
		}
		return arr;
	}
	
	public String[] getPowerUseNames(){
		String[] arr = new String[powerUses.length];
		for (int i = 0; i < arr.length; i++){
			arr[i] = powerUses[i].getName();
		}
		return arr;
	}
	
	public String getFormattedTime(){
		int year = recTime.get(Calendar.YEAR);
		int month = recTime.get(Calendar.MONTH);
		int day = recTime.get(Calendar.DAY_OF_MONTH);
		String monthstr = month < 10 ? "0" + month : Integer.toString(month);
		String daystr = day < 10 ? "0" + day : Integer.toString(day);
		return year + "-" + monthstr + "-" + daystr + " " + targetHour + ":00";
	}
	
	public float getPowerUseWattsPerHour(String powerUseName){
		for (int i = 0; i < powerUses.length; i++){
			if (powerUses[i].getName().equals(powerUseName)){
				return powerUses[i].getWattsPerHour();
			}
		}
		return 0;
	}
	
	public PowerUse[] getPowerUses(){
		return powerUses;
	}
}
