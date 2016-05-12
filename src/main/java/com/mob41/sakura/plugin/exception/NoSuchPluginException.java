package com.mob41.sakura.plugin.exception;

/**
 * It is thrown whenever the plugin description JSON was invalid.
 * @author Anthony
 *
 */
public class NoSuchPluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchPluginException(){
		super();
	}
	
	public NoSuchPluginException(String message){
		super(message);
	}
	
	public NoSuchPluginException(String message, Throwable cause){
		super(message, cause);
	}
	
	public NoSuchPluginException(Throwable cause){
		super(cause);
	}
}
