package com.github.mob41.sakura.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.mob41.sakura.action.Action;

/**
 * An API to port plugins into Sakura system
 * @author Anthony
 *
 */
public abstract class Plugin{
	
	private static final String _workingDir = System.getProperty("user.dir");
	
	private List<Action> _pluginActions;
	
	public Plugin(PluginDescription pluginDesc){
		this.pluginDesc = pluginDesc;
		_pluginActions = new ArrayList<Action>(50);
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
	 * Returns a array of <code>PluginAction</code> that can be ran.<br>
	 * <br>
	 * The plugin action can also be ran programmically.
	 * @return a array of <code>PluginAction</code> instances
	 */
	public Action[] getActions(){
		if (_pluginActions == null){
			return null;
		}
		Action[] arr = new Action[_pluginActions.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = _pluginActions.get(i);
		}
		return arr;
	}
	
	public PluginResponse runAction(int index, Object... args){
		if (_pluginActions == null){
			return PluginResponse.getNotImplementedResponse();
		} else if (_pluginActions.size() < index || index < 0){
			return PluginResponse.getNotImplementedResponse();
		}
		
		return _pluginActions.get(index).run(args);
	}
	
	public void setParameter(int index, Object value){
		
	}
	
	public PluginParameter getParameter(int index){
		return null;
	}
	
	public Object getParameterValue(int index){
		return null;
	}
	
	public final boolean addAction(Action pluginAction){
		return _pluginActions.add(pluginAction);
	}
	
	public final void removeAction(int index){
		_pluginActions.remove(index);
	}
	
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
		File folder = new File(_workingDir + "/pluginData/" + pluginDesc.getName());
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(_workingDir + "/pluginData/" + pluginDesc.getName() + "/" + fileName);
		if (!file.exists()){
			file.createNewFile();
		}
		
		return new FileOutputStream(file);
	}
	
	/**
	 * Removes the specified file / folder from the <code>pluginData/{pluginName}/{fileName}</code><br>
	 * <br>
	 * This function works properly to a folder <b>only if the folder is empty</b>.<br>
	 * If <code>fileName</code> is empty, the plugin data folder will be removed.
	 * @param fileName The file / folder name to be removed.
	 * @return A <code>Boolean</code> whether the removal was successful or not.
	 * @throws IOException If I/O goes wrong
	 */
	public final boolean removeFile(String fileName) throws IOException{
		File file = new File(_workingDir + "/pluginData/" + pluginDesc.getName() + (fileName == null || fileName.isEmpty() ? "" : "/" + fileName));
		if (file.exists()){
			return file.delete();
		} else {
			return false;
		}
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
		File file = new File(_workingDir + "/pluginData/" + pluginDesc.getName() + "/" + fileName);
		
		if (!file.exists()){
			return null;
		}
		
		return new FileInputStream(file);
	}
	
}
