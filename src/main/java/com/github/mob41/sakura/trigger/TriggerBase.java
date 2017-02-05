package com.github.mob41.sakura.trigger;

public interface TriggerBase {

	public boolean isTriggered(Object[] args);
	
	public ParameterType[] getParameterTypes();
	
}
