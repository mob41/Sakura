<%@ page import="java.util.List,java.net.URLEncoder,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.appliance.ApplianceManager,com.github.mob41.sakura.appliance.Appliance" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

String username = (String) session.getAttribute("username");
if (session == null || session.isNew() || username == null || !srv.getPermManager().isUserPermitted(username, "system.control")){
	String query = request.getQueryString();
	String path = request.getRequestURI().toString();
	System.out.println(path);
	System.out.println(query);
	String redirect = URLEncoder.encode(path + (query != null ? "?" + query : ""), "UTF-8");
	response.sendRedirect("login.jsp?redirect=" + redirect);
	return;
}

ApplianceManager lm = srv.getApplianceManager();

String applianceReq = request.getParameter("name");
String statusReq = request.getParameter("status");
if (applianceReq != null && statusReq != null && (statusReq.equals("on") || statusReq.equals("off")) && lm.isApplianceExist(applianceReq)){
	if (statusReq.equals("on")){
		lm.getAppliance(applianceReq).turnOn(srv);
	} else if (statusReq.equals("off")) {
		lm.getAppliance(applianceReq).turnOff(srv);
	}
}

List<Appliance> appliances = lm.getAppliances();
%>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="A web user interface for Sakura Home Automation System">
<meta name="author" content="mob41">
<title>Sakura</title>

	<!-- Bootstrap Core CSS -->
    <link href="bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="bower_components/metisMenu/dist/metisMenu.min.css" rel="stylesheet">

    <!-- Timeline CSS -->
    <link href="dist/css/timeline.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="bower_components/morrisjs/morris.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

	<!-- jQuery -->
    <script src="bower_components/jquery/dist/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="bower_components/metisMenu/dist/metisMenu.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="bower_components/raphael/raphael-min.js"></script>
    <script src="bower_components/morrisjs/morris.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="dist/js/sb-admin-2.js"></script>
    
</head>

<body>

    <div id="wrapper">

        <jsp:include page="nav.jsp" flush="true" />

        <div id="page-wrapper">
        	<div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Appliances</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <%
            if (appliances != null && appliances.size() > 0){
            	int rows = appliances.size() / 3 + 1;
            	for (int j = 0; j < rows; j++){
            %>
            <div class="row">
            	<%
            	Appliance appliance;
            	for (int i = 0; i < 3; i++){
            		if (j * 3 + i >= appliances.size()){
            			break;
            		}
            		appliance = appliances.get(j * 3 + i);
            	%>
                <div class="col-lg-4">
                	<div class="panel panel-default">
                		<div class="panel-heading">
                			Appliance: <%= appliance.getName() %>
                		</div>
                		
                		<div class="panel-body">
                				<p>Status: <% if (appliance.isTurnedOn()){ %><span class="text-success"><b>On<% } else { %><span class="text-danger"><b>Off<% } %></b></span></p>
                				<p>Power use: <%= appliance.getPowerUseWatts() %> W (<%= appliance.getPowerUseKiloWatts() %> kW)</p>
                				<p>Power usage source: <%= appliance.getPowerUseCalcSource() %></p>
                				<button type="button" class="btn btn-success" onclick="window.location='appliances.jsp?name=<%= appliance.getName() %>&status=on'">On</button>
                				<button type="button" class="btn btn-danger" onclick="window.location='appliances.jsp?name=<%= appliance.getName() %>&status=off'">Off</button>
                		</div>
                		
                		<div class="panel-footer">
                		</div>
                	</div>
                </div>
                <!-- /.col-lg-4 -->
                <%
            	}
                %>
            </div>
            <!-- /.row -->
            <%
            	}
            } else {
            %>
            <p>No appliances are registered currently.</p>
            <%
            }
            %>
        </div>
        <!-- /#page-wrapper -->
        
    </div>
    <!-- /#wrapper -->
    
</body>
</html>