<%@ page import="org.json.JSONObject,java.net.URLEncoder,java.util.Arrays,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,java.util.List,java.util.Map,com.github.mob41.sakura.misc.MiscKit,com.github.mob41.sakura.scene.SceneProcessThread,com.github.mob41.sakura.action.ActionManager,com.github.mob41.sakura.action.ParameterType,com.github.mob41.sakura.action.Action,com.github.mob41.sakura.scene.SceneManager,com.github.mob41.sakura.scene.Scene" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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

SceneManager sm = srv.getSceneManager();
ActionManager am = srv.getActionManager();

String sceneReq = request.getParameter("scene");
if (request.getMethod().equals("POST")){
	String actionReq = request.getParameter("action");
	String typeReq = request.getParameter("type");
	String indexReq = request.getParameter("index");
	String plugActNameReq = request.getParameter("plugactname");
	String sysActNameReq = request.getParameter("sysactname");
	String plugReq = request.getParameter("plug");
	
	if (actionReq != null && sceneReq != null){
		if (actionReq.equals("addaction") && typeReq != null && sm.isSceneExist(sceneReq)){
			Scene scene = sm.getScene(sceneReq);
			
			if (scene != null){
				if (typeReq.equals("system") && sysActNameReq != null){
					Action action = am.getSystemAction(sysActNameReq);
					
					if (action != null){
						scene.addAction(action, new String[action.getParameterTypes() == null ? 0 : action.getParameterTypes().length]);
						sm.updateScene(sceneReq, scene);
					}
				} else if (typeReq.equals("plugin") && plugActNameReq != null && plugReq != null){
					Action action = am.getPluginAction(plugReq, plugActNameReq);
					
					if (action != null){
						scene.addAction(action, new String[0]);
						sm.updateScene(sceneReq, scene);
					}
				}
			}
		} else if (actionReq.equals("insertaction") && typeReq != null && indexReq != null && sm.isSceneExist(sceneReq)){ 
			int index = -1;
			
			try {
				index = Integer.parseInt(indexReq);
			} catch (NumberFormatException ignore){}
			
			if (index != -1){
				Scene scene = sm.getScene(sceneReq);
				
				if (scene != null){
					if (typeReq.equals("system") && sysActNameReq != null){
						Action action = am.getSystemAction(sysActNameReq);
						
						if (action != null){
							scene.addAction(index, action);
							sm.updateScene(sceneReq, scene);
						}
					} else if (typeReq.equals("plugin") && plugActNameReq != null && plugReq != null){
						Action action = am.getPluginAction(plugReq, plugActNameReq);
						
						if (action != null){
							scene.addAction(index, action);
							sm.updateScene(sceneReq, scene);
						}
					}
				}
			} 
		} else if (actionReq.equals("removeaction") && indexReq != null && sm.isSceneExist(sceneReq)){
			Scene scene = sm.getScene(sceneReq);
			
			int index = -1;
			
			try {
				index = Integer.parseInt(indexReq);
			} catch (NumberFormatException ignore){}
			
			if (index != -1){
				scene.removeAction(index);
				sm.updateScene(sceneReq, scene);
			}
		} else if (actionReq.equals("addscene")){
			if (!sm.isSceneExist(sceneReq)){
				Scene scene = new Scene(sceneReq);
				sm.addScene(scene);
				sm.writeFile();
			}
		} else if (actionReq.equals("removescene")){
			if (sm.isSceneExist(sceneReq)){
				sm.removeScene(sceneReq);
				sm.writeFile();
			}
		} else if (actionReq.equals("runscene")){
			if (sm.isSceneExist(sceneReq)){
				sm.runScene(sceneReq);
				sm.writeFile();
			}
		} else if (actionReq.equals("para")){
			if (sm.isSceneExist(sceneReq)){
				Scene scene = sm.getScene(sceneReq);
				
				if (scene != null){
					Action[] acts = scene.getActions();
					for (int i = 0; i < acts.length; i++){
						ParameterType[] pts = acts[i].getParameterTypes();
						String[] actArgs = scene.getActionArgs(i);
						if (pts == null){
							continue;
						}
						
						if (actArgs == null || actArgs.length != pts.length){
							actArgs = new String[pts.length];
						}
						
						for (int x = 0; x < pts.length; x++){
							String value = request.getParameter(i + "-" + pts[x].getName());
							if (value == null || value.equals("none")){
								continue;
							}
							actArgs[x] = value;
						}
						
						scene.setActionArgs(i, actArgs);
					}
					
					sm.updateScene(sceneReq, scene);
				}
			}
		}
	}
}

