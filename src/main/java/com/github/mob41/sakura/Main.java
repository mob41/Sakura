package com.github.mob41.sakura;

import java.util.Calendar;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.dynamic.DynamicHandler;
import com.github.mob41.sakura.dynamic.DynamicManager;

public class Main {
	
	public static void main(String[] args) throws Exception {
		SakuraServer srv = new SakuraServer(80);
		srv.startWebServer();
	}

}