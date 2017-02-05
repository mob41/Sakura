package com.github.mob41.sakura.power;

import java.util.Calendar;

public class PowerRecordingThread extends Thread{

	private final PowerManager mgr;
	
	private boolean running = false;
	
	public PowerRecordingThread(PowerManager mgr){
		this.mgr = mgr;
	}
	
	public void shutdown(){
		if (running){
			running = false;
		}
	}
	
	@Override
	public void run(){
		if (!running){
			running = true;
			Calendar cal;
			int nowHour = -1;
			int afterHour = -1;
			while (running){
				cal = Calendar.getInstance();
				nowHour = cal.get(Calendar.HOUR_OF_DAY);
				if (afterHour == -1){
					afterHour = nowHour + 1 == 24 ? 0 : nowHour + 1;
				}
				
				if (mgr.getPowerRecords().size() == 0){
					record();
				} else if (nowHour == afterHour){
					afterHour = -1;
					record();
				} else {
					record(mgr.getPowerRecords().size() - 1);
				}
			}
			running = false;
		}
	}
	
	public void record(){
		record(-1);
	}
	
	private void record(int index){
		PowerRecord record = new PowerRecord(mgr.getInUses());
		
		if (index == -1){
			mgr.appendRecord(record);
		} else {
			mgr.getPowerRecords().set(index, record);
		}
	}
}
