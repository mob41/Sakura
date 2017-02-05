package com.github.mob41.sakura.exception;

/**
 * It is thrown if the requested plugin does not exist or loaded
 * @author Anthony
 *
 */
public class NoSuchPluginException extends RuntimeException {

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
