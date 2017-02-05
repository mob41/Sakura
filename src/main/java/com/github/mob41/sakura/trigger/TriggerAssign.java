package com.github.mob41.sakura.trigger;

public class TriggerAssign {
	
	private final String triggerName;

	private final String sceneName;
	
	private Object[] parameters;
	
	public TriggerAssign(String triggerName, String sceneName) {
		this(triggerName, sceneName, new Object[0]);
	}
	
	public TriggerAssign(String triggerName, String sceneName, Object[] parameters) {
		this.triggerName = triggerName;
		this.sceneName = sceneName;
		this.parameters = parameters;
	}
	
	public void setParameters(Object[] parameters){
		this.parameters = parameters;
	}
	
	public Object[] getParameters(){
		return parameters;
	}
	
	public String getTriggerName(){
		return triggerName;
	}
	
	public String getSceneName(){
		return sceneName;
	}

}
