package com.mob41.sakura.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.mob41.sakura.servlets.AccessTokenSession;
import com.mob41.sakura.servlets.ApiServlet;

public class Jetty {
	
	public static Server server;

	public static void start() throws Exception{
		server = new Server(8080);
        
        ServletContextHandler servcontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servcontext.setContextPath("/api");
        
        servcontext.addServlet(new ServletHolder(new ApiServlet()), "/*");
        server.setHandler(servcontext);
        
        server.start();
        server.join();

        
	}
	
	public static void main(String[] args) throws Exception{
		AccessTokenSession.runThread(50);
		start();
	}
}
