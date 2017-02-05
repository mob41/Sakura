package com.github.mob41.sakura.trigger;

public abstract class Trigger implements TriggerBase{

	private final String name;
	
	public Trigger(String name){
		this.name = name;
	}
	
	public final String getName(){
		return name;
	}
}
