package com.github.mob41.sakura.trigger;

public class ParameterType {

	public static final int INTEGER = 0;
	
	public static final int STRING = 1;
	 
	public static final int SELECTIONS = 2;
	
	private final String name;
	
	private String[] selections = null;
	
	private final int classType;
	
	public ParameterType(String name, String[] selections){
		this.name = name;
		this.selections = selections;
		this.classType = SELECTIONS;
	}
	
	public ParameterType(String name, int classType){
		this.name = name;
		this.classType = classType;
	}
	
	public String getName(){
		return name;
	}
	
	public String[] getSelections(){
		return selections;
	}
	
	public int getClassType(){
		return classType;
	}
}