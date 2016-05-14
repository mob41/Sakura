package com.mob41.sakura.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An API to port plugins into Sakura system
 * @author Anthony
 *
 */
public abstract class Plugin extends Events{
	
	private static final String workingDir = System.getProperty("user.dir");
	
	public Plugin(PluginDescription pluginDesc){
		this.pluginDesc = pluginDesc;
	}

	/**
	 * The Unique ID of this plugin
	 */
	public String pluginUid;
	
	/**
	 * The Description of this plugin
	 */
	public PluginDescription pluginDesc;
	
	/**
	 * It is called when this plugin is called by the API
	 */
	public abstract void onCallPlugin();
	
	/**
	 * It is called when the API send data to the plugin
	 * @param data Data from the API (Can be <code>null</code> or <code>JSONObject</code>)
	 */
	public abstract void onPluginReceiveData(Object data);
	
	/**
	 * It is called when the API request the plugin to send data
	 * @return Data to be sent to the API (Can be <code>null</code> or <code>JSONObject</code>)
	 */
	public abstract Object onPluginSendData();
	
	/**
	 * It is called when this plugin is being ended.<br>
	 */
	public abstract void onEndPlugin();
	
	/**
	 * Get the <code>FileOutputStream</code> from the <code>pluginData/{pluginName}/{fileName}</code><br>
	 * <br>
	 * If the file does not exist, the file will be automatically created.<br>
	 * If the folder does not exist, the folder will be automatically created.
	 * @param fileName The filename to write into plugin data save
	 * @return <code>FileOutputStream</code>, to write raw to it
	 * @throws IOException If I/O goes wrong
	 */
	public final FileOutputStream getDataFileOutputStream(String fileName) throws IOException{
		File folder = new File(workingDir + "\\pluginData\\" + pluginDesc.getName());
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(workingDir + "\\pluginData\\" + pluginDesc.getName() + "\\" + fileName);
		if (!file.exists()){
			file.createNewFile();
		}
		
		return new FileOutputStream(file);
	}
	
	/**
	 * Get the <code>FileInputStream</code> from the <code>pluginData/{pluginName}/{fileName}</code><br>
	 * <br>
	 * It will return <code>null</code> if the file is not readable/does not exist.
	 * @param fileName The filename to read from the plugin data
	 * @return <code>FileInputStream</code>, to read raw from it
	 * @throws IOException If I/O goes wrong
	 */
	public final FileInputStream getDataFileInputStream(String fileName) throws IOException{
		System.out.println(pluginDesc.getRawJSON());
		File file = new File(workingDir + "\\pluginData\\" + pluginDesc.getName() + "\\" + fileName);
		
		if (!file.exists()){
			return null;
		}
		
		return new FileInputStream(file);
	}
	
	
}
