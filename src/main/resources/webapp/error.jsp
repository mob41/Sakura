<%@ page isErrorPage="true" import="com.github.mob41.sakura.misc.MiscKit,java.io.PrintWriter,java.io.StringWriter" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%
String errorReq = request.getParameter("error");
if (errorReq == null || !MiscKit.isInteger(errorReq)){
	response.sendRedirect("index.jsp");
	return;
}

int errorCode = Integer.parseInt(errorReq);
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
    <link href="/bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="/bower_components/metisMenu/dist/metisMenu.min.css" rel="stylesheet">

    <!-- Timeline CSS -->
    <link href="/dist/css/timeline.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="/dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="/bower_components/morrisjs/morris.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="/bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

	<!-- jQuery -->
    <script src="/bower_components/jquery/dist/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="/bower_components/metisMenu/dist/metisMenu.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="/bower_components/raphael/raphael-min.js"></script>
    <script src="/bower_components/morrisjs/morris.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="/dist/js/sb-admin-2.js"></script>
    
</head>

<body>

    <div id="wrapper">

        <jsp:include page="/nav.jsp" flush="true" />

        <div id="page-wrapper">
        	<div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header"><% if (errorCode == 404){ %>Page Not Found<% } else if (errorCode == 403) { %>Forbidden<% } else if (errorCode == 500){ %>Server Error<% } %></h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-danger">
            			<div class="panel-heading">
            				<i class="fa fa-exclamation-circle"></i> <b>Error</b>
            			</div>
            			<div class="panel-body">
            				<% if (errorCode == 404){ %>
            				<p>The page that you are trying to access cannot be found. I am so sorry for that. If you believe this is an error, please contact with the developer.</p>
            				<% } else if (errorCode == 403){ %>
            				<p>You don't have enough permissions to access this page. I am so sorry for that. If you believe this is an error, please contact with the developer.</p>
            				<% } else if (errorCode == 500){ %>
            				<p>The system has just encountered a server error. The stack trace has been logged. Please contact the developer immediately.</p>
            				<pre><%
            					StringWriter stringWriter = null;
            					if (exception != null){
            						stringWriter = new StringWriter();
                    				
                					PrintWriter printWriter = new PrintWriter(stringWriter);
                					exception.printStackTrace(printWriter);
                					
                					printWriter.close();
                					stringWriter.close();
            					}
            				%><%= stringWriter != null ? stringWriter : "Could not receive stacktrace." %></pre>
            				<% } else { %>
            				<p>Unknown error.</p>
            				<% } %>
            			</div>
            		</div>
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