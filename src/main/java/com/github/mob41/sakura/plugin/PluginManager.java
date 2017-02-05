package com.github.mob41.sakura.plugin;

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

import com.github.mob41.sakura.action.ActionResponse;
import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.exception.InvalidPluginDescription;
import com.github.mob41.sakura.exception.InvalidPluginException;
import com.github.mob41.sakura.exception.NoSuchPluginException;
import com.github.mob41.sakura.hash.AES;

public class PluginManager {
	
	private static final String pluginFolderPath = System.getProperty("user.dir") + "/plugins";
	
	/**
	 * The default maximum plugins amount
	 */
	public static final int MAX_PLUGINS = 100;
	
	/**
	 * List of plugins
	 */
	private List<Plugin> plugins;
	
	private SakuraServer srv;

	/**
	 * Create a new <code>PluginManager</code> instance.<br>
	 * <br>
	 * It stores instances of <code>Plugin</code> or its inherits.
	 * @param srv A existing SakuraServer instance
	 */
	public PluginManager(SakuraServer srv){
		plugins = new ArrayList<Plugin>(MAX_PLUGINS);
		this.srv = srv;
	}
	
	/**
	 * Runs the action with the specified index from <code>getPluginActions()</code> array.
	 * @param pluginName The plugin's name 
	 * @param actionIndex The index from <code>getPluginActions()</code> array.
	 * @param args A array of parameters
	 * @return The action status and response in a <code>PluginResponse</code> instance.
	 * @throws NoSuchPluginException It is thrown if the <code>pluginName</code> specified is not loaded/invalid
	 */
	public ActionResponse runAction(String pluginName, int actionIndex, Object... args) throws NoSuchPluginException{
		Plugin plugin = getPlugin(pluginName);
		if (plugin == null){
			return null;
		}
		return plugin.runAction(actionIndex, args);
	}
	
	/**
	 * Get the <code>Plugin</code> instance with the plugin name
	 * @param pluginName The plugin's name
	 * @return The <code>Plugin</code> instance.
	 * @throws NoSuchPluginException It is thrown if the <code>pluginName</code> specified is not loaded/invalid
	 */
	public Plugin getPlugin(String pluginName) throws NoSuchPluginException{
		int index = getIndexOfPlugin(pluginName);
		if (index == -1){
			throw new NoSuchPluginException("The plugin \"" + pluginName + "\" wasn't loaded/found");
		}
		return plugins.get(index);
	}
	
	/**
	 * Returns loaded plugins.
	 * @return a <code>List</b> of loaded plugins
	 */
	public List<Plugin> getPlugins(){
		return plugins;
	}
	
	/**
	 * Add a plugin to the manager, with a plugin description (spec)
	 * @param plugin A plugin instance
	 * @param pluginDesc Plugin description in JSON
	 */
	public void addPlugin(Plugin plugin, PluginDescription desc){
		plugin.pluginUid = AES.getRandomByte();
		plugin.pluginDesc = desc;
		plugins.add(plugin);
	}
	
	/**
	 * Get the index of the <code>Plugin</code> instance in the list with the plugin name
	 * @param pluginUid A plugin's name
	 * @return The <code>Plugin</code> instance's index in the list
	 */
	public int getIndexOfPlugin(String pluginName){
		for (int i = 0; i < plugins.size(); i++){
			if (plugins.get(i).pluginDesc.getName().equals(pluginName)){
				return i;
			}
		}
		return -1;
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
				continue;
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
            loader = new PluginClassLoader(srv, description, PluginManager.class.getClassLoader(), file);
        } catch (InvalidPluginException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new InvalidPluginException(ex);
        }

        addPlugin(loader.getPlugin(), description);
        
        Plugin plugin = loader.getPlugin();
        
        try {
			loader.close();
		} catch (IOException e) {
			throw new InvalidPluginException("Error when closing loader.", e);
		}
        
        return plugin;
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
