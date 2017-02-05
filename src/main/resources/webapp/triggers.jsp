<%@ page import="java.util.List,java.net.URLEncoder,java.util.Iterator,java.util.Map,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.hash.AesUtil,com.github.mob41.sakura.misc.MiscKit,com.github.mob41.sakura.scene.SceneManager,com.github.mob41.sakura.scene.Scene,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.trigger.ParameterType,com.github.mob41.sakura.trigger.TriggerAssign,com.github.mob41.sakura.trigger.TriggerManager,com.github.mob41.sakura.trigger.Trigger" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

long start = System.currentTimeMillis();
String username = (String) session.getAttribute("username");
if (session == null || session.isNew() || username == null || !srv.getPermManager().isUserPermitted(username, "system.control")){
	String query = request.getQueryString();
	String path = request.getRequestURI().toString();
	String redirect = URLEncoder.encode(path + (query != null ? "?" + query : ""), "UTF-8");
	response.sendRedirect("login.jsp?redirect=" + redirect);
	return;
}

//TODO Encrypt request data, techinally isn't a GREAT protection, but only just hiding the code
//The code below is just a value encryption. Not a request encryption. Requires writing a javascript handling the data to be sent

	//Request processing
	
//Initialize AES toolkit
AesUtil aesUtil = new AesUtil(128, 1000);

//Get back values from server session
String _enc_val_iv = (String) session.getAttribute("_enc_val_iv");
String _enc_val_salt = (String) session.getAttribute("_enc_val_salt");
String _enc_val_pass = (String) session.getAttribute("_enc_val_pass");

//TODO Request encryption e.g.
//String _enc_req_iv = ...

String _para_trReq = (String) session.getAttribute("_para_trReq");
String _para_actReq = (String) session.getAttribute("_para_actReq");
String _para_regtrReq = (String) session.getAttribute("_para_regtrReq");
String _para_sceneReq = (String) session.getAttribute("_para_sceneReq");

String _act_para = (String) session.getAttribute("_act_para");
String _act_assign = (String) session.getAttribute("_act_assign");
String _act_unassign = (String) session.getAttribute("_act_unassign");

//Relieve values from parameters
String trReq = _para_trReq != null ? request.getParameter(_para_trReq) : null;
trReq = "none".equals(trReq) ? null : trReq;
String actReq = _para_actReq != null ? request.getParameter(_para_actReq) : null;
String regtrReq = _para_regtrReq != null ? request.getParameter(_para_regtrReq) : null;
String sceneReq = _para_sceneReq != null ? request.getParameter(_para_sceneReq) : null;

//Process requests
if (actReq != null){
	if (trReq != null && _enc_val_iv != null && _enc_val_salt != null && _enc_val_pass != null && actReq.equals(_act_para)){
		String detrReq = aesUtil.decrypt(_enc_val_salt, _enc_val_iv, _enc_val_pass, trReq);
		TriggerManager tm = srv.getTriggerManager();
		Map<String, TriggerAssign> tras = tm.getRunningTriggers();
		
		TriggerAssign tra = tras.get(detrReq);
		Trigger tr = tm.getRegisteredTrigger(tra.getTriggerName());
		
		if (tr != null){
			ParameterType[] types = tr.getParameterTypes();
			
			String valueReq;
			Object[] newPara = new Object[types.length];
			for (int i = 0; i < types.length; i++){
				if ((valueReq = request.getParameter(types[i].getName())) != null){
					if (types[i].getClassType() == ParameterType.INTEGER && MiscKit.isInteger(valueReq)){
						newPara[i] = Integer.parseInt(valueReq);
					} else if (types[i].getClassType() == ParameterType.STRING || types[i].getClassType() == ParameterType.SELECTIONS){
						newPara[i] = valueReq;
					}
				} else {
					newPara[i] = null;
				}
			}
			
			TriggerAssign newTra = new TriggerAssign(tra.getTriggerName(), tra.getSceneName(), newPara);
			tras.put(detrReq, newTra);
			tm.writeFile();
		}
	} else if (trReq != null && regtrReq != null && sceneReq != null && actReq.equals(_act_assign)){
		//TODO Encryption
		TriggerManager tm = srv.getTriggerManager();
		
		if (tm.isTriggerRegistered(regtrReq) && !tm.isAssignNameAssigned(trReq)){
			//TODO Further do error handling and post to alerts
			tm.addTrigger(trReq, regtrReq, sceneReq);
			tm.writeFile();
		}
	} else if (trReq != null && _enc_val_iv != null && _enc_val_salt != null && _enc_val_pass != null && actReq.equals(_act_unassign)){
		String detrReq = aesUtil.decrypt(_enc_val_salt, _enc_val_iv, _enc_val_pass, trReq);
		
		TriggerManager tm = srv.getTriggerManager();
		if (tm.isAssignNameAssigned(detrReq)){
			tm.removeAssignment(detrReq);
			tm.writeFile();
		}
	}
}

	// Re-randomize for another request

