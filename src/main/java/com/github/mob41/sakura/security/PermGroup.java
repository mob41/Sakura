package com.github.mob41.sakura.security;

import org.json.JSONArray;
import org.json.JSONObject;

public class PermGroup {

	private final PermNode[] permNodes;
	
	private final String groupName;
	
	public PermGroup(JSONObject perms, String groupName){
		if (groupName == null || groupName.isEmpty() || perms == null || perms.getJSONObject("groups").isNull(groupName)){
			this.permNodes = null;
			this.groupName = null;
			return;
		}
		
		JSONObject gpJson = perms.getJSONObject("groups").getJSONObject(groupName);
		
		this.groupName = groupName;
		
		JSONArray nodes = gpJson.getJSONArray("nodes");
		permNodes = new PermNode[nodes.length()];
		for (int i = 0; i < nodes.length(); i++){
			permNodes[i] = new PermNode(nodes.getString(i));
		}
	}
	
	public PermGroup(String groupName, PermNode[] permNodes){
		this.groupName = groupName;
		this.permNodes = permNodes;
	}
	
	public String getName(){
		return groupName;
	}
	
	public PermNode[] getPermissions(){
		return permNodes;
	}
	
	public boolean isPermitted(String permNodeAlias){
		return isPermitted(new PermNode(permNodeAlias));
	}
	
	public boolean isPermitted(PermNode perm){
		if (permNodes == null){
			return false;
		}
		
		for (int i = 0; i < permNodes.length; i++){
			if (permNodes[i].equals(perm)){
				return true;
			}
		}
		return false;
	}
}
