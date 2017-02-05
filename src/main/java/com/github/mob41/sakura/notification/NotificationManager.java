package com.github.mob41.sakura.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.security.User;

public class NotificationManager {
	
	Map<String, List<Notification>> nts;

	public NotificationManager(SakuraServer srv){
		nts = new HashMap<String, List<Notification>>(100);
	}
	
	public List<Notification> getNotifications(User user){
		return getNotifications(user.getUsername());
	}
	
	public List<Notification> getNotifications(String username){
		if (!nts.containsKey(username)){
			return null;
		}
		return nts.get(username);
	}
	
	public void addNotification(String username, Notification nt){
		List<Notification> ntl = null;
		if (!nts.containsKey(username)){
			ntl = new ArrayList<Notification>(100);
		} else {
			ntl = nts.get(username);
		}
		ntl.add(nt);
		nts.put(username, ntl);
	}
	
	public void readNotification(User user, String uid){
		int index = getUIDIndex(user.getUsername(), uid);
		
		if (index == -1){
			return;
		}
		
		nts.get(user.getUsername()).get(index).setAsRead();
	}
	
	public void removeNotification(String username, int index){
		if (!nts.containsKey(username)){
			return;
		}
		
		nts.get(username).remove(index);
	}
	
	public void removeNotification(Notification nt){
		
	}
	
	public int getUIDIndex(String username, String uid){
		List<Notification> list = nts.get(username);
		for (int i = 0; i < list.size(); i++){
			if (list.get(i).getUID().equals(uid)){
				return i;
			}
		}
		return -1;
	}
	
}
