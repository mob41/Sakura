package com.github.mob41.sakura.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicManager {
	
	public static final String DYNAMIC_START = "#@=";
	
	public static final String DYNAMIC_END = "=@#";
	
	public static final String SYS_VAR = "sys:";
	
	private final Map<String, Object> userVariables;
	
	private final List<DynamicHandler> handlers;

	public DynamicManager() {
		userVariables = new ConcurrentHashMap<String, Object>();
		handlers = new ArrayList<DynamicHandler>();
		
		addHandler(new SysDynHandler());
	}
	
	public void setUserVariable(String name, Object value){
		userVariables.put(name, value);
	}
	
	public Object getUserVariable(String name){
		return userVariables.get(name);
	}
	
	public void addHandler(DynamicHandler handler){
		handlers.add(handler);
	}
	
	public void removeHandler(DynamicHandler handler){
		handlers.remove(handler);
	}
	
	public String processDynamicString(String str){
		if (str == null){
			return null;
		}
		
		String newStr = new String(str);
		
		int startIndex = -1;
		int endIndex = -1;
		
		String strToReplace = null;
		String strToCheck = null;
		String strVarName = null;
		
		String strResult = null;
		Object objResult = null;
		
		System.out.println(" ========= Start-Loop =========");
		for (int i = 0; i < str.length(); i++){
			startIndex = str.indexOf(DYNAMIC_START, i);
			System.out.println("Dynamic Start Index: " + startIndex + " / " + i + " / " + str.length());
			
			if (startIndex == -1){
				System.out.println("No start index of dynamic: " +startIndex + " / " + i + " / " + str.length());
				System.out.println("Returns newStr: " + newStr);
				return newStr;
			} else {
				endIndex = str.indexOf(DYNAMIC_END, startIndex + DYNAMIC_START.length());
				System.out.println("Found start dynamic. Finding endIndex: " + endIndex);
				
				if (endIndex == -1){
					System.out.println("No end index of dynamic: " +endIndex + " / " + i + " / " + str.length());
					System.out.println("Returns newStr: " + newStr);
					return newStr;
				}
				
				strToReplace = str.substring(startIndex, endIndex + DYNAMIC_END.length());
				strToCheck = str.substring(startIndex + DYNAMIC_START.length(), endIndex);
				System.out.println("StrToReplace: " + strToReplace);
				System.out.println("StrToCheck: " + strToCheck);
				
				boolean sysVar = strToCheck.startsWith(SYS_VAR);
				System.out.println("isSysVar: " + sysVar);
				if (sysVar){
					strVarName = strToCheck.substring(SYS_VAR.length());
				} else {
					strVarName = strToCheck;
				}
				System.out.println("StrVarName: " + strVarName);
				
				objResult = getVariable(sysVar, strVarName);
				System.out.println("objResult: " + objResult);
				
				if (objResult != null){
					if (objResult instanceof String){
						strResult = (String) objResult;
					} else {
						strResult = objResult.toString();
					}
				} else {
					strResult = "null";
				}
				
				System.out.println("strResult: " + strResult);
				
				System.out.println("OldNewStr: " + newStr);
				newStr = newStr.replaceAll(strToReplace, strResult);
				System.out.println("NewNewStr: " + newStr);
				
				System.out.println("OldI: " + i);
				i = endIndex + 1;
				System.out.println("NewI: " + i);
			}
		}
		
		System.out.println("newStr: " + newStr);
		return newStr;
	}
	
	/**
	 * Returns a (dynamic) variable value, or <code>null</code> if not handled or no such variable<br>
	 * <br>
	 * This will perform a search to those registered <code>DynamicHandler</code>, the earlier it registered,
	 * the first to handle.<br>
	 * <br>
	 * If the registered handlers did not handle the variable name provided, it will search for user variables only.<br>
	 * <br>
	 * Returns <code>null</code> if nothing found.
	 * @param variableName The variable name to be searched
	 * @return The variable value, might be dynamic, returns <code>null</code> if nothing found
	 */
	public Object getVariable(String variableName){
		return getVariable(false, variableName);
	}
	
	/**
	 * Returns a (dynamic) variable value, or <code>null</code> if not handled or no such variable<br>
	 * <br>
	 * This will perform a search to those registered <code>DynamicHandler</code>, the earlier it registered,
	 * the first to handle.<br>
	 * <br>
	 * If the registered handlers did not handle the variable name provided, it will search for system variables if
	 * <code>system</code> is <code>true</code> otherwise the user variables.<br>
	 * <br>
	 * Returns <code>null</code> if nothing found.
	 * @param variableName The variable name to be searched
	 * @param system Whether system variables should be searched instead of user variables
	 * @return The variable value, might be dynamic, returns <code>null</code> if nothing found
	 */
	public Object getVariable(boolean system, String variableName){
		List<DynamicHandler> copiedList = new ArrayList<DynamicHandler>(handlers);
		
		for (DynamicHandler handler : copiedList){
			if (handler.isHandled(variableName)){
				return handler.handle(variableName);
			}
		}
		
		if (system){
			return System.getProperty(variableName);
		} else if (userVariables.containsKey(variableName)){
			return userVariables.get(variableName);
		}
		
		return null;
	}

}
