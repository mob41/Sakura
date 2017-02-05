<%@ page import="java.util.List,java.net.URLEncoder,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.plugin.Plugin,com.github.mob41.sakura.plugin.PluginManager" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

String username = (String) session.getAttribute("username");
if (session == null || session.isNew() || username == null || !srv.getPermManager().isUserPermitted(username, "system.admin")){
	String query = request.getQueryString();
	String path = request.getRequestURI().toString();
	String redirect = URLEncoder.encode(path + (query != null ? "?" + query : ""), "UTF-8");
	response.sendRedirect("login.jsp?redirect=" + redirect);
	return;
}

PluginManager pm = srv.getPluginManager();
List<Plugin> pml = pm.getPlugins();
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
                    <h1 class="page-header">Plugins</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-default">
            			<div class="panel-heading">
            				Plugin Manager
            			</div>
            			<div class="panel-body">
            				<div class="row">
            					<div class="col-lg-12">
            						<div class="alert alert-info">
            							<i class="fa fa-info-circle"></i> Notice: If just "Load all plugins" is very dirty, may duplicate initialization objects that registered by them. Still implementing reload plugins, and <code>unload()</code> method for plugins. By now, please reload by restarting the system manually.
            						</div>
            					</div>
            				</div>
            				<input type="button" class="btn btn-info" value="Reload All Plugins" disabled />
            			</div>
            		</div>
            	</div>
            </div>
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-default">
            			<div class="panel-heading">
            				Plugin settings / management
            			</div>
            			<div class="panel-body">
            				<div class="panel-group" id="mainAccordion">
            					<%
            					Plugin plugin;
            					for (int i = 0; i < pml.size(); i++){
            						plugin = pml.get(i);
            					%>
                                <div class="panel panel-default">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">
                                            <a data-toggle="collapse" data-parent="#mainAccordion" href="#<%= plugin.getPluginDescription().getName().trim() %>"><%= plugin.getPluginDescription().getName() %></a>
                                        </h4>
                                    </div>
                                    <div id="<%= plugin.getPluginDescription().getName().trim() %>" class="panel-collapse collapse">
                                        <div class="panel-body">
                                        	<div class="row">
                                        		<div class="col-lg-12">
            										<div class="alert alert-info">
            											<i class="fa fa-info-circle"></i> Currently, the SakuraAPI does not provide a <code>unload()</code> method for plugins. But, whatever, it is not important in this stage. So, it is not implemented.
            										</div>
            									</div>
                                        	</div>
                                        	<form role="form">
                                        		<div class="form-group">
                                        			
                                        		</div>
                                        	</form>
                                        	<input type="button" class="btn btn-danger" value="Unload" disabled />
                                    	</div>
                                	</div>
                                </div>
                                <%
                                }
                                %>
                            </div>
            			</div>
            		</div>
            	</div>
            </div>
        </div>
        <!-- /#page-wrapper -->
        
    </div>
    <!-- /#wrapper -->
    
</body>
</html>