//Randomize values
_para_trReq = AesUtil.random(128/8);
_para_actReq = AesUtil.random(128/8);
_para_regtrReq = AesUtil.random(128/8);
_para_sceneReq = AesUtil.random(128/8);

String _oenc_val_iv = _enc_val_iv;
_enc_val_iv = AesUtil.random(128/8);

String _oenc_val_salt = _enc_val_salt;
_enc_val_salt = AesUtil.random(128/8);

String _oenc_val_pass = _enc_val_pass;
_enc_val_pass = AesUtil.random(1024/8);
			
_act_para = AesUtil.random(128/8);
_act_assign = AesUtil.random(128/8);
_act_unassign = AesUtil.random(128/8);

//Set server values
session.setAttribute("_para_trReq", _para_trReq);
session.setAttribute("_para_actReq", _para_actReq);
session.setAttribute("_para_regtrReq", _para_regtrReq);
session.setAttribute("_para_sceneReq", _para_sceneReq);

session.setAttribute("_enc_val_iv", _enc_val_iv);
session.setAttribute("_enc_val_salt", _enc_val_salt);
session.setAttribute("_enc_val_pass", _enc_val_pass);

session.setAttribute("_act_para", _act_para);
session.setAttribute("_act_assign", _act_assign);
session.setAttribute("_act_unassign", _act_unassign);

	// Dynamic web page variables

//Initialize variables
TriggerManager tm = srv.getTriggerManager();

List<Trigger> regts = tm.getAllTriggers();
Map<String, TriggerAssign> runningts = tm.getRunningTriggers();

String detrReq;
try {
	detrReq = trReq != null && _oenc_val_iv != null && _oenc_val_salt != null && _oenc_val_pass != null ? aesUtil.decrypt(_oenc_val_salt, _oenc_val_iv, _oenc_val_pass, trReq) : null;
} catch (Exception e){
	detrReq = null;
}

