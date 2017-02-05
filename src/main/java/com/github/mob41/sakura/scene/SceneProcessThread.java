package com.github.mob41.sakura.scene;

import java.util.List;

import com.github.mob41.sakura.action.Action;

public class SceneProcessThread extends Thread {
	
	private final Scene s;
	
	private boolean running = false;
	
	private String runningActionName = "None";
	
	private int runningActionIndex = -1;
	
	protected SceneProcessThread(Scene s){
		this.s = s;
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
			
			List<String[]> allArgs = s.getActionArgs();
			Action[] actions = s.getActions();
			
			Action action;
			String[] args;
			
			runningActionName = "None";
			runningActionIndex = -1;
			
			for (int i = 0; i < actions.length; i++){
				if (!running){
					break;
				}
				
				action = actions[i];
				args = allArgs.get(i);
				
				if (action == null || args == null){
					continue;
				}
				
				runningActionName = action.getName();
				runningActionIndex = i;
				
				action.dynRun(args);
			}
			
			runningActionName = "None";
			runningActionIndex = -2;
			
			running = false;
		}
	}
	
	public Scene getSceneInstance(){
		return s;
	}
	
	public String getSceneName(){
		return s.getName();
	}
	
	public String getRunningActionName(){
		return runningActionName;
	}
	
	public int getRunningActionIndex(){
		return runningActionIndex;
	}
}
