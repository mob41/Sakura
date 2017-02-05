<%@ page import="java.util.List,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.power.PowerManager,com.github.mob41.sakura.power.PowerUse,com.github.mob41.sakura.power.PowerRecord,com.github.mob41.sakura.notification.AlertManager,com.github.mob41.sakura.notification.Alert" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

List<Alert> als = srv.getAlertManager().getAlerts(true);
List<PowerRecord> records = srv.getPowerManager().getPowerRecords();
String[] recordStrs = srv.getPowerManager().getTotalPowerRecordsNames();
PowerRecord currRecord = records.get(records.size() - 1);
String[] currRecordStrs = currRecord.getPowerUseNames();
response.addHeader("Refresh", "30");

String logoutVal = request.getParameter("logout");
if (logoutVal != null && logoutVal.equals("")){
	session.setAttribute("username", null);
} else {
	//TODO Remove this before production! This will expose the admin account!
	//TODO Do not use USERNAME as username session attr! use hash
	//session.setAttribute("username", "admin");
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
    
    <!-- Generated chart data -->
    <script>
    $(function(){
    	Morris.Donut({
            element: 'power-use-chart',
            data: [<% for (int i = 0; i < currRecordStrs.length; i++){ %>{
                label: '<%= currRecordStrs[i] %>',
                value: <%= (float) currRecord.getPowerUses()[i].getWattsPerHour() / 1000 %>
            }<% if (i != currRecordStrs.length - 1){ %>,<% } } %>],
            resize: true
        });
    	
    	Morris.Area({
            element: 'appliances-on-chart',
            data: [<% PowerRecord rec; for (int i = 0; i < records.size(); i++){ rec = records.get(i);%>{
            	period: '<%= rec.getFormattedTime() %>',<% for (int j = 0; j < recordStrs.length; j++) {%>
            	'<%= recordStrs[j].trim() %>': <%= (float) rec.getPowerUseWattsPerHour(recordStrs[j]) / (float) 1000 %><% if (j != recordStrs.length - 1){ %>,<% } %><%}%>
            	}<% if (i != records.size() - 1) { %>,<% } } %>
            ],
            xkey: 'period',
            ykeys: [<% for (int i = 0; i < recordStrs.length; i++) { %>
            	'<%= recordStrs[i].trim() %>'<% if (i != recordStrs.length - 1){ %>,<% } } %>
            ],
            labels: [<% for (int i = 0; i < recordStrs.length; i++) { %>
            	'<%= recordStrs[i].trim() %>'<% if (i != recordStrs.length - 1){ %>,<% } } %>
           	],
            pointSize: 2,
            hideHover: 'auto',
            resize: true
        });
    })
    </script>

</head>

<body>

    <div id="wrapper">

        <jsp:include page="nav.jsp" flush="true" />

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Dashboard</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
        	
        	<div class="row">
            	<div class="col-lg-12">
                	<div class="alert alert-warning">
                		<b>Experimental:</b> This system is still in early development stage. An 500 server error may occur as unexpected. If once occurred, copy error log / stack trace to a text file and report it to the developer. Thanks.
             		</div>
            	</div>
            	<!-- /.col-lg-12 -->
        	</div>
        	<!-- /.row -->
        
            <div class="row">
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-yellow">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-bolt fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge"><%= srv.getPowerManager().getCurrentPowerUseKiloWatts() %></div>
                                    <div>kW/h Power Use</div>
                                </div>
                            </div>
                        </div>
                        <a href="poweruse.jsp">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-desktop fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge"><%= srv.getPowerManager().getInUses().length %></div>
                                    <div>Appliance(s) on</div>
                                </div>
                            </div>
                        </div>
                        <a href="appliances.jsp">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-green">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-cogs fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">0</div>
                                    <div>Scene(s)</div>
                                </div>
                            </div>
                        </div>
                        <a href="scenes.jsp">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-red">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-warning fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge"><%= als.size() %></div>
                                    <div>Alert(s)</div>
                                </div>
                            </div>
                        </div>
                        <a href="alerts.jsp">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-bar-chart-o fa-fw"></i> Appliances on (Alpha)
                            <div class="pull-right">
                                <div class="btn-group">
                                    <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        Actions
                                        <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu pull-right" role="menu">
                                        <li><a href="#">Action</a>
                                        </li>
                                        <li><a href="#">Another action</a>
                                        </li>
                                        <li><a href="#">Something else here</a>
                                        </li>
                                        <li class="divider"></li>
                                        <li><a href="#">Separated link</a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div id="appliances-on-chart"></div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-8 -->
                <div class="col-lg-4">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-bell fa-fw"></i> Notifications Panel
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="list-group">
                            <% 
                            if (als.size() > 0){
                            	for (Alert alert : als) { 
                            %>
                                <a href="#" class="list-group-item">
                                    <i class="fa fa-<%= alert.getFAIconStr() %> fa-fw"></i> <%= alert.getMessage() %>
                                    <span class="pull-right text-muted small"><em><%= alert.getFormattedTime() %></em>
                                    </span>
                                </a>
                            <%
                            	}
                            } else {
                            %>
                            	<a href="#" class="list-group-item">
                                    <i class="fa fa-check-circle fa-fw"></i> No alerts
                                    <span class="pull-right text-muted small"><em>---</em>
                                    </span>
                                </a>
                            <% } %>
                            </div>
                            <!-- /.list-group -->
                            <a href="#" class="btn btn-default btn-block">View All Alerts</a>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-bar-chart-o fa-fw"></i> Power use (Estimated)
                        </div>
                        <div class="panel-body">
                            <div id="power-use-chart"></div>
                            <a href="#" class="btn btn-default btn-block">View Details</a>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-4 -->
            </div>
            <!-- /.row -->
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
    
</body>
</html>