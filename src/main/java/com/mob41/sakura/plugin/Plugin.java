package com.mob41.sakura.plugin;

/**
 * An API to port plugins into Sakura system
 * @author Anthony
 *
 */
public abstract class Plugin extends Events{
	

	/**
	 * The Unique ID of this plugin
	 */
	public String pluginUid;
	
	/**
	 * The Name of this plugin
	 */
	public PluginDescription pluginDesc;
	
	/**
	 * It is called when this plugin is called by the API
	 */
	public abstract void onCallPlugin();
	
	/**
	 * It is called when the API send data to the plugin
	 * @param data Data from the API (Can be <code>null</code> or <code>JSONObject</code>)
	 */
	public abstract void onPluginReceiveData(Object data);
	
	/**
	 * It is called when the API request the plugin to send data
	 * @return Data to be sent to the API (Can be <code>null</code> or <code>JSONObject</code>)
	 */
	public abstract Object onPluginSendData();
	
	/**
	 * It is called when this plugin is being ended.<br>
	 */
	public abstract void onEndPlugin();
	
	
}
