package com.github.mob41.sakura.security;

import org.json.JSONArray;
import org.json.JSONObject;

public class PermUser {

	private final String username;
	
	private final PermGroup[] assocGroups;
	
	private final PermNode[] selfNodes;
	
	public PermUser(JSONObject perms, String username){
		if (perms.getJSONObject("users").isNull(username)){
			this.username = null;
			this.assocGroups = null;
			this.selfNodes = null;
			return;
		}
		this.username = username;
		
		JSONObject userJson = perms.getJSONObject("users").getJSONObject(username);
		JSONArray userGroups = userJson.getJSONArray("groups");
		
		assocGroups = new PermGroup[userGroups.length()];
		for (int i = 0; i < assocGroups.length; i++){
			assocGroups[i] = new PermGroup(perms, userGroups.getString(i));
		}
		
		JSONArray userNodes = userJson.getJSONArray("perms");
		selfNodes = new PermNode[userNodes.length()];
		
		for (int i = 0; i < selfNodes.length; i++){
			selfNodes[i] = new PermNode(userNodes.getString(i));
		}
	}
	
	public PermUser(String username, PermGroup[] assocGroups, PermNode[] selfNodes){
		this.username = username;
		this.selfNodes = selfNodes;
		this.assocGroups = assocGroups;
	}
	
	public final String getUsername(){
		return username;
	}
	
	public PermGroup[] getAssociatedGroups(){
		return assocGroups;
	}
	
	public PermNode[] getSelfPermissions(){
		return selfNodes;
	}
	
	public boolean isPermitted(String permNodeAlias){
		return isPermitted(new PermNode(permNodeAlias));
	}
	
	public boolean isPermitted(PermNode permNode){
		if (permNode == null){
			return false;
		}
		
		boolean gpPer = false;
		for (int i = 0; i < assocGroups.length; i++){
			gpPer = assocGroups[i].isPermitted(permNode);
		}
		
		boolean selfPer = isSelfPermitted(permNode);
		
		return selfPer || gpPer;
	}
	
	public boolean isSelfNodeExist(PermNode permNode){
		for (int i = 0; i < selfNodes.length; i++){
			if (permNode.equals(selfNodes[i])){
				return true;
			}
		}
		return false;
	}
	
	public boolean isSelfPermitted(PermNode permNode){
		if (permNode == null){
			return false;
		}
		
		for (int i = 0; i < selfNodes.length; i++){
			if (selfNodes[i].equals(permNode)){
				return true;
			}
		}
		return false;
	}
}
