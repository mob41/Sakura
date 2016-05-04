package com.mob41.sakura.servlets.old;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       //TODO Not implemented. Admin UI
    public AdminServlet() {
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
		request.getParameter("username");
		request.getParameter("password");
		request.getParameter("");
	}

}
