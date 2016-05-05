package com.mob41.sakura.plugin;

import java.io.File;

public class PluginLoader extends ClassLoader {
	
	public static final String ext = ".jar";
	
	public String pluginsFolder = System.getProperty("user.dir") + "\\plugins";
	
	public void loadAllPlugins(){
		File folder = new File(pluginsFolder);
		if (!folder.exists()){
			folder.mkdir();
		}
		File[] files = folder.listFiles();
		
		for (File file : files){
			String fileName = file.getName();
			if (fileName.substring(fileName.length() - 4).equals(".jar")){
				//TODO Search jar, probably these code is invalid... :(
			}
		}
	}
	
}
