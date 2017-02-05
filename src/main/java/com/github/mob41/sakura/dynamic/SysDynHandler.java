package com.github.mob41.sakura.dynamic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SysDynHandler implements DynamicHandler {
	
	private final List<String> isHandles;

	public SysDynHandler() {
		isHandles = new ArrayList<String>(50);
		
		isHandles.add("current-time");
	}

	@Override
	public boolean isHandled(String variableName) {
		return isHandles.contains(variableName);
	}

	@Override
	public Object handle(String variableName) {
		if (!isHandled(variableName) || variableName == null){
			return null;
		}
		
		if (variableName.equals("current-time")){
			return Calendar.getInstance().getTime().toString();
		} else {
			return null;
		}
	}

}
