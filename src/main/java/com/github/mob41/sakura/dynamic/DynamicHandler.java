package com.github.mob41.sakura.dynamic;

public interface DynamicHandler {
	
	/**
	 * Returns whether this handler will handle the specified variable name
	 * @param variableName The variable name to be checked
	 * @return The result in Boolean
	 */
	public boolean isHandled(String variableName);

	/**
	 * Returns the (dynamic) result with the variable name specified
	 * @return The result in Object (unknown)
	 */
	public Object handle(String variableName);
	
}
