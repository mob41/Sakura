package com.github.mob41.sakura.action;

import com.github.mob41.sakura.plugin.ParameterType;
import com.github.mob41.sakura.plugin.PluginResponse;

public interface ActionBase {
	
	/**
	 * Returns a array of parameters' types. If returned <code>null</code> or invalid,
	 *  no parameters will be required on function <code>run(Object... args)</code>.<br>
	 *  <br>
	 *  Otherwise, the <code>run(Object... args)</code> function requires the same parameters' types to be specified. 
	 * @return a array of instances of parameters' types.
	 */
	public ParameterType[] getParameterTypes();
	
	/**
	 * Runs this action.<br>
	 * @param args A array of arguments, not <code>null</code> if <code>getParameterTypes()</code> is not returned <code>null</code> or invalid.
	 * @return This action status, and message in a <code>PluginResponse</code> instance. It may be <code>null</code>
	 * if the developer does not return a proper <code>PluginResponse</code>
	 * instance. The message may be <code>null</code> if the status is <code>PluginResponse.STATUS_NO_RESPONSE</code> or bad coding of the developer
	 */
	public PluginResponse run(Object... args);
	
}