SceneProcessThread spt = sm.getProcessThread();

if (spt != null && spt.isRunning()){
	response.addHeader("Refresh", "2");
}

Scene[] scenes = sm.getScenes();
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
    
    <script>
    function updateSel(index){
    	var type = $("#typeSel" + index).val();
    	if (type == "system"){
    		document.getElementById("sysActSel" + index).style.display = "";
    		document.getElementById("plugActSel" + index).style.display = "none";
    	} else if (type == "plugin"){
    		document.getElementById("sysActSel" + index).style.display = "none";
    		document.getElementById("plugActSel" + index).style.display = "";
    	} else {
    		document.getElementById("sysActSel" + index).style.display = "none";
    		document.getElementById("plugActSel" + index).style.display = "none";
    	}
    }
    </script>
    
</head>

<body>

    <div id="wrapper">

        <jsp:include page="nav.jsp" flush="true" />

        <div id="page-wrapper">
        	<div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Scenes</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
                <div class="col-lg-12">
                <form role="form" method="get" id="sceneSel">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Scenes Management
                        </div>
                        <div class="panel-body">
                            	<div class="form-group">
                            		<label>Select scene:</label>
                            		<select name="scene" class="form-control" onchange="document.getElementById('sceneSel').submit();"><% for (int i = 0; i < scenes.length; i++){%>
                            			<option value="<%= scenes[i].getName() %>" <% if (scenes[i].getName().equals(sceneReq)){%>selected<%}%>><%= scenes[i].getName() %></option>
                                    <%}%></select>
                            	</div>
                        </div>
                        <div class="panel-footer">
                        	<input type="submit" class="btn btn-success" value="Select" />
                        	<button type="button" class="btn btn-default" onclick="$('#addSceneModal').modal();">Add new scene</button>
                        	<button type="button" class="btn btn-info" <% if (sceneReq != null && sm.isSceneExist(sceneReq) && !(spt != null && spt.isRunning())){ %>onclick="$('#runSceneModal').modal()"<% } else { %>disabled<% } %>>Run this scene</button>
                        	<button type="button" class="btn btn-danger" <% if (sceneReq != null && sm.isSceneExist(sceneReq) && !(spt != null && spt.isRunning())){ %>onclick="$('#removalModal').modal()"<% } else { %>disabled<% } %>>Remove this scene</button>
                        </div>
                    </div>
                    </form>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="alert alert-info">
            			<i class="fa fa-info-circle"></i> All the parameter values support dynamic variable syntax, i.e.: <code>#@=current-time=@#</code> shows the current time
            		</div>
            	</div>
            </div>
            
            <% if (sceneReq != null && sm.isSceneExist(sceneReq)) { %>
            <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-clock-o fa-fw"></i> Scene Timeline
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <ul class="timeline">
                            <% Scene scene = sm.getScene(sceneReq); Action[] actions = scene.getActions(); if (actions.length == 0) { %><li>
                                    <div class="timeline-badge info"><i class="fa fa-plus"></i>
                                    </div>
                                    <div class="timeline-panel">
                                        <div class="timeline-heading">
                                            <h4 class="timeline-title">Add new action</h4>
                                        </div>
                                        <div class="timeline-body">
                                            <form role="form" method="post">
                                            	<input type="hidden" name="action" value="addaction" />
                                            	<input type="hidden" name="scene" value="<%= sceneReq %>" />
                                            	<div class="form-group">
                                            		<label>Action type:</label>
                                            		<select name="type" id="typeSel" class="form-control" onchange="updateSel('')">
                                            			<option>-- Select type --</option>
                                            			<option value="system">System actions</option>
                                            			<option value="plugin">Plugin actions</option>
                                            		</select>
                                            	</div>
                                            	<div class="form-group" id="sysActSel" style="display:none">
                                            		<label>Select system action:</label>
                                            		<select name="sysactname" class="form-control">
                                            			<option>-- Select action --</option>
                                            			<%
                                            			Action action;
                                            			List<Action> sysActs = am.getAllSystemActions();
                                            			for (int i = 0; i < sysActs.size(); i++){
                                            				action = sysActs.get(i);
                                            			%>
                                            			<option value="<%= action.getName() %>"><%= action.getName() %></option>
                                            			<%
                                            			}
                                            			%>
                                            		</select>
                                            	</div>
                                            	<div class="form-group" id="plugActSel" style="display:none">
                                            		<label>Select plugin action:</label>
                                            		<select name="plugactname">
                                            			<option>-- Select action --</option>
                                            			<%
                                            			List<Action> plugActList;
                                            			Map<String, List<Action>> plugActs = am.getAllPluginActions();
                                            			String[] plugStrs = am.getActionsPluginNames();
                                            			for (int i = 0; i < plugStrs.length; i++){
                                            				plugActList = plugActs.get(plugStrs[i]);
                                            			%>
                                            			<option disabled>=== <%= plugStrs[i] %> ===</option>
                                            			<%
                                            				for (int j = 0; j < plugActList.size(); j++){
                                            					action = plugActList.get(j);
                                            			%>
                                            			<option value="<%= action.getName() %>"><%= action.getName() %></option>
                                            			<%
                                            				}
                                            			}
                                            			%>
                                            		</select>
                                            	</div>
                                            	<input type="submit" class="btn btn-success" value="Add" />
                                            </form>
                                        </div>
                                    </div>
                                </li>
                                <% } else { 
                                	Action action; 
                                	for (int i = 0; i < actions.length; i++){ 
                                		action = actions[i]; 
                                		if (action == null){
                                %>
                                <li <% if (i % 2 != 0){ %>class="timeline-inverted"<% } %>>
                                    <div class="timeline-badge danger"><i class="fa fa-exclamation"></i>
                                    </div>
                                    <div class="timeline-panel">
                                        <div class="timeline-heading">
                                            <h4 class="timeline-title">Parsing error: Index <%= i %></h4>
                                        </div>
                                        <div class="timeline-body">
                                        	<% JSONObject actionJson = sm.getSceneJSON(sceneReq).getJSONArray("actions").getJSONObject(i); %>
                                            <p>The following <%= actionJson.getString("type") %> action: "<%= actionJson.getString("name") %>" <% if (actionJson.getString("type").equals("plugin")){ %>for plugin "<%= actionJson.getString("pluginName")%>" <% } %> cannot be parsed by the action manager. Probably not registered by the system/plugin itself. This action will be bypassed until it is registered properly.</p>
                                        	<hr>
                                        	<button class="btn btn-danger" onclick="document.getElementById('removeactionform').submit();">Remove this action</button>
                                        </div>
                                    </div>
                                    <form id="removeactionform" method="post">
                                    	<input type="hidden" name="action" value="removeaction" />
                                    	<input type="hidden" name="index" value="<%= i %>" />
                                    	<input type="hidden" name="scene" value="<%= sceneReq %>" />
                                    </form>
                                </li>
                                <%
                                			continue;
                                		}
                                %>
                                <li <% if (i % 2 != 0){ %>class="timeline-inverted"<% } %>>
                                    <div class="timeline-badge"><i class="fa fa-code"></i>
                                    </div>
                                    <div class="timeline-panel">
                                        <div class="timeline-heading">
                                            <h4 class="timeline-title"><%= action.getName() %></h4>
                                        </div>
                                        <div class="timeline-body">
                                        	<% if (spt != null && spt.isRunning() && spt.getSceneName().equals(sceneReq) && i == spt.getRunningActionIndex()){ %>
                                        	<div class="row">
                                        		<div class="col-lg-12">
                                        			<div class="alert alert-success">
                                        				<i class="fa fa-gears"></i> This action is still running
                                        			</div>
                                        		</div>
                                        	</div>
                                        	<% } %>
                                            <%
                                            ParameterType[] types = action.getParameterTypes(); 
                                            %>
                                            <form role="form" method="post" action="scenes.jsp">
                                            <%
                                            if (types != null){
                                            	Object[] actArgs = scene.getActionArgs(i);
                                            	ParameterType type;
                                            	boolean text = false;
                                           		for (int j = 0; j < types.length; j++){
                                           			if (!text && (!MiscKit.isAllNotNull(actArgs) || actArgs.length != types.length)){
                                           				text = true;
                                           	%>
                                           		<p class="text-danger">No/invalid parameter(s) saved! This action will be skipped!</p>
                                           	<%
                                           			}
                                           			type = types[j];
                                           			if (type.getClassType() == ParameterType.STRING ||
                                           					type.getClassType() == ParameterType.INTEGER){
                                           	%>
                                            	<div class="form-group<% if (actArgs.length <= j){ %> has-error<% } %>">
                                            		<label class="control-label"><%= type.getName() %> (<%= type.getClassType() == ParameterType.INTEGER ? "Integer" : "String" %>)</label>
                                            		<input type="text" name="<%= i + "-" + type.getName() %>" class="form-control" value="<%= actArgs.length > j ? actArgs[j] : "" %>" />
                                        		</div>
                                        	<%
                                           			} else if (type.getClassType() == ParameterType.SELECTIONS){
                                           	%>
                                           		<div class="form-group<% if (actArgs.length <= j || actArgs[j] == null){ %> has-error<% } %>">
                                            		<label class="control-label"><%= type.getName() %> (Selection)</label>
                                            		<select name="<%= i + "-" + type.getName() %>" class="form-control">
                                            			<option value="none">--- Select ---</option>
                                            			<%
                                            			String[] selections = type.getSelections();
                                            			if (selections != null){
                                            				for (int x = 0; x < selections.length; x++){
                                            			%>
                                            			<option value="<%= selections[x] %>" <% if (actArgs.length > j && actArgs[j] != null && actArgs[j].equals(selections[x])){ %>selected<% } %>><%= selections[x] %></option>
                                            			<%
                                            				}
                                            			} 
                                            			%>
                                            		</select>
                                        		</div>
                                           	<%
                                           			}
                                           		}
                                           	} else {
                                        	%>
                                        	<p>No parameters required.</p>
                                        	<% } %>
                                            <hr>
                                            <% if (types != null && types.length != 0){ %>
                                            <input type="hidden" name="index" value="<%= i %>" />
                                            <input type="hidden" name="action" value="para" />
                                            <input type="hidden" name="scene" value="<%= sceneReq %>" />
                                            <input type="submit" class="btn btn-success" value="Save"/>
                                            <input type="reset" class="btn btn-default" value="Reset" />
                                            <% } %>
                                            <div class="btn-group">
                                                <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-gear"></i>  <span class="caret"></span>
                                                </button>
                                                <ul class="dropdown-menu" role="menu">
                                                    <li><a href="#" onclick="$('#insertActionUpModal<%= i %>').modal();">Add action upstairs</a>
                                                    </li>
                                                    <li><a href="#" onclick="$('#insertActionDownModal<%= i %>').modal();">Add action downstairs</a>
                                                    </li>
                                                    <li class="divider"></li>
                                                    <li><a href="#" onclick="document.getElementById('removeactionform<%= i %>').submit();">Remove this action</a>
                                                    </li>
                                                </ul>
                                            </div>
                                            </form>
                                            <!-- Modal -->
    										<div class="modal fade" id="insertActionUpModal<%= i %>" tabindex="-1" role="dialog" aria-labelledby="insertActionUpModal<%= i %>Label" aria-hidden="true">
    											<div class="modal-dialog">
    												<div class="modal-content">
    												<form method="post">
    													<input type="hidden" name="scene" value="<%= sceneReq %>" />
    													<input type="hidden" name="index" value="<%= i %>" />
    													<input type="hidden" name="action" value="insertaction" />
    													<div class="modal-header">
                											<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                											<h4 class="modal-title" id="myModalLabel">Insert action</h4>
                										</div>
                										<div class="modal-body">
                												<div class="form-group">
                													<label class="control-label">Select action type:</label>
                                            						<select name="type" id="typeSelu<%= i %>" class="form-control" onchange="updateSel('u<%= i %>')">
                                            							<option value="none">-- Select --</option>
                                            							<option value="system">System actions</option>
                                            							<option value="plugin">Plugin actions</option>
                                            						</select>
                                            					</div>
                                            					<div class="form-group" id="sysActSelu<%= i %>" style="display:none">
                                            						<label class="control-label">Select action:</label>
                                            						<select name="sysactname" class="form-control">
                                            							<option>-- Select action --</option>
                                            							<%
                                            							List<Action> sysActs = am.getAllSystemActions();
                                            							for (int x = 0; x < sysActs.size(); x++){
                                            								action = sysActs.get(x);
                                            							%>
                                            							<option value="<%= action.getName() %>"><%= action.getName() %></option>
                                            							<%
                                            							}
                                            							%>
                                            						</select>
                                            					</div>
                                            					<div class="form-group" id="plugActSelu<%= i %>" style="display:none">
                                            						<label class="control-label">Select action:</label>
                                            						<select name="plugactname" class="form-control">
                                            							<option value="none">-- Select --</option>
                                            							<%
                                            							List<Action> plugActList;
                                            							Map<String, List<Action>> plugActs = am.getAllPluginActions();
                                            							String[] plugStrs = am.getActionsPluginNames();
                                            							for (int x = 0; x < plugStrs.length; x++){
                                            								plugActList = plugActs.get(plugStrs[x]);
                                            							%>
                                            							<option disabled>=== <%= plugStrs[x] %> ===</option>
                                            							<%
                                            								for (int j = 0; j < plugActList.size(); j++){
                                            									action = plugActList.get(j);
                                            							%>
                                            							<option value="<%= action.getName() %>"><%= action.getName() %></option>
                                            							<%
                                            								}
                                            							}
                                            							%>
                                            						</select>
                												</div>
                										</div>
                										<div class="modal-footer">
                											<input type="submit" class="btn btn-success" value="Insert" />
                											<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                										</div>
            										</form>
													</div>
            										<!-- /.modal-content -->
												</div>
												<!-- /.modal-dialog -->
											</div>
    										<!-- /.modal -->
    										<!-- Modal -->
    										<div class="modal fade" id="insertActionDownModal<%= i %>" tabindex="-1" role="dialog" aria-labelledby="insertActionDownModal<%= i %>Label" aria-hidden="true">
    											<div class="modal-dialog">
    												<div class="modal-content">
    												<form method="post">
    													<input type="hidden" name="scene" value="<%= sceneReq %>" />
    													<input type="hidden" name="index" value="<%= i + 1 %>" />
    													<input type="hidden" name="action" value="insertaction" />
    													<div class="modal-header">
                											<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                											<h4 class="modal-title" id="myModalLabel">Insert action</h4>
                										</div>
                										<div class="modal-body">
                												<div class="form-group">
                													<label class="control-label">Select action type:</label>
                                            						<select name="type" id="typeSeld<%= i %>" class="form-control" onchange="updateSel('d<%= i %>')">
                                            							<option value="none">-- Select --</option>
                                            							<option value="system">System actions</option>
                                            							<option value="plugin">Plugin actions</option>
                                            						</select>
                                            					</div>
                                            					<div class="form-group" id="sysActSeld<%= i %>" style="display:none">
                                            						<label class="control-label">Select action:</label>
                                            						<select name="sysactname" class="form-control">
                                            							<option>-- Select action --</option>
                                            							<%
                                            							for (int x = 0; x < sysActs.size(); x++){
                                            								action = sysActs.get(x);
                                            							%>
                                            							<option value="<%= action.getName() %>"><%= action.getName() %></option>
                                            							<%
                                            							}
                                            							%>
                                            						</select>
                                            					</div>
                                            					<div class="form-group" id="plugActSeld<%= i %>" style="display:none">
                                            						<label class="control-label">Select action:</label>
                                            						<select name="plugactname" class="form-control">
                                            							<option value="none">-- Select --</option>
                                            							<%
                                            							for (int x = 0; x < plugStrs.length; x++){
                                            								plugActList = plugActs.get(plugStrs[x]);
                                            							%>
                                            							<option disabled>=== <%= plugStrs[x] %> ===</option>
                                            							<%
                                            								for (int j = 0; j < plugActList.size(); j++){
                                            									action = plugActList.get(j);
                                            							%>
                                            							<option value="<%= action.getName() %>"><%= action.getName() %></option>
                                            							<%
                                            								}
                                            							}
                                            							%>
                                            						</select>
                                            					</div>
                										</div>
                										<div class="modal-footer">
                											<input type="submit" class="btn btn-success" value="Insert" />
                											<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                										</div>
            										</form>
													</div>
            										<!-- /.modal-content -->
												</div>
												<!-- /.modal-dialog -->
											</div>
    										<!-- /.modal -->
                                            <form id="removeactionform<%= i %>" method="post">
                                            	<input type="hidden" name="action" value="removeaction" />
                                            	<input type="hidden" name="index" value="<%= i %>" />
                                            	<input type="hidden" name="scene" value="<%= sceneReq %>" />
                                            </form>
                                        </div>
                                    </div>
                                </li>
                                <% } %>
                            </ul>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                    <% } } %>
                </div>
        </div>
        <!-- /#page-wrapper -->
        
    </div>
    <!-- /#wrapper -->
    
    <% if (sceneReq != null && sm.isSceneExist(sceneReq)){ %>
    <!-- Modal -->
    <div class="modal fade" id="removalModal" tabindex="-1" role="dialog" aria-labelledby="removeSecneModalLabel" aria-hidden="true">
    	<div class="modal-dialog">
    		<div class="modal-content">
    		<form method="post">
    			<input type="hidden" name="action" value="removescene" />
    			<input type="hidden" name="scene" value="<%= sceneReq %>" />
    			<div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                	<h4 class="modal-title" id="myModalLabel">Scene removal</h4>
                </div>
                <div class="modal-body">
                	<p>Are you sure to remove this scene (<%= sceneReq %>)?</p>
                </div>
                <div class="modal-footer">
                	<input type="submit" class="btn btn-danger" value="Remove" />
                	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </form>
			</div>
            <!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
    <!-- /.modal -->
    
    <!-- Modal -->
    <div class="modal fade" id="runSceneModal" tabindex="-1" role="dialog" aria-labelledby="runSceneModalLabel" aria-hidden="true">
    	<div class="modal-dialog">
    		<div class="modal-content">
    		<form method="post">
    			<input type="hidden" name="action" value="runscene" />
    			<input type="hidden" name="scene" value="<%= sceneReq %>" />
    			<div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                	<h4 class="modal-title" id="myModalLabel">Run scene</h4>
                </div>
                <div class="modal-body">
                	<p>Are you sure to run this scene (<%= sceneReq %>)?</p>
                </div>
                <div class="modal-footer">
                	<input type="submit" class="btn btn-success" value="Run" />
                	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </form>
			</div>
            <!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
    <!-- /.modal -->
    <% } %>
    
    <!-- Modal -->
    <div class="modal fade" id="addSceneModal" tabindex="-1" role="dialog" aria-labelledby="addSecneModalLabel" aria-hidden="true">
    	<div class="modal-dialog">
    		<div class="modal-content">
    		<form method="post" action="scenes.jsp">
    			<input type="hidden" name="action" value="addscene" />
    			<div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                	<h4 class="modal-title" id="myModalLabel">Add new scene</h4>
                </div>
                <div class="modal-body">
                	<div class="form-group">
                		<label>Scene Name:</label>
                		<input type="text" name="scene" class="form-control" autofocus required />
                	</div>
                </div>
                <div class="modal-footer">
                	<input type="submit" class="btn btn-success" value="Add" />
                	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </form>
			</div>
            <!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
    <!-- /.modal -->
    
</body>
</html>