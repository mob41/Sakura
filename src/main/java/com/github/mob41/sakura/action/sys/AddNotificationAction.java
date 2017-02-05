package com.github.mob41.sakura.action.sys;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.action.ParameterType;
import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.notification.Notification;
import com.github.mob41.sakura.notification.NotificationManager;

public class AddNotificationAction extends Action{
	
	private static final ParameterType[] types = {
		new ParameterType("Target user", ParameterType.STRING),
		new ParameterType("Title", ParameterType.STRING),
		new ParameterType("Message", ParameterType.STRING)
	};

	public AddNotificationAction(SakuraServer srv) {
		super(srv, "Add notification");
	}

	@Override
	public ParameterType[] getParameterTypes() {
		return types;
	}

	@Override
	public ActionResponse run(Object[] args) {
		if (args.length <= 3 || !(args[0] instanceof String) || !(args[1] instanceof String) || !(args[2] instanceof String)){
			return new ActionResponse(ActionResponse.STATUS_FAILED, "Invalid arguments");
		}
		
		getServer().getNotificationManager().addNotification((String) args[0], new Notification((String) args[1], (String) args[2]));
		return null;
	}

}
