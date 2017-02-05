package com.github.mob41.sakura.api;

import java.util.ArrayList;
import java.util.List;

public class APIEncKeyThread extends Thread {
	
	public static final int DEFAULT_TIMEOUT = 300000;

	public APIEncKeyThread() {
		sess = new ArrayList<APISession>(100);
	}
	
	private boolean running = false;
	
	private final List<APISession> sess;
	
	public boolean isRunning(){
		return running;
	}
	
	public void shutdown(){
		if (running){
			running = false;
			interrupt();
		}
	}
	
	public APISession getSession(String uid){
		int index = getIndex(uid);
		return index != -1 ? sess.get(index) : null;
	}
	
	public int getIndex(String uid){
		for (int i = 0; i < sess.size(); i++){
			if (sess.get(i).getUid().equals(uid)){
				return i;
			}
		}
		return -1;
	}
	
	public APISession registerSession(){
		return registerSession(DEFAULT_TIMEOUT);
	}
	
	public APISession registerSession(int timeoutMs){
		APISession ses = new APISession(timeoutMs);
		sess.add(ses);
		
		if (!isRunning()){
			start();
		}
		return ses;
	}
	
	public void unregisterSession(int index){
		sess.remove(index);
	}
	
	public void clearAll(){
		sess.clear();
	}
	
	@Override
	public void run(){
		if (!running){
			running = true;
			sess.clear();
			APISession ses;
			while (running){
				for (int i = 0; i < sess.size(); i++){
					ses = sess.get(i);
					if (ses.isTimedOut()){
						sess.remove(i);
					}
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
			running = false;
		}
	}
	
}
