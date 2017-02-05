package com.github.mob41.sakura.action.sys;

import java.io.IOException;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.action.ParameterType;
import com.github.mob41.sakura.api.SakuraServer;

public class RestartSystem extends Action {
	
	private static final ParameterType[] types = new ParameterType[0];

	public RestartSystem(SakuraServer srv) {
		super(srv, "Restart OS");
	}

	@Override
	public ParameterType[] getParameterTypes() {
		return types;
	}

	@Override
	public ActionResponse run(Object[] args) {
		String os = System.getProperty("os.name");
		Runtime r = Runtime.getRuntime();
		try {
			if (os.contains("Windows")){
				r.exec("shutdown -r -t 0");
			} else if (os.contains("Ubuntu") || os.contains("Debian") ||
					os.contains("Unix") || os.equals("Raspbian") ||
					os.contains("Linux") || os.contains("Mac")){
				r.exec("reboot");
			} else {
				//TODO Custom command
				return new ActionResponse(ActionResponse.STATUS_FAILED, "Unable to determine operating system. Please set a custom command.");
			}
			return new ActionResponse(ActionResponse.STATUS_SUCCESS, "Command ran.");
		} catch (IOException e){
			e.printStackTrace();
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Error occurred while shutting down: " + e);
		}
	}

}
