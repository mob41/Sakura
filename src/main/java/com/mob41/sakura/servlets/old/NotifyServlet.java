package com.mob41.sakura.servlets.old;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import org.mob41.pushbullet.api.PBClient;
import org.mob41.pushbullet.api.PBServer;

import com.mob41.sakura.auth.SessionAuth;
import com.rpi.ha.Conf;

@WebServlet("/notify")
public class NotifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public NotifyServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "GET, POST");
			JSONObject json = new JSONObject();
			json.put("generated", Calendar.getInstance().getTimeInMillis());
			response.setContentType("application/json");
			String action = request.getParameter("state");
			String code = request.getParameter("code");
			String error = request.getParameter("error");
			//TODO Authkey is disabled here. Not implemented.
			//String authkey = request.getParameter("authkey");
			HttpSession session = request.getSession(false);
			String sessionkey = (String) session.getAttribute("sessionkey");
			try {
				action.isEmpty();
			} catch (NullPointerException e){
				action = "register";
			}
			try {
				error.isEmpty();
			} catch (NullPointerException e){
				error = "unknown";
			}
			if (error == "access_denied"){
				json.put("status", -1);
				json.put("response", "Pushbullet Access Denied.");
				response.getWriter().println(json);
				response.getWriter().flush();
				response.flushBuffer();
				response.sendRedirect(Conf.homedash_url);
			}
			/*
			boolean auth = false;
			try {
				auth = HashKey.auth(authkey);
			} catch (Exception e){
				response.setStatus(400);
				json.put("status", -1);
				json.put("response", "Key syntax invaild.");
				response.getWriter().println(json);
				return;
			}
			*/
			boolean auth = true;
			if (!auth){
				response.setStatus(403);
				json.put("status", -1);
				json.put("response", "Wrong AuthKey!");
				response.getWriter().println(json);
				return;
			}
			switch (action){
			case "redirect":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					response.getWriter().println("<h1>Invaild Session</h1>");
					return;
				}
				String reredirecturl = Conf.homedash_url + ":" + Conf.api_port + "/api/notify?sessionkey=" + sessionkey;
				String enurl = URLEncoder.encode(reredirecturl, "UTF-8");
				response.sendRedirect("https://www.pushbullet.com/authorize?client_id=" + Conf.pushbullet_clientid
						+ "&redirect_uri=" + enurl + "&response_type=code&scope=everything");
				break;
			case "viewpbs":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String pbusername = PBServer.convertSessionKeyToUsername(sessionkey);
				int pbcindex = PBServer.getCodeIndex(pbusername);
				if (pbcindex == -1){
					json.put("response", "No client registered.");
					json.put("status", -2);
					response.getWriter().println(json);
					return;
				}
				String[] pbcode = PBServer.getPBCode(pbcindex);
				String maskedpbencode = "******" + pbcode[1].substring(5, 10) + "******";
				json.put("pbcode", maskedpbencode);
				json.put("status", 1);
				response.getWriter().println(json);
				return;
			case "removepbs":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String removemaskedpbencode = request.getParameter("mask");
				boolean donermvpbs = PBServer.removeByMask(removemaskedpbencode);
				if (donermvpbs){
					json.put("response", "PBCode Removed.");
					json.put("status", 1);
					response.getWriter().println(json);
					return;
				} else {
					json.put("response", "Could not remove.");
					json.put("status", -1);
					response.getWriter().println(json);
					return;
				}
			case "testnotify":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String testusername = PBServer.convertSessionKeyToUsername(sessionkey);
				if (NotifySchedule.getAmountOfSche(testusername) <= 0){
					json.put("response", "No schedules defined.");
					json.put("status", -1);
					response.getWriter().println(json);
					return;
				}
				NotifySchedule.getThread().triggerCheck(testusername);
				json.put("response", "Trigger checking NOW.");
				json.put("status", 1);
				response.getWriter().println(json);
				return;
			case "addnotify":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String schename = request.getParameter("schename");
				String trigger = request.getParameter("trigger");
				boolean timeenabled = Boolean.parseBoolean(request.getParameter("timeenabled"));
				String during = null;
				if (timeenabled){
					during = request.getParameter("during");
				}
				String username = PBServer.convertSessionKeyToUsername(sessionkey);
				boolean add = NotifySchedule.getThread().addNotify(username, schename, trigger, timeenabled, during);
				if (add){
					json.put("response", "Added notification.");
					json.put("status", 1);
					response.getWriter().println(json);
					return;
				}
				else
				{
					json.put("response", "Error occurred.");
					json.put("status", -1);
					response.getWriter().println(json);
					return;
				}
			case "removenotify":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String rmvschename = request.getParameter("schename");
				String rmvusername = PBServer.convertSessionKeyToUsername(sessionkey);
				if (rmvschename == null || rmvusername == null){
					json.put("response", "Missing parameters.");
					json.put("status", -1);
					json.put("generated", Calendar.getInstance().getTimeInMillis());
					response.getWriter().println(json);
					return;
				}
				boolean rmvdone = NotifySchedule.getThread().removeNotify(rmvusername, rmvschename);
			    if (rmvdone){
			    	json.put("response", "Removed notification.");
					json.put("status", 1);
					json.put("generated", Calendar.getInstance().getTimeInMillis());
					response.getWriter().println(json);
					return;
			    }
			    else {
			    	json.put("response", "Error occurred.");
					json.put("status", -1);
					json.put("generated", Calendar.getInstance().getTimeInMillis());
					response.getWriter().println(json);
					return;
			    }
			case "viewnotify":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String viewusername = PBServer.convertSessionKeyToUsername(sessionkey);
				int amount = NotifySchedule.getAmountOfSche(viewusername);
				json.put("amount", Integer.toString(amount));
				if (amount <= 0){
					json.put("response", "There is no notifications set.");
					json.put("status", -2);
					response.getWriter().println(json);
					return;
				}
				String[] viewnotdata = NotifySchedule.getUserSche(viewusername);
				json.put("sche", viewnotdata);
				json.put("username", viewusername);
				json.put("status", 1);
				json.put("response", "Searched Successfully.");
				response.getWriter().println(json);
				return;
			case "sepschecode":
				if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
					json.put("response", "Invaild session");
					json.put("session", false);
					json.put("status", -3);
					response.getWriter().println(json);
					return;
				}
				String specschecode = request.getParameter("schecode");
				String[] specschecodearr = NotifySchedule.getScheduleCode(specschecode);
				json.put("status", 1);
				json.put("schecode", specschecodearr);
				json.put("response", "Separated successfully.");
				response.getWriter().println(json);
				return;
			default:	
			case "register":
				response.setStatus(204);
				try {
					if (!SessionAuth.sesthread.isSessionAvailable(sessionkey)){
						response.setStatus(401);
						return;
					}
					String accesstoken = PBClient.convertCodeToAccessToken(code);
					boolean register = PBServer.registerViaSessionKey(sessionkey, accesstoken);
					if (register){
						PBClient.pushNote("Notification Relink", "Connected successfully.", accesstoken);
					} else {
						response.setStatus(403);
						return;
					}
				} catch (Exception e) {
					response.setStatus(400);
					return;
				}
				response.sendRedirect(Conf.homedash_url);
				break;
			}
		} catch (Exception e){
			response.setStatus(500);
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
