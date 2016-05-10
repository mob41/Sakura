package com.mob41.sakura.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import com.mob41.sakura.plugin.exception.InvalidPluginDescription;

public class PluginDescription{
	
	private final String mainclass;

	private final String name;
	
	private final String version;
	
	private final String author;
	
	private final String website;
	
	private final String desc;
	
	private final JSONObject rawJson;
	
	/**
	 * Creates a new PluginDescription from a raw JSON.<br>
	 * <br>
	 * The JSON must exists these entries:<br>
	 * - Name<br>
	 * - Description<br>
	 * - Class<br>
	 * - Version<br>
	 * - Author<br>
	 * - Website<br>
	 * @param raw The JSON (plugin.json) from the plugin
	 * @throws InvalidPluginDescription If the JSON has invalid or missing entries
	 */
	public PluginDescription(JSONObject raw) throws InvalidPluginDescription{
		this.rawJson = raw;
		try {
			name = raw.getString("name");
			desc = raw.getString("desc");
			version = raw.getString("version");
			author = raw.getString("author");
			mainclass = raw.getString("class");
			website = raw.getString("website");
		} catch (JSONException e){
			throw new InvalidPluginDescription("Error parsing plugin description JSON: " + e);
		}
	}
	
	/**
	 * Returns the plugin's name specified in the description
	 * @return Plugin's name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the plugin description
	 * @return Plugin's description
	 */
	public String getDesc(){
		return desc;
	}
	
	/**
	 * Returns the main class of this plugin. The main class must be extended to <code>com.mob41.sakura.plugin.Plugin</code>
	 * @return Plugin's main class in String
	 */
	public String getMainClass(){
		return mainclass;
	}
	
	/**
	 * Returns the plugin's version.
	 * @return Plugin's version
	 */
	public String getVersion(){
		return version;
	}
	
	/**
	 * Returns the plugin's author name
	 * @return Plugin's author name
	 */
	public String getAuthor(){
		return author;
	}
	
	/**
	 * Returns the plugin's website
	 * @return Plugin's website URL
	 */
	public String getWebsite(){
		return website;
	}
	
	/**
	 * Returns the raw JSON of the plugin description.
	 * @return JSONObject
	 */
	public JSONObject getRawJSON(){
		return rawJson;
	}
}
