package com.mob41.sakura.servlets.old;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/func")
public class FuncServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public FuncServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		Calendar cal = Calendar.getInstance();
		response.setContentType("application/json");
		json.put("generated", cal.getTimeInMillis());
		json.put("response", "GET requests are rejected.");
		json.put("status", "-1");
		response.getWriter().println(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "POST");
			JSONObject json = new JSONObject();
			Calendar cal = Calendar.getInstance();
			String action = request.getParameter("action");
			response.setContentType("application/json");
			json.put("generated", cal.getTimeInMillis());
			switch (action){
			case "encrypt":
				String string = request.getParameter("string");
				String salt = request.getParameter("salt");
				String hash;
				try {
					hash = HashKey.encrypt(string, salt);
					json.put("hash", hash);
					json.put("status", "1");
					response.getWriter().println(json);
					return;
				} catch (Exception e) {
					json.put("response", "Could not encrypt the String and Salt provided.");
					json.put("status", "-1");
					response.getWriter().println(json);
					return;
				}
			case "decrypt":
				String remoteHash = request.getParameter("hash");
				String remoteSalt = request.getParameter("salt");
				String returnStr;
				try {
					returnStr = HashKey.decrypt(remoteHash, remoteSalt);
					json.put("output", returnStr);
					json.put("status", "1");
					response.getWriter().println(json);
					return;
				} catch (Exception e) {
					json.put("response", "Could not decrypt from the Hash and Salt provided.");
					json.put("status", "-1");
					response.getWriter().println(json);
					return;
				}
			case "getRandomSalt":
				String encodedRandSalt = HashKey.getRandomSalt();
				json.put("output", encodedRandSalt);
				json.put("status", "1");
				response.getWriter().println(json);
				return;
			case "getexchangekey":
				returnStr = HashKey.getExchangeKey();
				json.put("output", returnStr);
				json.put("status", "1");
				response.getWriter().println(json);
				return;
			case "getsalt":
				String saltpwd = request.getParameter("saltpwd");
				if (HashKey.cleartypeSaltAuth(saltpwd)){
					json.put("output", HashKey.y);
					json.put("status", "1");
					response.getWriter().println(json);
					return;			
				}
				else
				{
					json.put("response", "The password is incorrect.");
					json.put("status", "-1");
					response.getWriter().println(json);
					return;		
				}
			default:
				json.put("response", "Unknown function.");
				json.put("status", "-1");
				response.getWriter().println(json);
				return;
			}
		} catch (NullPointerException e){
			JSONObject json = new JSONObject();
			Calendar cal = Calendar.getInstance();
			String action = request.getParameter("action");
			response.setContentType("application/json");
			json.put("generated", cal.getTimeInMillis());
			json.put("response", "Probably Wrong Parameters.");
			json.put("exception", "NullPointerException");
			json.put("status", "-1");
			response.getWriter().println(json);
			return;
		} catch (Exception e){
			JSONObject json = new JSONObject();
			Calendar cal = Calendar.getInstance();
			String action = request.getParameter("action");
			response.setContentType("application/json");
			json.put("generated", cal.getTimeInMillis());
			json.put("response", "Error occurred");
			json.put("exception", e.getMessage());
			json.put("status", "-1");
			response.getWriter().println(json);
			return;
		}
	}

}
