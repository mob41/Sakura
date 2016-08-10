package com.github.mob41.sakura.server;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class Jetty {
	
	public static Server server;

	public static void start() throws Exception
    {
        Server server = new Server(8080);
        
        ContextHandlerCollection coll = new ContextHandlerCollection();

        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
        
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath( "/" );
        webapp.setResourceBase("webapp");

        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault(server);
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
        webapp.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
        
        coll.addHandler(webapp);
        
        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/api/");
        
        servletHandler.addServlet(new ServletHolder(new APIServlet()), "/*");
        
        coll.addHandler(servletHandler);
        
        server.setHandler(coll);
        server.start();
        server.join();
    }
	
	public static void main(String[] args) throws Exception{
		start();
	}
}
