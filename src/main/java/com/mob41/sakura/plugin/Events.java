package com.mob41.sakura.plugin;

/**
 * <code>Events</code> class to control events in plugins<br>
 * <br>
 * Better to use <code>Plugin</code> class directly.
 * @author Anthony
 *
 */
public abstract class Events {

	/**
	 * This will be called whenever the API is connected to a client.<br>
	 * Override this method to function
	 */
	public void onClientConnectAPI() {};
	
	/**
	 * This will be called whenever the API is disconnected from the client.<br>
	 * Override this method to function
	 */
	public void onClientDisconnectAPI() {};
	
	/**
	 * This will be called whenever the client is trying to access plugins.<br>
	 * Override this method to function<br>
	 * <br>
	 * <b>Note:</b> Built-in plugins like <code>LoginAgentPlugin</code> is always the highest priority.
	 */
	public void onAccessPlugins() {};
	
	/**
	 * This will be called whenever after the client accesses plugins.<br>
	 * Override this method to function
	 */
	public void onAfterAccessPlugins() {};
}
