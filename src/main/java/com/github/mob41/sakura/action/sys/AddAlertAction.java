package com.github.mob41.sakura.action.sys;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.action.ParameterType;
import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.notification.Alert;
import com.github.mob41.sakura.notification.AlertManager;

public class AddAlertAction extends Action{
	
	private static final ParameterType[] types = {
		new ParameterType("Level", new String[]{"Notice", "Warning", "Error"}),
		new ParameterType("Permission level", new String[]{"Global", "User and Admin only", "Admin only"}),
		new ParameterType("Message", ParameterType.STRING)
	};

	public AddAlertAction(SakuraServer srv) {
		super(srv, "Add alert");
	}

	@Override
	public ParameterType[] getParameterTypes() {
		return types;
	}

	@Override
	public ActionResponse run(Object[] args) {
		if (args.length < 3 || args.length > 3 || !(args[0] instanceof String) || !(args[1] instanceof String) || !(args[2] instanceof String)){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Invalid arguments");
		}
		String levelstr = (String) args[0];
		String permstr = (String) args[1];
		String msg = (String) args[2];
		
		int level = 0;
		if (levelstr.equals("Notice")){
			level = Alert.NOTICE;
		} else if (levelstr.equals("Warning")){
			level = Alert.WARNING;
		} else if (levelstr.equals("Error")){
			level = Alert.ERROR;
		}
		
		String perm = Alert.ADMIN_PERM;
		if (permstr.equals("Global")){
			perm = Alert.GLOBAL_PERM;
		} else if (permstr.equals("User and Admin only")){
			perm = Alert.USER_PERM;
		} else if (permstr.equals("Admin only")){
			perm = Alert.ADMIN_PERM;
		}
		System.out.println("Adding: " + level + " msg : " + msg + " perm: " + perm);
		getServer().getAlertManager().addAlert(new Alert(level, msg, perm));
		return null;
	}

}
