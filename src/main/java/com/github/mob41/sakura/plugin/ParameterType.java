package com.github.mob41.sakura.plugin;

public class ParameterType {

	public static final int INTEGER = 0;
	
	public static final int STRING = 1;
	
	private final String name;
	
	private final int classType;
	
	public ParameterType(String name, int classType){
		this.name = name;
		this.classType = classType;
	}
	
	public String getName(){
		return name;
	}
	
	public int getClassType(){
		return classType;
	}
}