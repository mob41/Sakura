package com.mob41.sakura.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * 
	 * @return Return whether the connection should be alive
	 */
	public boolean onClientConnectAPI(HttpServletRequest request, HttpServletResponse response) {
		return true;
	};
	
	/**
	 * This will be called whenever the API is disconnected from the client.<br>
	 * Override this method to function<br>
	 * <br>
	 * It is not allowed to kill connection after the API is disconnected.
	 */
	public boolean onClientDisconnectAPI(HttpServletRequest request, HttpServletResponse response) {
		return true;
	};
	
	/**
	 * This will be called whenever the client is trying to access plugins.<br>
	 * Override this method to function<br>
	 * <br>
	 * <b>Note:</b> Built-in plugins like <code>LoginAgentPlugin</code> is always the highest priority.
	 * 
	 * @return Return whether the connection should be alive
	 */
	public boolean onAccessPlugins(HttpServletRequest request, HttpServletResponse response) {
		return true;
	};
	
	/**
	 * This will be called whenever after the client accesses plugins.<br>
	 * Override this method to function<br>
	 * <br>
	 * It is not allowed to kill connection after accessing plugin.
	 */
	public void onAfterAccessPlugins(HttpServletRequest request, HttpServletResponse response) {};
}
