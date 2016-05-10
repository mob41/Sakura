package com.mob41.sakura.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.json.JSONObject;

import com.mob41.sakura.hash.AES;
import com.mob41.sakura.plugin.exception.InvalidPluginDescription;
import com.mob41.sakura.plugin.exception.InvalidPluginException;

public class PluginManager {
	
	private static final String pluginFolderPath = System.getProperty("user.dir") + "\\plugins";
	
	private static final PluginManager pluginManager = new PluginManager();
	
	/**
	 * The default maximum plugins amount
	 */
	public static final int MAX_PLUGINS = 100;
	
	/**
	 * List of plugins
	 */
	private List<Object> plugins;

	/**
	 * Create a new <code>PluginManager</code> instance.<br>
	 * <br>
	 * It stores instances of <code>Plugin</code> or its inherits.
	 */
	public PluginManager(){
		plugins = new ArrayList<Object>(MAX_PLUGINS);
	}
	
	
	public Object runPluginLifeCycle(String pluginName, Object data){
		Object plug = getPlugin(pluginName);
		((Plugin) plug).onCallPlugin();
		((Plugin) plug).onPluginReceiveData(data);
		Object object = ((Plugin) plug).onPluginSendData();
		((Plugin) plug).onEndPlugin();
		return object;
	}
	
	/**
	 * Call the plugin
	 * @param pluginUid A plugin's name
	 */
	public void callPlugin(String pluginName){
		((Plugin) getPlugin(pluginName)).onCallPlugin();
	}
	/**
	 * End the plugin
	 * @param pluginUid A plugin's name
	 */
	public void endPlugin(String pluginName){
		((Plugin) getPlugin(pluginName)).onEndPlugin();
	}
	
	/**
	 * Send (raw) data to the plugin
	 * @param pluginUid A plugin's name
	 * @param data The (raw) data. Can be <code>null</code> or <code>JSONObject</code>
	 */
	public void sendDataToPlugin(String pluginName, Object data){
		((Plugin) getPlugin(pluginName)).onPluginReceiveData(data);
	}
	
	/**
	 * Receive (raw) data from the plugin
	 * @param pluginUid A plugin's name
	 * @return The (raw) data. Can be <code>null</code> or <code>JSONObject</code>
	 */
	public Object receiveDataFromPlugin(String pluginName){
		return ((Plugin) getPlugin(pluginName)).onPluginSendData();
	}
	
	/**
	 * Get the <code>Plugin</code> instance with the plugin name
	 * @param pluginUid A plugin's name
	 * @return The <code>Plugin</code> instance.
	 */
	public Object getPlugin(String pluginName){
		int index = getIndexOfPlugin(pluginName);
		if (index == -1){
			return null;
		}
		return plugins.get(index);
	}
	
	/**
	 * Add a plugin to the manager, with a plugin description (spec)
	 * @param plugin A plugin instance
	 * @param pluginDesc Plugin description in JSON
	 */
	public void addPlugin(Plugin plugin, PluginDescription desc){
		JSONObject pluginDesc = desc.getRawJSON();
		plugin.pluginUid = AES.getRandomByte();
		plugin.pluginName = pluginDesc.getString("name");
		plugin.pluginVer = pluginDesc.getString("version");
		plugins.add(plugin);
	}
	
	/**
	 * Get the index of the <code>Plugin</code> instance in the list with the plugin name
	 * @param pluginUid A plugin's name
	 * @return The <code>Plugin</code> instance's index in the list
	 */
	public int getIndexOfPlugin(String pluginName){
		for (int i = 0; i < plugins.size(); i++){
			if (((Plugin) plugins.get(i)).pluginName.equals(pluginName)){
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
	
	public void loadAllPlugins() throws InvalidPluginException{
		File folder = new File(pluginFolderPath);
		if (!folder.exists() || !folder.isDirectory()){
			folder.mkdirs();
		}
		File[] files = folder.listFiles();
		for (File file : files){
			if (!file.getName().substring(file.getName().length() - 4).equals(".jar")){
				System.err.println("WARNING: Non-plugin files are in the plugins folder, which is not recommended.");
			}
			loadPlugin(file);
		}
	}
	
	public Plugin loadPlugin(final File file) throws InvalidPluginException {

        if (!file.exists()) {
            throw new InvalidPluginException(file.getPath() + " does not exist");
        }

        final PluginDescription description;
        try {
            description = getPluginDescription(file);
        } catch (InvalidPluginDescription ex) {
            throw new InvalidPluginException(ex);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getName());

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(String.format(
                "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                dataFolder,
                description.getName(),
                file
            ));
        }

        final PluginClassLoader loader;
        try {
            loader = new PluginClassLoader(description, System.class.getClassLoader(), file);
        } catch (InvalidPluginException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new InvalidPluginException(ex);
        }

        addPlugin(loader.getPlugin(), description);

        try {
			loader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return loader.getPlugin();
    }
	
	public PluginDescription getPluginDescription(File file) throws InvalidPluginDescription {

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("plugin.json");

            if (entry == null) {
                throw new InvalidPluginDescription("Jar does not contain plugin.json");
            }

            stream = jar.getInputStream(entry);

            return new PluginDescription(new JSONObject(read(stream)));

        } catch (IOException ex) {
            throw new InvalidPluginDescription(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }
	
	private static String read(InputStream in) throws IOException{
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
