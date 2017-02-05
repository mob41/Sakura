<%@ page import="java.util.List,java.net.URLEncoder,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.power.PowerRecord,com.github.mob41.sakura.power.PowerUse,com.github.mob41.sakura.power.PowerManager,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.power.PowerManager" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

String username = (String) session.getAttribute("username");
if (session == null || session.isNew() || username == null || !srv.getPermManager().isUserPermitted(username, "system.control")){
	String query = request.getQueryString();
	String path = request.getRequestURI().toString();
	String redirect = URLEncoder.encode(path + (query != null ? "?" + query : ""), "UTF-8");
	response.sendRedirect("login.jsp?redirect=" + redirect);
	return;
}

List<PowerRecord> records = srv.getPowerManager().getPowerRecords();
String[] recordStrs = srv.getPowerManager().getTotalPowerRecordsNames();
PowerRecord currRecord = records.get(records.size() - 1);
String[] currRecordStrs = currRecord.getPowerUseNames();
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
                    <h1 class="page-header">Power Usage</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-bar-chart-o fa-fw"></i> Appliances on
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
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-8">
            		<div class="panel panel-default">
                        <div class="panel-heading">
                        	Last record (per 1 hour) appliances on
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                        	<div class="table-responsive">
                                <table class="table table-bordered table-striped">
                                    <thead>
                                        <tr>
                                            <th>
                                                Power Use name
                                            </th>
                                            <th>
                                            	In use
                                            </th>
                                            <th>
                                                Power (W)
                                            </th>
                                            <th>
                                                Power (kW)
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody><% PowerUse powerUse; for (int i = 0; i < currRecordStrs.length; i++){ powerUse = currRecord.getPowerUses()[i]; %>
                                    	<tr>
                                    		<td><%= currRecordStrs[i] %></td>
                                    		<td><%= powerUse.inUse() ? "Yes" : "No" %></td>
                                    		<td><%= powerUse.getWattsPerHour() %> W</td>
                                    		<td><%= powerUse.getWattsPerHour() / 1000 %> kW</td>
                                    	</tr>
                                    <% } %></tbody>
                                </table>
                            </div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
            	</div>
            	<!-- /.col-lg-8 -->
            	<div class="col-lg-4">
            		<div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-bar-chart-o fa-fw"></i> Pie
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
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
</body>
</html>