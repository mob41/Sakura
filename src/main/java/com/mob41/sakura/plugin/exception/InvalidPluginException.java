package com.mob41.sakura.plugin.exception;

/**
 * It is thrown whenever the plugin description JSON was invalid.
 * @author Anthony
 *
 */
public class InvalidPluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPluginException(){
		super();
	}
	
	public InvalidPluginException(String message){
		super(message);
	}
	
	public InvalidPluginException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidPluginException(Throwable cause){
		super(cause);
	}
}
