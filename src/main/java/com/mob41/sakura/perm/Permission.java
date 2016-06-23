package com.mob41.sakura.perm;

import com.mob41.sakura.plugin.loginagent.User;

public abstract class Permission {

	public String permName;
	
	public String permAction;
	
	public String permAlias;
	
	public String targetPlugin;
	
	protected final boolean isUserGained(User user){
		return false;
	}
}
