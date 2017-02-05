package com.github.mob41.sakura.power;

import java.util.ArrayList;
import java.util.List;

import com.github.mob41.sakura.api.SakuraServer;

//TODO Save power records using JSON
public class PowerManager {

	public static final String MANUAL_POWER_USE_FILE_NAME = "power_manual.json";
	
	public static final String RECORDS_POWER_USE_FILE_NAME = "power_records.json";
	
	private final List<PowerUse> powerUses;
	
	private final List<PowerRecord> powerRecords;
	
	private final PowerRecordingThread prThread;
	
	public PowerManager(SakuraServer srv){
		powerUses = new ArrayList<PowerUse>(100);
		powerRecords = new ArrayList<PowerRecord>(24);
		prThread = new PowerRecordingThread(this);
		prThread.start();
	}
	
	public List<PowerRecord> getPowerRecords(){
		return powerRecords;
	}
	
	public String[] getTotalPowerRecordsNames(){
		List<PowerRecord> list = getPowerRecords();
		List<String> strs = new ArrayList<String>(100);
		PowerUse[] uses;
		boolean exist = false;
		for (PowerRecord record : list){
			uses = record.getPowerUses();
			for (PowerUse use : uses){
				exist = false;
				for (String str : strs){
					if (use.getName().equals(str)){
						exist = true;
						break;
					}
				}
				if (!exist){
					strs.add(use.getName());
				}
			}
		}
		
		String[] arr = new String[strs.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = strs.get(i);
		}
		
		return arr;
	}
	
	protected void appendRecord(PowerRecord record){
		if (powerRecords.size() >= 24){
			powerRecords.remove(0);
		}
		
		powerRecords.add(record);
	}
	
	public void registerPowerUse(PowerUse powerUse){
		registerPowerUse(powerUse, false);
	}
	
	public void registerPowerUse(PowerUse powerUse, boolean inUse){
		powerUses.add(powerUse);
		powerUse.setInUse(inUse);
	}
	
	public void setInUse(String powerUseName, boolean inUse){
		int index = getIndex(powerUseName);
		
		if (index != -1){
			powerUses.get(index).setInUse(inUse);
		}
	}
	
	public boolean isPowerUseRegistered(String powerUseName){
		return getIndex(powerUseName) != -1;
	}
	
	public int getIndex(String powerUseName){
		for (int i = 0; i < powerUses.size(); i++){
			if (powerUses.get(i).getName().equals(powerUseName)){
				return i;
			}
		}
		return -1;
	}
	
	public float getCurrentPowerUseKiloWatts(){
		float watts = 0;
		PowerUse[] arr = getInUses();
		for (PowerUse obj : arr){
			watts += obj.getWattsPerHour();
		}
		return watts / 1000;
	}
	
	public PowerUse[] getInUses(){
		List<PowerUse> list = new ArrayList<PowerUse>(100);
		for (PowerUse powerUse : powerUses){
			if (powerUse.inUse()){
				list.add(powerUse);
			}
		}
		
		PowerUse[] arr = new PowerUse[list.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = list.get(i);
		}
		
		return arr;
	}
	
	public PowerUse[] getNotInUses(){
		List<PowerUse> list = new ArrayList<PowerUse>(100);
		for (PowerUse powerUse : powerUses){
			if (!powerUse.inUse()){
				list.add(powerUse);
			}
		}
		
		PowerUse[] arr = new PowerUse[list.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = list.get(i);
		}
		
		return arr;
	}
	
}
