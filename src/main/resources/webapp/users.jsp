<%@ page import="java.util.List,java.net.URLEncoder,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.security.PermNode,com.github.mob41.sakura.security.UserManager,com.github.mob41.sakura.security.User" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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

String nameReq = request.getParameter("name");
String nodeReq = request.getParameter("node");
String actReq = request.getParameter("action");

PermManager pm = srv.getPermManager();
UserManager um = srv.getUserManager();

if (nameReq != null && actReq != null){
	if (um.isUsernameExist(nameReq)){
		if (nodeReq != null && actReq.equals("removeperm")){
			pm.disallowUser(nameReq, nodeReq);
			pm.writeFile();
		} else if (nodeReq != null && actReq.equals("addperm")){
			pm.allowUser(nameReq, nodeReq);
			pm.writeFile();
		}
	}
}

List<User> users = um.getUsersList();
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
                    <h1 class="page-header">User Management</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="alert alert-info">
            			<i class="fa fa-info-circle"></i> For security reasons, adding users, changing password, removing users are <b>not allowed</b> to be done in web. Password-related stuff will be allowed when the client to server encryption system is completed and working.
            		</div>
            	</div>
            </div>
            
            <div class="row">
            	<div class="col-lg-12">
            		<form role="form" method="post" id="usersSel">
            			<div class="panel panel-default">
            				<div class="panel-heading">
            					User management
            				</div>
            				<div class="panel-body">
            					<div class="form-group">
            						<label class="control-label">Select user:</label>
            						<select name="name" class="form-control" onchange="$('#usersSel').submit();">
            							<option value="none">--- Select ---</option><% User user; for (int i = 0; i < users.size(); i++){ user = users.get(i); %>
            							<option value="<%= user.getUsername() %>" <% if (nameReq != null && nameReq.equals(user.getUsername())){ %>selected<% } %>><%= user.getUsername() %></option>
            						<% } %></select>
            					</div>
            				</div>
            				<div class="panel-footer">
            					<input type="submit" class="btn btn-success" value="Select" />
            					<input type="button" class="btn btn-default disabled" <%-- onclick="$('#addUserModal').modal();" --%> value="Add new user" />
            					<input type="button" class="btn btn-danger disabled" value="Remove this user" />
            				</div>
            			</div>
            		</form>
            	</div>
            </div>
            <!-- /.row -->
            
            <% if (nameReq != null && um.isUsernameExist(nameReq)){ %>
            <div class="row">
            	<div class="col-lg-8">
            		<div class="panel panel-default">
            			<div class="panel-heading">
            				User self-permissions (<b>not</b> inherited from group)
            			</div>
            			<div class="panel-body">
            				<div class="table-responsive">
            					<table class="table table-bordered table-striped">
            						<thead>
            							<tr>
            								<th>Permission Node</th>
            								<th>Action</th>
            							</tr>
            						</thead>
            						<tbody>
            							<%
            							PermNode[] nodes = pm.getUser(nameReq).getSelfPermissions();
            							for (int i = 0; i < nodes.length; i++){
            							%>
            							<tr>
            								<td><%= nodes[i].getAlias() %></td>
            								<td>
            									<form role="form" method="post">
            										<input type="hidden" name="name" value="<%= nameReq %>" />
            										<input type="hidden" name="action" value="removeperm" />
            										<input type="hidden" name="node" value="<%= nodes[i].getAlias() %>" />
            										<input type="submit" class="btn btn-danger" value="Remove" />
            									</form>
            								</td>
            							</tr>
            							<%
            							}
            							%>
            						</tbody>
            					</table>
            				</div>
            				<hr />
            				<form role="form" method="post">
            					<input type="hidden" name="name" value="<%= nameReq %>" />
            					<input type="hidden" name="action" value="addperm" />
            					<div class="form-group">
            						<label class="control-label">Node to be added:</label>
            						<input type="text" name="node" class="form-control" />
            					</div>
            					<input type="submit" class="btn btn-success" value="Add node" />
            					<input type="reset" class="btn btn-default" value="Reset" />
            				</form>
            			</div>
            			<div class="panel-footer">
            			</div>
            		</div>
            	</div>
            </div>
            <!-- /.row -->
            <% } %>
            
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
    
</body>
</html>