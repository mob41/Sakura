package com.github.mob41.sakura.action;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.dynamic.DynamicManager;

public abstract class Action{
	
	private final String _actionName;
	
	private final SakuraServer _sakuraSrv;
	
	public Action(SakuraServer srv, String actionName){
		this._actionName = actionName;
		this._sakuraSrv = srv;
	}
	
	public final SakuraServer getServer(){
		return _sakuraSrv;
	}
	
	public final String getName(){
		return _actionName;
	}
	
	/**
	 * Returns a array of parameters' types. If returned <code>null</code> or invalid,
	 *  no parameters will be required on function <code>run(Object... args)</code>.<br>
	 *  <br>
	 *  Otherwise, the <code>run(Object... args)</code> function requires the same parameters' types to be specified. 
	 * @return a array of instances of parameters' types.
	 */
	public abstract ParameterType[] getParameterTypes();
	
	/**
	 * Runs this action, <b>without dynamic variables</b>, use the method <code>dynRun()</code> instead!
	 * @param args A array of arguments, not <code>null</code> if <code>getParameterTypes()</code> is not returned <code>null</code> or invalid.
	 * @return This action status, and message in a <code>PluginResponse</code> instance. It may be <code>null</code>
	 * if the developer does not return a proper <code>PluginResponse</code>
	 * instance. The message may be <code>null</code> if the status is <code>PluginResponse.STATUS_NO_RESPONSE</code> or bad coding of the developer
	 */
	public abstract ActionResponse run(Object[] args);
	
	/**
	 * Runs this action, and process all dynamic variables (e.g. <code>#@=DynVar=@#</code>)<br>
	 * <br>
	 * *Overriding this function to return the result of the method <code>run()</code> can
	 * bypass the dynamic variable processing if necessary.
	 * @param args A array of arguments, not <code>null</code> if <code>getParameterTypes()</code> is not returned <code>null</code> or invalid.
	 * @return This action status, and message in a <code>PluginResponse</code> instance. It may be <code>null</code>
	 * if the developer does not return a proper <code>PluginResponse</code>
	 * instance. The message may be <code>null</code> if the status is <code>PluginResponse.STATUS_NO_RESPONSE</code> or bad coding of the developer
	 */
	public ActionResponse dynRun(Object[] args){
		if (args == null){
			run(null);
		}
		
		DynamicManager dynMgr = getServer().getDynamicManager();
		
		for (int i = 0; i < args.length; i++){
			if (args[i] instanceof String){
				args[i] = dynMgr.processDynamicString((String) args[i]);
			}
		}
		return run(args);
	}
}
