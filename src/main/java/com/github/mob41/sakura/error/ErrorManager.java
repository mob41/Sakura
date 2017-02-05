package com.github.mob41.sakura.error;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.notification.Alert;
import com.github.mob41.sakura.notification.AlertManager;

public class ErrorManager {
	
	private final SakuraServer srv;
	
	public ErrorManager(SakuraServer srv) {
		this.srv = srv;
	}
	
	public void report(String message){
		srv.getAlertManager().addAlert(new Alert(Alert.ERROR, message));
	}

}
