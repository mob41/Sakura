package com.mob41.sakura.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.json.JSONException;
import org.json.JSONObject;

public class PluginLoader{
	
	private String pluginFolder = System.getProperty("user.dir") + "\\plugins";

	public void loadAllPlugins() throws IOException, ClassNotFoundException, JSONException, InstantiationException, IllegalAccessException{
		File folder = new File(pluginFolder);
		if (!folder.exists()){
			folder.mkdirs();
		}
		File[] files = folder.listFiles();
		URL[] urls = new URL[files.length];
		for (int i = 0; i < files.length; i++){
			try {
				urls[i] = files[i].toURI().toURL();
			} catch (MalformedURLException e) {
				urls[i] = null;
				e.printStackTrace();
			}
		}
	    
		for (URL url : urls){
			URLClassLoader cl = new URLClassLoader(new URL[]{ url },System.class.getClassLoader());
			JSONObject desc = new JSONObject(cl.getResourceAsStream("plugin.json"));
			
			Class pluginClass = cl.loadClass(desc.getString("class"));
			Plugin plugin = (Plugin) pluginClass.newInstance();
			
			PluginManager.getPluginManager().addPlugin(plugin, desc);
			cl.close();
		}
	}
	
	private static String read(FileInputStream in) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return sb.toString();
	}
}
