package com.github.mob41.sakura.plugin;

public class PluginParameter {

	private final String name;
	
	private Object value;
	
	public PluginParameter(String name){
		this(name, null);
	}
	
	public PluginParameter(String name, Object value){
		this.name = name;
		this.value = value;
	}
	
	public final String getName(){
		return name;
	}
	
	public final void setValue(Object value){
		this.value = value;
	}
	
	public final Object getValue(){
		return value;
	}
}
