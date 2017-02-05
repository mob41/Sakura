package com.github.mob41.sakura.action.sys;

import java.io.IOException;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.action.ParameterType;
import com.github.mob41.sakura.api.SakuraServer;

public class HaltSystem extends Action {
	
	private static final ParameterType[] types = new ParameterType[0];

	public HaltSystem(SakuraServer srv) {
		super(srv, "Halt OS");
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
				r.exec("shutdown -s -t 0");
			} else if (os.contains("Ubuntu") || os.contains("Debian") ||
					os.contains("Unix") || os.contains("Mac")){
				r.exec("halt");
			} else {
				return new ActionResponse(ActionResponse.STATUS_FAILED, "Unable to determine operating system. Please set a custom command.");
			}
			return new ActionResponse(ActionResponse.STATUS_SUCCESS, "Command ran.");
		} catch (IOException e){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Error occurred while shutting down: " + e);
		}
	}

}
