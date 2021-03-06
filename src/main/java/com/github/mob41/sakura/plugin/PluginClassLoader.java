package com.github.mob41.sakura.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.exception.InvalidPluginException;

public class PluginClassLoader extends URLClassLoader {
	
	private final PluginDescription desc;
	private final Plugin plugin;
	
	public PluginClassLoader(final SakuraServer srv, final PluginDescription desc, final ClassLoader parent, final File file) throws MalformedURLException, InvalidPluginException {
		super(new URL[]{file.toURI().toURL()}, parent);
		
		this.desc = desc;
		
		try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(desc.getMainClass(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `" + desc.getMainClass() + "'", ex);
            }
            Class<? extends Plugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(Plugin.class);
            } catch (ClassCastException ex) {
                throw new InvalidPluginException("main class `" + desc.getMainClass() + "' does not extend Plugin", ex);
            }
            plugin = pluginClass.getDeclaredConstructor(PluginDescription.class, SakuraServer.class).newInstance(desc, srv);
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
		} catch (Exception ex) {
			throw new InvalidPluginException("Error", ex);
		}
	}
	
	/**
	 * Returns the loaded Plugin
	 * @return The Plugin loaded
	 */
	public Plugin getPlugin(){
		return plugin;
	}
	
	/**
	 * Returns the plugin description loaded.
	 * @return
	 */
	public PluginDescription getPluginDesc(){
		return this.desc;
	}

}
