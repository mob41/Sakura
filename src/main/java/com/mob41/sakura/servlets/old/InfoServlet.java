package com.mob41.sakura.servlets.old;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.rpi.ha.ann.AnnounceMem;
import com.rpi.ha.scene.SceneSave;
import com.rpi.ha.ui.UI;
import com.rpi.ha.widget.HKOweather;

@WebServlet("/control")
public class InfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InfoServlet.class.getName());
       
    public InfoServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST");
		logger.info("A device (" + request.getRemoteAddr() + ") connected to INFO service.");
		if (request.getParameter("json") == null){
			logger.info("The device (" + request.getRemoteAddr() + ") had nothing to do. Redirecting...");
			response.sendRedirect("/");
			response.setContentType("text/plain");
			response.getWriter().println("Home Automation System Web UI - by Anthony Law");
			return;
		}
		response.setContentType("application/json");
		JSONObject json = new JSONObject();
		Calendar cal = Calendar.getInstance();
		if (!UI.UIstarted){
			json.put("response", "UI is not ready");
			json.put("generated", cal.getTimeInMillis());
			json.put("status", "-1");
			logger.info("The device (" + request.getRemoteAddr() + ") is disconnecting");
			response.getWriter().println(json);
			return;
		}
		int rowcount = UI.busArrTimeTable.getModel().getRowCount();
		int colcount = UI.busArrTimeTable.getModel().getColumnCount();
		String[][] data = new String[rowcount][colcount];
		int i;
		int j;
		for (i = 0; i < rowcount; i++){
			for (j = 0; j < colcount; j++){
				data[i][j] = (String) UI.busArrTimeTable.getModel().getValueAt(i, j);
			}
		}
		
		json.put("ann", AnnounceMem.getAllData());
		json.put("anns", AnnounceMem.getAmountOfAnnouncement());
		
		json.put("busarrives", UI.busArrTimeTable.getModel().getRowCount());
		json.put("stime", cal.getTime());
		json.put("generated", cal.getTimeInMillis());
		json.put("busarrive", data);
		json.put("temperature", HKOweather.getTemp(23));
		json.put("weathericon", HKOweather.getWeatherImageURL());
		logger.info("The device (" + request.getRemoteAddr() + ") is disconnecting");
		response.getWriter().println(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
