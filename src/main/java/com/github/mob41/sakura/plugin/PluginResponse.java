package com.github.mob41.sakura.plugin;

public class PluginResponse {
	
	public static final int STATUS_NOT_IMPLEMENTED = -3;
	
	public static final int STATUS_PERMISSION_DENIED = -2;
	
	public static final int STATUS_FAILED = -1;
	
	public static final int STATUS_NO_RESPONSE = 0;
	
	public static final int STATUS_SUCCESS = 1;
	
	public static final int STATUS_CREATED = 2;
	
	public static final int STATUS_ACCEPTED = 3;
	
	private final int status;
	
	private final String message;
	
	public PluginResponse(){
		this(STATUS_NO_RESPONSE, null);
	}
	
	public PluginResponse(int status){
		this(status, null);
	}
	
	public PluginResponse(int status, String message){
		this.status = status;
		this.message = message;
	}
	
	public int getStatus(){
		return status;
	}
	
	public String getStatusInString(){
		String str = "Unknown";
		switch (status){
		case STATUS_ACCEPTED:
			str = "Accepted";
			break;
		case STATUS_CREATED:
			str = "Created";
			break;
		case STATUS_FAILED:
			str = "Failed";
			break;
		case STATUS_NO_RESPONSE:
			str = "No response";
			break;
		case STATUS_NOT_IMPLEMENTED:
			str = "Not implemented";
			break;
		case STATUS_PERMISSION_DENIED:
			str = "Permission denied";
			break;
		case STATUS_SUCCESS:
			str = "Success";
			break;
		}
		return str;
	}
	
	public String getMessage(){
		return message;
	}
	
	public static PluginResponse getDefaultResponse(){
		return new PluginResponse();
	}
	
	public static PluginResponse getNotImplementedResponse(){
		return new PluginResponse(STATUS_NO_RESPONSE);
	}
}
