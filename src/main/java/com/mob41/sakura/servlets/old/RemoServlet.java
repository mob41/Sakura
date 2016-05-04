package com.mob41.sakura.servlets.old;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JTabbedPane;

import org.json.JSONObject;

import com.mob41.sakura.auth.SessionAuth;
import com.rpi.ha.ui.UI;

@WebServlet("/remo")
public class RemoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RemoServlet() {
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
		JSONObject json = new JSONObject();
		response.setContentType("application/json");
		JTabbedPane tab;
		String verifycode;
		String randcode;
		int next;
		json.put("generated", Calendar.getInstance().getTimeInMillis());
		response.setStatus(202);
		try {
			String action = request.getParameter("action");
			if (action.equals("test")){
				json.put("status", 1);
				json.put("response", "test");
				response.getWriter().println(json);
				return;
			}
			String sessionkey = request.getParameter("sessionkey");
			String authkey = request.getParameter("authkey");
			System.out.println("Action: " + action + "\nSession: " + sessionkey + "\nAuth: " + authkey);
			if (!HashKey.auth(authkey)){
				response.setStatus(403);
				json.put("status", -2);
				json.put("response", "Authentication problems");
				response.getWriter().println(json);
				return;
			} else if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
				response.setStatus(403);
				json.put("status", -3);
				json.put("response", "Invaild Session");
				response.getWriter().println(json);
				return;
			}
			switch (action){
			case "nttab":
				tab = UI.window.tab;
				next = tab.getSelectedIndex() + 1;
				if (next < tab.getTabCount()){
					tab.setSelectedIndex(next);
				}
				json.put("status", 1);
				json.put("response", "command sent");
				break;
			case "pstab":
				tab = UI.window.tab;
				next = tab.getSelectedIndex() - 1;
				if (next >= 0){
					tab.setSelectedIndex(next);
				}
				json.put("status", 1);
				json.put("response", "command sent");
				break;
			case "restart":
				verifycode = request.getParameter("code");
				if (verifycode.equals("none")){
					randcode = HashKey.getRandom(3);
					LoginServlet.addCode(randcode);
					ShowCodeUI.start(randcode);
					json.put("status", 1);
					json.put("response", "Code is shown on LCD.");
					break;
				}
				if (LoginServlet.isCodeExist(verifycode)){
					json.put("status", 1);
					json.put("response", "Restarting");
					try {
						Runtime.getRuntime().exec("sudo reboot");
					} catch (RuntimeException e){
						e.printStackTrace();
					}
					break;
				}
				else
				{
					json.put("status", -1);
					json.put("response", "Invaild Code");
					break;
				}
			case "halt":
				verifycode = request.getParameter("code");
				if (verifycode.equals("none")){
					randcode = HashKey.getRandom(3);
					LoginServlet.addCode(randcode);
					ShowCodeUI.start(randcode);
					json.put("status", 1);
					json.put("response", "Code is shown on LCD.");
					break;
				}
				if (LoginServlet.isCodeExist(verifycode)){
					json.put("status", 1);
					json.put("response", "Halting");
					try {
						Runtime.getRuntime().exec("sudo halt");
					} catch (RuntimeException e){
						e.printStackTrace();
					}
					break;
				}
				else
				{
					json.put("status", -1);
					json.put("response", "Invaild Code");
					break;
				}
			default:
				json.put("status", -1);
				json.put("response", "unknown command");
				break;
			}
			response.getWriter().println(json);
			return;
		} catch (NullPointerException e){
			response.setStatus(400);
			json.put("status", -1);
			json.put("response", "invaild request");
			response.getWriter().println(json);
			return;
		} catch (Exception e){
			e.printStackTrace();
			response.setStatus(501);
			json.put("status", -1);
			json.put("response", "server error");
			response.getWriter().println(json);
			return;
		}
	}

}
