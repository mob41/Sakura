package com.github.mob41.sakura.exception;

/**
 * It is thrown whenever the plugin description JSON was invalid.
 * @author Anthony
 *
 */
public class InvalidPluginDescription extends RuntimeException {

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
