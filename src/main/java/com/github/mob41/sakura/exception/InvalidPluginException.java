package com.github.mob41.sakura.exception;

/**
 * It is thrown when the plugin was invalid.
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
