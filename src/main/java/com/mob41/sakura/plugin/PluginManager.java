package com.mob41.sakura.plugin;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.mob41.sakura.hash.AES;

public class PluginManager {
	
	private static final PluginManager pluginManager = new PluginManager();
	
	/**
	 * The default maximum plugins amount
	 */
	public static final int MAX_PLUGINS = 100;
	
	/**
	 * List of plugins
	 */
	private List<Plugin> plugins;

	/**
	 * Create a new <code>PluginManager</code> instance.<br>
	 * <br>
	 * It stores instances of <code>Plugin</code> or its inherits.
	 */
	public PluginManager(){
		plugins = new ArrayList<Plugin>(MAX_PLUGINS);
	}
	
	
	public Object runPluginLifeCycle(String pluginUid, Object data){
		Plugin plug = getPlugin(pluginUid);
		plug.onCallPlugin();
		plug.onPluginReceiveData(data);
		Object object = plug.onPluginSendData();
		plug.onEndPlugin();
		return object;
	}
	
	/**
	 * Call the plugin
	 * @param pluginUid A plugin's UID
	 */
	public void callPlugin(String pluginUid){
		getPlugin(pluginUid).onCallPlugin();
	}
	/**
	 * End the plugin
	 * @param pluginUid A plugin's UID
	 */
	public void endPlugin(String pluginUid){
		getPlugin(pluginUid).onEndPlugin();
	}
	
	/**
	 * Send (raw) data to the plugin
	 * @param pluginUid A plugin's UID
	 * @param data The (raw) data. Can be <code>null</code> or <code>JSONObject</code>
	 */
	public void sendDataToPlugin(String pluginUid, Object data){
		getPlugin(pluginUid).onPluginReceiveData(data);
	}
	
	/**
	 * Receive (raw) data from the plugin
	 * @param pluginUid A plugin's UID
	 * @return The (raw) data. Can be <code>null</code> or <code>JSONObject</code>
	 */
	public Object receiveDataFromPlugin(String pluginUid){
		return getPlugin(pluginUid).onPluginSendData();
	}
	
	/**
	 * Get the <code>Plugin</code> instance with the plugin UID
	 * @param pluginUid A plugin's UID
	 * @return The <code>Plugin</code> instance.
	 */
	public Plugin getPlugin(String pluginUid){
		int index = getIndexOfPlugin(pluginUid);
		if (index == -1){
			return null;
		}
		return plugins.get(index);
	}
	
	public void addPlugin(Plugin plugin, JSONObject pluginDesc){
		plugin.pluginUid = AES.getRandomByte();
		plugin.pluginName = pluginDesc.getString("name");
		plugin.pluginVer = pluginDesc.getString("version");
		plugins.add(plugin);
	}
	
	/**
	 * Get the index of the <code>Plugin</code> instance in the list with the plugin UID
	 * @param pluginUid A plugin's UID
	 * @return The <code>Plugin</code> instance's index in the list
	 */
	public int getIndexOfPlugin(String pluginUid){
		for (int i = 0; i < plugins.size(); i++){
			if (plugins.get(i).pluginUid.equals(pluginUid)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the currently running plugin manager.
	 * @return PluginManager
	 */
	public static PluginManager getPluginManager(){
		return pluginManager;
	}
	
	
}
