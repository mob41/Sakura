package com.github.mob41.sakura.plugin;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.api.SakuraServer;

public abstract class PluginAction extends Action {

	private final String pluginName;
	
	private final SakuraServer srv;
	
	public PluginAction(PluginDescription desc, SakuraServer srv, String actionName) {
		super(srv, actionName);
		this.srv = srv;
		this.pluginName = desc.getName();
	}
	
	public String getPluginName(){
		return pluginName;
	}

}
