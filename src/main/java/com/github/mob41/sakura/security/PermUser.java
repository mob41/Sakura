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
		
		System.out.println("Self nodes: " + userNodes.length() + " nodes");
		for (int i = 0; i < selfNodes.length; i++){
			System.out.println("(" + i + ") per: " + userNodes.getString(i));
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
			System.out.println("False permnode null");
			return false;
		}
		
		boolean gpPer = false;
		System.out.println("Checking assocGroups total " + assocGroups.length);
		for (int i = 0; i < assocGroups.length; i++){
			System.out.println("Checking permmit: " + assocGroups[i].getName());
			gpPer = assocGroups[i].isPermitted(permNode);
		}
		System.out.println("GpPer: " + gpPer);
		System.out.println("End asscoGroup check");
		
		boolean selfPer = isSelfPermitted(permNode);
		
		System.out.println("SelfPer: " + selfPer);
		
		return selfPer || gpPer;
	}
	
	public boolean isSelfNodeExist(PermNode permNode){
		System.out.println("Searching: " + permNode);
		for (int i = 0; i < selfNodes.length; i++){
			System.out.println(selfNodes[i].getAlias());
			if (permNode.equals(selfNodes[i])){
				System.out.println("True");
				return true;
			}
		}
		System.out.println("Not found");
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
