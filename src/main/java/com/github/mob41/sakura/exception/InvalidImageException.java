package com.github.mob41.sakura.exception;

/**
 * It is thrown if the image is invalid
 * @author Anthony
 *
 */
public class InvalidImageException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidImageException(){
		super();
	}
	
	public InvalidImageException(String message){
		super(message);
	}
	
	public InvalidImageException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidImageException(Throwable cause){
		super(cause);
	}
}
