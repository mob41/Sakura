package com.github.mob41.sakura.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.sakura.hash.AesUtil;
import com.github.mob41.sakura.security.UserManager;

public class APIServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2121520229486327810L;
	
	private static final int STATUS_ERROR = -1;
	
	private static final int STATUS_SUCCESS = 0;

	@Override
	public void init(){
		
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PrintWriter respWriter = response.getWriter();
		
		response.setHeader("Allow", "POST");
		response.setHeader("Cache-Control", "No-Cache");
		response.setHeader("Connection", "Keep-Alive");
		response.setHeader("Content-Language", "en");
		response.setHeader("Server", "SakuraAPI Alpha");
		
		response.setContentType("application/json");
		
		JSONObject respJson = new JSONObject();
		
		respJson.put("generated", Calendar.getInstance().getTimeInMillis());
		
		String user = request.getParameter("u");
		String pass = request.getParameter("p");
		
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		
		if (session.isNew() || (user != null && pass != null)){
			session.setMaxInactiveInterval(360000);
			
			if ((user == null || user.isEmpty()) && (pass == null || pass.isEmpty())){
				badRequestEnd(respJson, respWriter, response);
				return;
			}
			
			boolean auth = false;
			
			if (auth){
				String _auth_randiv = AesUtil.random(128/8);
				String _auth_randsalt = AesUtil.random(128/8);
				String _auth_randpass = AesUtil.random(128/8);
				String _auth_reqpa = AesUtil.random(128/8);
				
				session.setAttribute("iv", _auth_randiv);
				session.setAttribute("salt", _auth_randsalt);
				session.setAttribute("pass", _auth_randpass);
				session.setAttribute("req", _auth_reqpa);
				
				respJson.put("2eklsa", _auth_randiv);
				respJson.put("dsASk2", _auth_randsalt);
				respJson.put("sd2BNf", _auth_randpass);
				respJson.put("xcnm02", _auth_reqpa);
				
				respJson.put("status", STATUS_SUCCESS);
				
				end(respJson, respWriter, response);
				return;
			} else {
				respJson.put("status", STATUS_ERROR);
				end(respJson, respWriter, response, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		
		String eniv = (String) session.getAttribute("iv");
		String ensalt = (String) session.getAttribute("salt");
		String enpass = (String) session.getAttribute("pass");
		String reqpa = (String) session.getAttribute("req");
		
		if (eniv == null || eniv.isEmpty() || ensalt == null || eniv.isEmpty() || enpass == null || enpass.isEmpty()){
			session.invalidate();
			
			JSONObject errorResp = new JSONObject();
			errorResp.put("generated", respJson.getLong("generated"));
			errorResp.put("status", STATUS_ERROR);
			errorResp.put("error", "Corrupted session");
			
			response.setHeader("Connection", "Close");
			
			end(errorResp, respWriter, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String reqEn = request.getParameter(reqpa);
		
		if (reqEn == null){
			badRequestEnd(respJson, respWriter, response);
			return;
		}
		
		AesUtil aesUtil = new AesUtil(128, 1000);
		String reqDe = null;
		try {
			reqDe = aesUtil.decrypt(ensalt, eniv, enpass, reqEn);
		} catch (Exception e){
			badRequestEnd(respJson, respWriter, response);
			return;
		}
		
		JSONObject reqJson = null;
		try {
			reqJson = new JSONObject(reqDe);
		} catch (JSONException e){
			badRequestEnd(respJson, respWriter, response);
			return;
		}
		
		final String reqJsonPara = "asdm2s";
		
		if (reqJson.isNull(reqJsonPara)){
			badRequestEnd(respJson, respWriter, response);
			return;
		}
		
		String req = reqJson.getString(reqJsonPara);
		
		if (req.equals("bkdf3d")){ //Logout
			session.invalidate();
			end(respJson, respWriter, response);
			return;
		} else if (req.equals("xznmk2")){ //Plugin
			
		}
	}
	
	private void badRequestEnd(JSONObject respJson, PrintWriter respWriter, HttpServletResponse response) throws IOException{
		respJson.put("status", STATUS_ERROR);
		
		end(respJson, respWriter, response, HttpServletResponse.SC_BAD_REQUEST);
		return;
	}
	
	private void end(JSONObject respJson, PrintWriter respWriter, HttpServletResponse response) throws IOException{
		end(respJson, respWriter, response, HttpServletResponse.SC_ACCEPTED);
	}
	
	private void end(JSONObject respJson, PrintWriter respWriter, HttpServletResponse response, int status) throws IOException{
		response.setStatus(status);
		respWriter.println(respJson.toString());
		respWriter.flush();
		respWriter.close();
	}
}
