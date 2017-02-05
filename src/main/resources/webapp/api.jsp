<%@ page import="org.json.JSONObject,org.json.JSONException,java.util.Calendar,com.github.mob41.sakura.api.SakuraAPI,com.github.mob41.sakura.api.SakuraServer" contentType="text/html; charset=utf-8" language="java" errorPage="" %><%
if (!request.getMethod().equals("POST")){
	return;
}
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);
response.setContentType("application/json");

String apiTypeReq = request.getParameter("type");
boolean noEnc = apiTypeReq != null && apiTypeReq.equals("dev");

String req = request.getParameter("request");
JSONObject json = null;
if (req != null){
	try {
		json = new JSONObject(req);
	} catch (JSONException e){
		json = null;
	}
}

JSONObject result;
try {
	if (noEnc){
		System.out.println("NoEnc");
		result = srv.getEasyAPI().request(json);
	} else {
		System.out.println("DefEnc");
		result = srv.getAPI().request(json);
	}
} catch (Exception e){
	result = new JSONObject();
	result.put("generated", Calendar.getInstance().getTimeInMillis());
	result.put("result", SakuraAPI.RESULT_SERVER_ERROR);
	result.put("description", e);
}
%><%= result.toString() %>