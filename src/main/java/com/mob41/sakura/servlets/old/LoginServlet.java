package com.mob41.sakura.servlets.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.mob41.sakura.auth.LoginAuth;
import com.mob41.sakura.auth.SessionAuth;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected SessionAuth sesthread;
	
	private static List<String> codeverify = new ArrayList<String>();
	
	protected static boolean isCodeExist(String code){
		Object[] codearr = codeverify.toArray();
		String codeitem;
		int amount = codearr.length;
		int i;
		for (i = 0; i < amount; i++){
			codeitem = (String) codearr[i];
			if (codeitem.equals(code)){
				return true;
			}
		}
		return false;
	}
	
	protected static int getCodeIndex(String code){
		Object[] codearr = codeverify.toArray();
		String codeitem;
		int amount = codearr.length;
		int i;
		for (i = 0; i < amount; i++){
			codeitem = (String) codearr[i];
			if (codeitem.equals(code)){
				return i;
			}
		}
		return -1;
	}
	
	protected static boolean addCode(String code){
		if (isCodeExist(code)){
			return false;
		}
		try {
			codeverify.add(code);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	protected static boolean removeCode(String code){
		if (!isCodeExist(code)){
			return false;
		}
		int index = getCodeIndex(code);
		if (index <= -1){
			return false;
		}
		try {
			codeverify.remove(index);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	private static final Logger logger = LogManager.getLogger(LoginServlet.class.getName());
       
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(403);
		response.setContentType("text/plain");
		response.getWriter().println("HomeAutoSys API for WebUI or Automation Purpose");
		response.getWriter().println("Copyright (c) 2015 Anthony Law. All rights reserved.\n");
		response.getWriter().println("Error 403: You are not allowed to get here.");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST");
		JSONObject json = new JSONObject();
		Calendar cal = Calendar.getInstance();
		response.setContentType("application/json");
		json.put("generated", cal.getTimeInMillis());
		try {
			String remoteIP = request.getRemoteAddr();
			long regtime = cal.getTimeInMillis();
			logger.info("The device ("+ request.getRemoteAddr() +") is at API host.");
			String action = request.getParameter("action");
			switch(action){
			case "showcodeui":
				logger.info("The device ("+ request.getRemoteAddr() +") asking to show code on UI");
				String randcode = HashKey.getRandom(3);
				boolean successaddcode = addCode(randcode);
				if (!successaddcode){
					json.put("status", -1);
					json.put("response", "Could not generate code");
				}
				else
				{
					if (!ShowCodeUI.isRunning){
						ShowCodeUI.start(randcode);
						json.put("status", 1);
						json.put("response", "Generated. Timeout 10 seconds");
					} else
					{
						json.put("status", -1);
						json.put("response", "A code is still showing on the LCD");
					}
				}
				response.getWriter().println(json);
				return;
			case "unregister":
				logger.info("The device ("+ request.getRemoteAddr() +") is unregistering");
				String unreguser = request.getParameter("username");
				String unregsessionkey = request.getParameter("sessionkey");
				String unregauthkey = request.getParameter("authkey");
				try {
					if (!HashKey.auth(unregauthkey)){
						json.put("status", -1);
						json.put("response", "Authentication problems.");
						response.getWriter().println(json);
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (!SessionAuth.sesthread.isSessionAvailable(unregsessionkey)){
					json.put("status", -3);
					json.put("response", "Invaild session");
					response.getWriter().println(json);
					return;
				}
				boolean successunreg = false;
				try {
					LoginAuth.removeUser(unreguser);
					successunreg = true;
				} catch (Exception e){
					successunreg = false;
				}
				if (!successunreg){
					json.put("status", -1);
					json.put("response", "Could not unregister.");
				}
				else
				{
					json.put("status", 1);
					json.put("response", "Unregistered.");
				}
				response.getWriter().println(json);
				return;
			case "register":
				logger.info("The device ("+ request.getRemoteAddr() +") is registering");
				String regusr = request.getParameter("username");
				String regpwd = request.getParameter("password");
				String key = request.getParameter("code");
				boolean regsuccess = isCodeExist(key);
				if (regsuccess){
					removeCode(key);
				} else {
					json.put("response", "Invaild code");
					json.put("status", -1);
					response.getWriter().println(json);
					return;
				}
				try {
					LoginAuth.importClearTypeUsrPwd(regusr, regpwd);
					regsuccess = true;
					json.put("response", "Registered.");
					json.put("status", 1);
				} catch (Exception e){
					regsuccess = false;
					json.put("response", "Error occurred");
					json.put("status", 1);
				}
				response.getWriter().println(json);
				return;
			case "checksession":
				String checksessionkey = request.getParameter("sessionkey");
				boolean session = SessionAuth.sesthread.isSessionAvailable(checksessionkey);
				json.put("session", session);
				json.put("response", session ? "Session vaild" : "Session Invaild");
				json.put("status", 1);
				json.put("sessionkey", checksessionkey);
				response.getWriter().println(json);
				return;
			case "loginct":
				String usrct = request.getParameter("username");
				String pwdct = request.getParameter("password");
				if (!LoginAuth.authClearType(usrct, pwdct)){
					logger.info("The device (" + remoteIP + ") entered a wrong password./ The user doesn't exist");
					json.put("response", "Wrong password.");
					json.put("session", "false");
					json.put("sessionkey", "null");
					json.put("status", "-1");
					response.getWriter().println(json);
					return;
				}
				String result = SessionAuth.sesthread.registerSession(HashKey.getRandomSalt(), usrct, remoteIP, Long.toString(regtime),
						Integer.toString(SessionAuth.DEFAULT_TIMEOUT));
				if (result == "-1"){
					logger.error("The device (" + remoteIP + ") could not register session.");
					json.put("response", "Could not register session.");
					json.put("session", false);
					json.put("sessionkey", "null");
					json.put("code", "error_noreg_session");
					json.put("status", "-1");
					response.getWriter().println(json);
					return;
				}
				logger.info("The device (" + remoteIP + ") registed a session: " + result);
				json.put("response", "Session registered. / Loading from vaild session.");
				json.put("session", true);
				json.put("sessionkey", result);
				json.put("status", "1");
				response.getWriter().println(json);
				logger.info("The device (" + remoteIP + ") was disconnected.");
			}
		} catch (NullPointerException e){
			logger.error("The device (" + request.getRemoteAddr() + ") was missing parameters");
			json.put("response", "Missing parameters for specific action or required parameters.");
			json.put("exception", "java.lang.NullPointerException");
			json.put("status", "-1");
			response.getWriter().println(json);
		}
	}
	

}
