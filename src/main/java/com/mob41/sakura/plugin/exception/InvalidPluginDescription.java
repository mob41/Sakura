package com.mob41.sakura.plugin.exception;

/**
 * It is thrown whenever the plugin description JSON was invalid.
 * @author Anthony
 *
 */
public class InvalidPluginDescription extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPluginDescription(){
		super();
	}
	
	public InvalidPluginDescription(String message){
		super(message);
	}
	
	public InvalidPluginDescription(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidPluginDescription(Throwable cause){
		super(cause);
	}
}
