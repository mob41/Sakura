package com.github.mob41.sakura.notification;

import java.util.ArrayList;
import java.util.List;

import com.github.mob41.sakura.api.SakuraServer;

public class AlertManager {
	
	List<Alert> als;
	
	public AlertManager(SakuraServer srv){
		als = new ArrayList<Alert>(100);
	}
	
	public void addAlert(Alert alert){
		als.add(alert);
	}
	
	public void removeAlert(int index){
		removeAlert(index, false);
	}
	
	public void removeAlert(int index, boolean reversed){
		if (reversed){
			Alert al = getReversedAlerts().get(index);
			als.remove(al);
		} else {
			als.remove(index);
		}
	}
	
	public List<Alert> getAlerts(){
		return getAlerts(false);
	}
	
	public List<Alert> getAlerts(boolean reversed){
		return getAlerts(Alert.GLOBAL_PERM, reversed);
	}
	
	public List<Alert> getAlerts(String perm, boolean reversed){
		if (reversed){
			return getReversedAlerts();
		}
		List<Alert> alerts = new ArrayList<Alert>(100);
		for (Alert alert : als){
			if (alert.getPermission().equals(perm)){
				alerts.add(alert);
			}
		}
		return alerts;
	}
	
	public List<Alert> getReversedAlerts(){
		List<Alert> revs = new ArrayList<Alert>(als.size());
		for (int i = als.size() - 1; i >= 0; i--){
			revs.add(als.get(i));
		}
		return revs;
	}
}
