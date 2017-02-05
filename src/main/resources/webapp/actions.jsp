<%@ page import="org.json.JSONObject,java.net.URLEncoder,java.util.Map,java.util.List,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.action.ActionManager,com.github.mob41.sakura.action.Action,com.github.mob41.sakura.action.ParameterType" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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

ActionManager am = srv.getActionManager();

String typeReq = request.getParameter("type");
String cmdReq = request.getParameter("cmd");
String actReq = request.getParameter("act");

if (cmdReq != null && typeReq != null && actReq != null){
	if (cmdReq.equals("unregister")){
		if (typeReq.equals("system")){
			am.unregisterSystemAction(actReq);
		} //else if (typeReq.equals("plugin")){
		//}
	} else if (cmdReq.equals("run")){
		if (typeReq.equals("system")){
			Action act = am.getSystemAction(actReq);
			
			if (act != null){
				ParameterType[] types = act.getParameterTypes();
				
				Object[] args = null;
				
				if (types != null && types.length != 0){
					System.out.println("TODO: Relieve parameters for running actions with arguments");
					out.println("<p><span color=\"red\"><b>Your request is rejected, </b></span> because the function is not implemented. <b>TODO: Relieve parameters for running actions with arguments</b></p>");/*
					args = new Object[types.length];
					
					for (int i = 0; i < types.length; i++){
						final ParameterType type = types[i];
						final String paraName = type.getName();
						
						String req = request.getParameter(paraName);
						
						if (req == null){
							return;
						}
						
						switch (type.getClassType()){
						case ParameterType.INTEGER:
						case ParameterType.STRING:
						case ParameterType.SELECTIONS:
						}
					}
					*/
				} else {
					act.run(args);
				}
			}
		} //else if (typeReq.equals("plugin")){
		//}
	}
}

List<Action> sysActs = am.getAllSystemActions();
Map<String, List<Action>> plugActs = am.getAllPluginActions();
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
                    <h1 class="page-header">Actions Management</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
                <div class="col-lg-12">
                	<div class="alert alert-danger">
                		<b>Warning:</b> Modifying, unregistering actions may break the scenes system. Do not modify unless you know what are you doing.
                	</div>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Registered actions
                        </div>
                        <!-- .panel-heading -->
                        <div class="panel-body">
                            <div class="panel-group" id="mainAccordion">
                                <div class="panel panel-default">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">
                                            <a data-toggle="collapse" data-parent="#mainAccordion" href="#systemActions">System actions</a>
                                        </h4>
                                    </div>
                                    <div id="systemActions" class="panel-collapse collapse">
                                        <div class="panel-body">
                            				<div class="panel-group" id="accordion">
                            				<%
                            				Action sysAct;
                            				for (int i = 0; i < sysActs.size(); i++){
                            					sysAct = sysActs.get(i);
                            					
                            					String actName = sysAct.getName();
                            					String _actName = actName.trim().toLowerCase().replaceAll("\\s", "_");
                            				%>
                                				<div class="panel panel-default">
                                    				<div class="panel-heading">
                                        				<h4 class="panel-title">
                                            				<a data-toggle="collapse" data-parent="#accordion" href="#<%= _actName %>"><%= actName %></a>
                                        				</h4>
                                    				</div>
                                    				<div id="<%= _actName %>" class="panel-collapse collapse">
                                        				<div class="panel-body">
                                        					<form role="form" method="post">
                                        						<input type="hidden" name="cmd" value="run" />
                                        						<input type="hidden" name="type" value="system" />
                                        						<input type="hidden" name="act" value="<%= actName %>" />
                                        						<input type="submit" class="btn btn-info" value="Run action" />
                                        					</form>
                                        					<form role="form" method="post">
                                        						<input type="hidden" name="cmd" value="unregister" />
                                        						<input type="hidden" name="type" value="system" />
                                        						<input type="hidden" name="act" value="<%= actName %>" />
                                        						<input type="submit" class="btn btn-danger" value="Unregister" />
                                        					</form>
                                        				</div>
                                    				</div>
                                				</div>
                                			<%
                            				}
                                			%>
                                        	</div>
                                    	</div>
                                	</div>
                                	<div class="panel panel-default">
                                    	<div class="panel-heading">
                                        	<h4 class="panel-title">
                                            	<a data-toggle="collapse" data-parent="#mainAccordion" href="#pluginActions">Plugin actions</a>
                                        	</h4>
                                    	</div>
                                    	<div id="pluginActions" class="panel-collapse collapse">
                                        	<div class="panel-body">
                                        	</div>
                                    	</div>
                                </div>
                            </div>
                        </div>
                        <!-- .panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
        </div>
        <!-- /#page-wrapper -->
        
    </div>
    <!-- /#wrapper -->
    
</body>
</html>