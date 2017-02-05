<%@ page import="java.util.List,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.notification.AlertManager,com.github.mob41.sakura.notification.Alert" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

response.addHeader("Refresh", "30");

String usernameatt = (String) session.getAttribute("username");

boolean dismissAllow = usernameatt == null ? false : srv.getPermManager().isUserPermitted(usernameatt, "system.alerts.dismiss");

AlertManager alm = srv.getAlertManager();

String dismissReq = request.getParameter("dismiss");
if (dismissReq != null && dismissAllow){
	int index = -1;
	try {
		index = Integer.parseInt(dismissReq);
	} catch (NumberFormatException ignore){}
	
	if (index != -1){
		alm.removeAlert(index, true);
	}
}

List<Alert> als = alm.getAlerts(true);
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
                    <h1 class="page-header">Alerts</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <%
            Alert al;
            if (als != null && als.size() > 0){
            	int rows = als.size() / 3 + 1;
            	int tmp = 0;
            	for (int i = 0; i < als.size(); i++){
            %>
            <div class="row">
                <%
                	for (int j = 0; j < 3; j++){
                	if (i * 3 + j >= als.size()){
                		break;
                	}
                	al = als.get(i * 3 + j);
                %>
                <div class="col-lg-4">
                    <div class="panel panel-<%= al.getFAStatusStr() %>">
                        <div class="panel-heading">
                            <%= al.getTime().getTime().toString() %>
                        </div>
                        <div class="panel-body">
                            <p><%= al.getMessage() %></p>
                        </div>
                        <div class="panel-footer">
                        <% if (dismissAllow) { %>
                        	<form action="alerts.jsp" method="post">
                        		<input type="hidden" name="dismiss" value="<%= i * 3 + j %>" />
                            	<input type="submit" class="btn btn-default" value="Dismiss" />
                            </form>
                        <% } else { %>
                        <div class="tooltip-dismiss">
                        	<button type="button" class="btn btn-default disabled" data-toggle="tooltip" data-placement="right" title="You don't have permission to do this.">Dismiss</button>
                        </div>
                        <% } %>
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
            <p>No alerts.</p>
            <%
            }
            %>
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
    
    <% if (!dismissAllow){ %>
    <script>
    $('.tooltip-dismiss').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    })
    </script>
    <% } %>
</body>
</html>