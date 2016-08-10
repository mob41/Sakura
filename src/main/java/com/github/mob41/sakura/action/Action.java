package com.github.mob41.sakura.action;

public abstract class Action implements ActionBase{
	
	private String _actionName;
	
	public Action(String actionName){
		this._actionName = actionName;
	}
	
	public final String getName(){
		return _actionName;
	}
	
}