String entrReq;
try {
	entrReq = detrReq != null ? aesUtil.encrypt(_enc_val_salt, _enc_val_iv, _enc_val_pass, detrReq) : null;
} catch (Exception e){
	entrReq = null;
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
                    <h1 class="page-header">Triggers</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <form method="post" id="selform" role="form">
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-default">
            			<div class="panel-heading">
            				Running triggers management
            			</div>
            			<div class="panel-body">
            				<div class="form-group">
            					<label class="control-label">Select running trigger:</label>
            					<select name="<%= _para_trReq %>" class="form-control" onchange="$('#selform').submit();">
            						<option value="none">--- Select ---</option>
            					<%
            					Iterator<String> it = runningts.keySet().iterator();
            					String key;
            					while (it.hasNext()){
            						key = it.next();
            					%>
            						<option value="<%= aesUtil.encrypt(_enc_val_salt, _enc_val_iv, _enc_val_pass, key) %>" <% if (detrReq != null && detrReq.equals(key)){ %>selected<% } %>>"<%= key %> (<%= runningts.get(key).getTriggerName() %>)" triggers scene "<%= runningts.get(key).getSceneName() %>"</option>
            					<%
            					}
            					%>
            					</select>
            				</div>
            			</div>
            			<div class="panel-footer">
            				<input type="submit" class="btn btn-success" value="Select" />
            				<button type="button" class="btn btn-default" onclick="$('#addModal').modal();">Assign a trigger</button>
            				<button type="button" class="btn btn-danger <% if (!(detrReq != null && runningts.containsKey(detrReq))){ %>disabled<% } %>" <% if (detrReq != null && runningts.containsKey(detrReq)){ %>onclick="$('#removalModal').modal();"<% } %>>Unassign this trigger</button>
            			</div>
            		</div>
            	</div>
            </div>
            </form>
            
            <%
            if (detrReq != null && runningts.containsKey(detrReq)){
            	TriggerAssign tra = runningts.get(detrReq);
            	Trigger tr = tm.getRegisteredTrigger(tra.getTriggerName());
            	
            	if (tr != null){
            %>
            <form role="form" method="post">
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-info">
            			<div class="panel-heading">
            				Parameters
            			</div>
            			<div class="panel-body">
            				<%
            				ParameterType[] types = tr.getParameterTypes();
            				Object[] paras = tra.getParameters();
            				
            				if (types != null && types.length > 0){
                				if (types.length != paras.length || !MiscKit.isAllNotNull(paras)){
                    		%>
                    		<p class="text-danger">This trigger does not have enough / valid parameters. This trigger will be skipped.</p>
                    		<%
                    			}
            					for (int i = 0; i < types.length; i++){
            				%>
            					<% if (types[i].getClassType() == ParameterType.INTEGER || types[i].getClassType() == ParameterType.STRING){ %>
            				<div class="form-group<% if (paras.length <= i || paras[i] == null){ %> has-error<% } %>">
            					<label class="control-label"><%= types[i].getName() %> (<%= types[i].getClassType() == ParameterType.INTEGER ? "Integer" : "String" %>)</label>
            					<input type="text" name="<%= types[i].getName() %>" class="form-control" <%= types[i].getClassType() == ParameterType.INTEGER ? "onkeypress='return event.charCode >= 48 && event.charCode <= 57'" : "" %> value="<%= paras.length > i ? paras[i] : "" %>" />
            					<% } else if (types[i].getClassType() == ParameterType.SELECTIONS) { %>
            				<div class="form-group<% if (paras.length <= i || paras[i] == null){ %> has-error<% } %>">
            					<label class="control-label"><%= types[i].getName() %> (Selections)</label>
            					<select name="<%= types[i].getName() %>" class="form-control">
            						<option value="none">-- Select --</option>
            						<% 
            						String[] sel = types[i].getSelections();
            						if (sel != null){
            							for (int j = 0; j < sel.length; j++){
            						%>
            						<option value="<%= sel[j] %>" <% if (paras.length > i && paras[i] != null && paras[i].equals(sel[j])){ %>selected<% } %>><%= sel[j] %></option>
            						<%
            							}
            						} else {
            						%>
            						<option disabled>Error: null selections!</option>
            						<%
            						}
            						%>
            					</select>
            					<% } else { %>
            				<div class="form-group has-error">
            					<label class="control-label"><%= types[i].getName() %> (Type cannot be determined, disabled)</label>
            					<input type="text" class="form-control disabled" value="" />
            					<% } %>
            				</div>
            				<%
            					}
            				} else if (types == null) {
            				%>
            				<p class="text-danger">This trigger does not require any parameters. But it still returns a "null", instead of a 0 length array. It is still okay...</p>
            				<%
            				} else if (types.length == 0){
            				%>
            				<p>Does not require parameters.</p>
            				<%
            				}
            				%>
            			</div>
            			<div class="panel-footer">
            				<% if (types != null && types.length > 0){ %>
            				<%-- TODO Randomize hidden action value and parameter name, and encrypt form data, technically not so much protection, but sucks for n00b hackers --%>
            				<input type="hidden" name="<%= _para_actReq %>" value="<%= _act_para %>" />
            				<input type="hidden" name="<%= _para_trReq %>" value="<%= entrReq %>" />
            				<input type="submit" value="Save" class="btn btn-success" />
            				<input type="reset" value="Reset" class="btn btn-default" />
            				<% } %>
            			</div>
            		</div>
            	</div>
            </div>
            </form>
            <%
            	} else {
            %>
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-danger">
            			<div class="panel-heading">
            				<i class="fa fa-warning"></i> Broken trigger assignment
            			</div>
            			<div class="panel-body">
            				This running trigger's native trigger assignment cannot be parsed. The assigned trigger wasn't registered to the trigger manager. This trigger will be skipped.
            			</div>
            			<div class="panel-footer">
            			</div>
            		</div>
            	</div>
            </div>
            <%
            	}
            } 
            %>
            
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="alert alert-info">
            		    <p>This page is loaded using <b><%= System.currentTimeMillis() - start %> ms</b>, including encryption, decryption elapsed time.</p>
            		</div>
            	</div>
            </div>
        </div>
        <!-- /#page-wrapper -->
        
    </div>
    <!-- /#wrapper -->
    
    <!-- Modal -->
    <div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel" aria-hidden="true">
    	<div class="modal-dialog">
    		<div class="modal-content">
    		<form method="post">
    			<input type="hidden" name="<%= _para_actReq %>" value="<%= _act_assign %>" />
    			<div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                	<h4 class="modal-title" id="myModalLabel">Trigger assignment</h4>
                </div>
                <div class="modal-body">
                	<div class="form-group">
                		<label class="control-label">Assignment Name:</label>
                		<input type="text" name="<%= _para_trReq %>" class="form-control" />
                	</div>
                	<div class="form-group">
                		<label class="control-label">Select Trigger:</label>
                		<select class="form-control" name="<%= _para_regtrReq %>">
                			<%
                			List<Trigger> regtrs = tm.getAllTriggers();
                			Trigger tr;
                			for (int i = 0; i < regtrs.size(); i++){
                				tr = regtrs.get(i);
                			%>
                			<option value="<%= tr.getName() %>"><%= tr.getName() %></option>
                			<%
                			}
                			%>
                		</select>
                	</div>
                	<div class="form-group">
                		<label class="control-label">Triggers scene:</label>
                		<select class="form-control" name="<%= _para_sceneReq %>">
                			<%
                			Scene[] scenes = srv.getSceneManager().getScenes();
                			Scene scene;
                			for (int i = 0; i < scenes.length; i++){
                				scene = scenes[i];
                			%>
                			<option value="<%= scene.getName() %>"><%= scene.getName() %></option>
                			<%
                			}
                			%>
                		</select>
                	</div>
                </div>
                <div class="modal-footer">
                	<input type="submit" class="btn btn-success" value="Assign" />
                	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </form>
			</div>
            <!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
    <!-- /.modal -->
    
    <% if (detrReq != null && runningts.containsKey(detrReq)){ %>
    <!-- Modal -->
    <div class="modal fade" id="removalModal" tabindex="-1" role="dialog" aria-labelledby="removeTriggerModalLabel" aria-hidden="true">
    	<div class="modal-dialog">
    		<div class="modal-content">
    		<form method="post">
    			<input type="hidden" name="<%= _para_actReq %>" value="<%= _act_unassign %>" />
    			<input type="hidden" name="<%= _para_trReq %>" value="<%= entrReq %>" />
    			<div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                	<h4 class="modal-title" id="myModalLabel">Trigger unassignment</h4>
                </div>
                <div class="modal-body">
                	<p>Are you sure to unassign this trigger (<%= detrReq %>)?</p>
                </div>
                <div class="modal-footer">
                	<input type="submit" class="btn btn-danger" value="Unassign" />
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
    
</body>
</html>