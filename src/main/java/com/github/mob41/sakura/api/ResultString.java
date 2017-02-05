package com.github.mob41.sakura.api;

import java.io.Serializable;

public class ResultString implements Serializable, Comparable<String>, CharSequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4743742519348946246L;

	private final int result;
	
	private final String str;
	
	public ResultString(int result, String str) {
		this.result = result;
		this.str = str;
	}
	
	public int getResult(){
		return result;
	}
	
	public String getString(){
		return str;
	}
	
	@Override
	public String toString(){
		return str;
	}

	@Override
	public char charAt(int arg0) {
		return str.charAt(arg0);
	}

	@Override
	public int length() {
		return str.length();
	}

	@Override
	public CharSequence subSequence(int arg0, int arg1) {
		return str.subSequence(arg0, arg1);
	}

	@Override
	public int compareTo(String arg0) {
		return str.compareTo(arg0);
	}

}
