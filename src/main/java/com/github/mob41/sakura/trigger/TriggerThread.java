package com.github.mob41.sakura.trigger;

import java.util.Iterator;
import java.util.Map;

import com.github.mob41.sakura.scene.SceneManager;

public class TriggerThread extends Thread {

	private static final int DEFAULT_DELAY = 1000;
	
	private final TriggerManager tm;
	
	private boolean running = false;
	
	private int delay = DEFAULT_DELAY;
	
	public TriggerThread(TriggerManager tm) {
		this.tm = tm;
	}
	
	public boolean isRunning(){
		return running;
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
			SceneManager sm = tm.getServer().getSceneManager();
			Map<String, TriggerAssign> runMap;
			String assignedTriggerName;
			String key;
			Trigger tr;
			TriggerAssign tra;
			while (running){
				runMap = tm.getRunningTriggers();
				Iterator<String> it = runMap.keySet().iterator();
				while (it.hasNext()){
					key = it.next();
					tra = runMap.get(key);
					assignedTriggerName = tra.getTriggerName();
					tr = assignedTriggerName != null ? tm.getRegisteredTrigger(assignedTriggerName) : null;
					
					if (tr != null){
						if (tr != null && tr.isTriggered(tra.getParameters())){
							sm.runScene(runMap.get(key).getSceneName());
						}
					}
				}
				try {
					sleep(delay);
				} catch (InterruptedException e) {
					break;
				}
			}
			running = false;
		}
	}

}
