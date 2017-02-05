package com.github.mob41.sakura.exception;

/**
 * It is thrown whenever the server encounter a error
 * @author Anthony
 *
 */
public class SakuraServerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SakuraServerException(){
		super();
	}
	
	public SakuraServerException(String message){
		super(message);
	}
	
	public SakuraServerException(String message, Throwable cause){
		super(message, cause);
	}
	
	public SakuraServerException(Throwable cause){
		super(cause);
	}
}
