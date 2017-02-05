package com.github.mob41.sakura.api;

import org.json.JSONObject;

/**
 * Provides an interface to control a external API from plugins in a SakuraAPI protocol.<br>
 * <br>
 * It is compact to use in some API applications which require
 * different status and programs from Sakura, e.g. SakuraUI. 
 * This allows to interface the plugin within the SakuraAPI protocol
 * directly.
 * @author Anthony
 *
 */
public interface ExternalAPI {
	
	/**
	 * Returns the name of this API
	 * @return String
	 */
	public String getName();
	
	/**
	 * Returns the permission node (e.g. <code>system.api</code>) of this API.<br>
	 * <br>
	 * Before the request pass through <code>isAllowed()</code>, 
	 * this function will be called to check the permissions of the 
	 * API user whether the user is permitted to use this permission node. 
	 * If not, the request will return a Permission Denied result code.<br>
	 * <br>
	 * If this function returned <code>null</code>, the permission node will not be checked.
	 * @return String
	 */
	public String getPermNode();

	/**
	 * Checks whether this API is allowed to access.<br>
	 * <br>
	 * This will be called after <code>getPermNode()</code>.
	 * The developer can make a more optional API access
	 * granting control using this function, besides of
	 * using Sakura's internal permission nodes system.<br>
	 * <br>
	 * A original decrypted <code>JSONObject</code> of request
	 *  will be passed into this function.
	 * @param in Decrypted request data in <code>JSONObject</code>
	 * @return whether this API is allowed to access
	 */
	public boolean isAllowed(JSONObject in);
	
	/**
	 * Handles the incoming request.<br>
	 * <br>
	 * This will be called after <code>getPermNode()</code> 
	 * and <code>isAllowed()</code>. The developer can
	 * handle the request incoming to this external
	 * API, return a new <code>JSONObject</code> response
	 * to the API user.<br>
	 * <br>
	 * Encryption is done after this function. It is also
	 * okay to add another layer of encryption optionally
	 * before the <code>toEnc()</code> function in 
	 * <code>SakuraAPI</code>.
	 * @param in Decrypted request data in <code>JSONObject</code>
	 * @return Response in <code>JSONObject</code.
	 */
	public JSONObject handle(JSONObject in);
}
