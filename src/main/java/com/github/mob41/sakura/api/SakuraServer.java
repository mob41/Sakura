package com.github.mob41.sakura.api;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import com.github.mob41.sakura.action.ActionManager;
import com.github.mob41.sakura.appliance.ApplianceManager;
import com.github.mob41.sakura.dynamic.DynamicManager;
import com.github.mob41.sakura.error.ErrorManager;
import com.github.mob41.sakura.exception.SakuraServerException;
import com.github.mob41.sakura.image.ImageManager;
import com.github.mob41.sakura.notification.AlertManager;
import com.github.mob41.sakura.notification.NotificationManager;
import com.github.mob41.sakura.page.CustomPageManager;
import com.github.mob41.sakura.plugin.PluginManager;
import com.github.mob41.sakura.power.PowerManager;
import com.github.mob41.sakura.scene.SceneManager;
import com.github.mob41.sakura.security.PermManager;
import com.github.mob41.sakura.security.UserManager;
import com.github.mob41.sakura.server.APIServlet;
import com.github.mob41.sakura.trigger.TriggerManager;

public class SakuraServer {
	
	public static final String WEB_APP_SERVER_ATTRIBUTE = "com.github.mob41.sakura.api.SakuraServer.WebAppServerInstance";
	
	private final SakuraAPI api;
	
	private final EasyAPI eapi;
	
	private final ActionManager actionMgr;
	
	private final ApplianceManager applianceMgr;
	
	private final ErrorManager errorMgr;
	
	private final NotificationManager notifyMgr;
	
	private final CustomPageManager custPageMgr;
	
	private final PluginManager plugMgr;
	
	private final PowerManager powerMgr;
	
	private final SceneManager sceneMgr;
	
	private final ImageManager imageMgr;
	
	private final PermManager permMgr;
	
	private final UserManager userMgr;
	
	private final TriggerManager triMgr;
	
	private final AlertManager alertMgr;
	
	private final APIManager apiMgr;
	
	private final DynamicManager dynamicMgr;
	
	private final Server jettySrv;

	public SakuraServer(int webServerPort) throws SakuraServerException{
		try {
			applianceMgr = new ApplianceManager(this);
			
			actionMgr = new ActionManager(this);
			custPageMgr = new CustomPageManager(this);
			
			errorMgr = new ErrorManager(this);
			
			notifyMgr = new NotificationManager(this);
			alertMgr = new AlertManager(this);
			
			powerMgr = new PowerManager(this);
			sceneMgr = new SceneManager(this);
			
			permMgr = new PermManager(this);
			userMgr = new UserManager(this, 50);
			
			triMgr = new TriggerManager(this);
			plugMgr = new PluginManager(this);
			
			imageMgr = new ImageManager();
			
			apiMgr = new APIManager(this);
			
			api = new SakuraAPI(this);
			
			eapi = new EasyAPI(this);
			
			dynamicMgr = new DynamicManager();
			
			plugMgr.loadAllPlugins();
			
			jettySrv = new Server(webServerPort);
	        
	        ContextHandlerCollection coll = new ContextHandlerCollection();

	        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
	        jettySrv.addBean(mbContainer);
	        
	        WebAppContext webapp = new WebAppContext();
	        webapp.setContextPath( "/" );
	        System.out.println(SakuraServer.class.getClassLoader().getResource("webapp").toURI().toURL().toString());
	        webapp.setResourceBase(SakuraServer.class.getClassLoader().getResource("webapp").toURI().toURL().toString());
	        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

	        Configuration.ClassList classlist = Configuration.ClassList
	                .setServerDefault(jettySrv);
	        classlist.addBefore(
	                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
	                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
	        webapp.setAttribute(
	                "org.eclipse.jetty.jettySrv.webapp.ContainerIncludeJarPattern",
	                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
	        
	        webapp.setAttribute("org.eclipse.jetty.containerInitializers" , Arrays.asList (
	                new ContainerInitializer(new JettyJasperInitializer(), null)));
	        webapp.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
	        
	        webapp.setAttribute(WEB_APP_SERVER_ATTRIBUTE, this);
	        
	        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
	        errorHandler.addErrorPage(404, "/error.jsp?error=404");
	        errorHandler.addErrorPage(403, "/error.jsp?error=403");
	        errorHandler.addErrorPage(500, "/error.jsp?error=500");
	        
	        webapp.setErrorHandler(errorHandler);
	        
	        coll.addHandler(webapp);
	        
	        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        servletHandler.setContextPath("/api/");
	        
	        servletHandler.addServlet(new ServletHolder(new APIServlet()), "/*");
	        
	        servletHandler.setAttribute(WEB_APP_SERVER_ATTRIBUTE, this);
	        
	        coll.addHandler(servletHandler);
	        
	        jettySrv.setHandler(coll);
		} catch (Exception e){
			throw new SakuraServerException("Error when initializing SakuraServer instance", e);
		}
	}
	
	public void stopWebServer() throws SakuraServerException{
		if (jettySrv != null && jettySrv.isRunning()){
			try {
				jettySrv.stop();
			} catch (Exception e) {
				throw new SakuraServerException("Unable to stop Jetty web server: " + e);
			}
		} else {
			throw new SakuraServerException("The server wasn't running");
		}
	}
	
	public void startWebServer() throws SakuraServerException{
		if (jettySrv.isStopped()){
			try {
				jettySrv.start();
			} catch (Exception e) {
				throw new SakuraServerException("Unable to start up Jetty Web Server: " + e);
			}
		} else {
			throw new SakuraServerException("The Jetty web server is already started");
		}
	}
	
	public DynamicManager getDynamicManager(){
		return dynamicMgr;
	}
	
	public APIManager getAPIManager(){
		return apiMgr;
	}
	
	public ActionManager getActionManager(){
		return actionMgr;
	}
	
	public ApplianceManager getApplianceManager(){
		return applianceMgr;
	}
	
	public ErrorManager getErrorManager(){
		return errorMgr;
	}
	
	public NotificationManager getNotificationManager(){
		return notifyMgr;
	}
	
	public CustomPageManager getCustomPageManager(){
		return custPageMgr;
	}
	
	public PluginManager getPluginManager(){
		return plugMgr;
	}
	
	public PowerManager getPowerManager(){
		return powerMgr;
	}
	
	public SceneManager getSceneManager(){
		return sceneMgr;
	}
	
	public PermManager getPermManager(){
		return permMgr;
	}
	
	public UserManager getUserManager(){
		return userMgr;
	}
	
	public TriggerManager getTriggerManager(){
		return triMgr;
	}
	
	public AlertManager getAlertManager(){
		return alertMgr;
	}
	
	public ImageManager getImageManager(){
		return imageMgr;
	}
	
	/**
	 * Provides an end-to-end data encrypted API.
	 * @return A SakuraAPI instance to communicate with this server instance
	 */
	public SakuraAPI getAPI(){
		return api;
	}
	
	/**
	 * Provides an easy-to-access API for development purpose.<br>
	 * <br>
	 * In production, the SakuraAPI encryption API should be used.
	 * @return
	 */
	public EasyAPI getEasyAPI(){
		return eapi;
	}

}
