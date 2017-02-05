<%@ page import="java.util.List,java.net.URLEncoder,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.notification.NotificationManager,com.github.mob41.sakura.notification.Notification" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

String username = (String) session.getAttribute("username");
if (session == null || session.isNew() || username == null){
	String query = request.getQueryString();
	String path = request.getRequestURI().toString();
	String redirect = URLEncoder.encode(path + (query != null ? "?" + query : ""), "UTF-8");
	response.sendRedirect("login.jsp?redirect=" + redirect);
	return;
}
response.addHeader("Refresh", "30");

NotificationManager ntm = srv.getNotificationManager();
List<Notification> nts = ntm.getNotifications(username);

String dismissReq = request.getParameter("dismiss");
if (dismissReq != null){
	int index = ntm.getUIDIndex(username, dismissReq);
	if (index != -1){
		Notification gnt = nts.get(index);
		if (gnt.isRead()){
			ntm.removeNotification(username, index);
		} else {
			gnt.setAsRead();
		}
	}
}
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
                    <h1 class="page-header">Notifications</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <%
            Notification nt;
            if (nts != null && nts.size() > 0){
            	int rows = nts.size() / 3 + 1;
            	int tmp = 0;
            	for (int i = 0; i < rows; i++){
            %>
            <div class="row">
                <%
                	for (int j = 0; j < 3; j++){
                	if (i * 3 + j >= nts.size()){
                		break;
                	}
                	nt = nts.get(i * 3 + j);
                %>
                <div class="col-lg-4">
                    <div class="panel panel-<%= nt.isRead() ? "default" : "primary" %>">
                        <div class="panel-heading">
                            <%= nt.getTitle() %>
                        </div>
                        <div class="panel-body">
                            <p><%= nt.getMessage() %></p>
                        </div>
                        <div class="panel-footer">
                        	<form action="notifications.jsp" method="post">
                        		<input type="hidden" name="dismiss" value="<%= nt.getUID() %>" />
                            	<input type="submit" class="btn btn-default" value="<%= nt.isRead() ? "Remove" : "Dismiss" %>" />
                            </form>
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
            	<p>No notifications.</p>
            <%
            }
            %>
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
    
</body>
</html>