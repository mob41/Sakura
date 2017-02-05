<%@ page import="com.github.mob41.sakura.hash.AesUtil,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.UserManager,com.github.mob41.sakura.security.User" contentType="text/html; charset=utf-8" language="java" errorPage="" %>

<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

String usernameatt = (String) session.getAttribute("username");
if (usernameatt != null){
	response.sendRedirect("index.jsp");
	return;
}

String loginRedirect = request.getParameter("redirect");
System.out.println("[" + loginRedirect + "]");
%>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Sakura - Login</title>

    <!-- Bootstrap Core CSS -->
    <link href="../bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="../bower_components/metisMenu/dist/metisMenu.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="../dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="../bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    
    <!-- jQuery -->
    <script src="../bower_components/jquery/dist/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="../bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="../bower_components/metisMenu/dist/metisMenu.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="../dist/js/sb-admin-2.js"></script>

</head>

<body>

	<%
	boolean redirect = false;
	boolean showModal = false;
	String title = "Error occurred";
	String error = "Unknown Error";
	String reuserpara = (String) session.getAttribute("userpara");
	String repasspara = (String) session.getAttribute("passpara");
	if (reuserpara != null && repasspara != null){
		String reuserval = request.getParameter(reuserpara);
		String repassval = request.getParameter(repasspara);
		if (reuserval != null && repassval != null) {
			if (request.getMethod().equals("POST")){
				String reveripara = (String) session.getAttribute("veripara");
				String reverivalold = (String) session.getAttribute("verival");
				if (reveripara != null && reverivalold != null){
					String reverival = request.getParameter(reveripara);
					if (reverival != null && reverivalold.equals(reverival)){
						boolean auth = srv.getUserManager().authenticate(reuserval, repassval);
						if (!auth){
							title = "Login unsuccessful";
							error = "Incorrect username or password.";
							showModal = true;
						} else {
							session.setAttribute("username", reuserval);
							response.sendRedirect(loginRedirect != null ? loginRedirect : "index.jsp");
						}
					} else {
						error = "Verification failed. Another login page is opened with this session.";
						session.setAttribute("veripara", null);
						session.setAttribute("verival", null);
						showModal = true;
					}
				}
			} else {
				title = "Security Error";
				error = "Client is not using POST to send login information!";
				showModal = true;
				redirect = true;
			}
		}
	}
	%>
	
	<% if (redirect) { %>
	<meta http-equiv="Refresh" content="2;url=login.jsp">
	<% } %>
	
	<% if (showModal){ %>
	<!-- Modal -->
    <div class="modal fade" id="modal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
    	<div class="modal-dialog">
    		<div class="modal-content">
    			<div class="modal-header">
                	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                	<h4 class="modal-title" id="myModalLabel"><%= title %></h4>
                </div>
                <div class="modal-body">
                	<%= error %>
                </div>
                <div class="modal-footer">
                	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
			</div>
            <!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
    <!-- /.modal -->
    <script>
    $('#modal').modal('show');
	</script>
	<% } %>

    <div class="container">
        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title"><% if (loginRedirect == null){ %><button type="button" class="btn btn-default btn-circle" onclick="window.location='index.jsp'"><i class="fa fa-arrow-left"></i></button>	Login<% } else { %>Login to continue<% } %></h3>
                    </div>
                    <%
                    String userpara = AesUtil.random(2048/8);
                    String passpara = AesUtil.random(2048/8);
                    String veripara = AesUtil.random(2048/8);
                    String verival = AesUtil.random(2048/8);
                    session.setAttribute("userpara", userpara);
                    session.setAttribute("passpara", passpara);
                    session.setAttribute("veripara", veripara);
                    session.setAttribute("verival", verival);
                    %>
                    <div class="panel-body">
                        <form role="form" method="post">
                            <fieldset>
                                <div class="form-group">
                                    <input class="form-control" placeholder="Username" name="<%= userpara %>" type="text" autofocus required>
                                </div>
                                <div class="form-group">
                                    <input class="form-control" required placeholder="Password" name="<%= passpara %>" type="password" value="" required>
                                </div>
                                <input type="hidden" name="<%= veripara %>" value="<%= verival %>" />
                                <div class="checkbox">
                                    <label>
                                        <input name="remember" type="checkbox" value="Remember Me">Remember Me
                                    </label>
                                </div>
                                <!-- Change this to a button or input when using this as a form -->
                                <input type="submit" class="btn btn-lg btn-success btn-block" value="Login" />
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
</body>

</html>
