<%@ page import="java.util.List,java.net.URLEncoder,java.util.Iterator,java.io.File,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.security.PermManager,org.apache.commons.fileupload.servlet.ServletFileUpload,org.apache.commons.fileupload.FileItem,org.apache.commons.fileupload.FileUploadException,org.apache.commons.fileupload.disk.DiskFileItemFactory,org.apache.commons.io.output.*" contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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

boolean runThis = false; //Disable firmware upload right now
boolean isMultipart = ServletFileUpload.isMultipartContent(request);
if (isMultipart && runThis){
	DiskFileItemFactory factory = new DiskFileItemFactory();
	factory.setSizeThreshold(4 * 1024);
	factory.setRepository(new File("/tmp"));
	
	ServletFileUpload upload = new ServletFileUpload(factory);
	upload.setSizeMax(50 * 1024);
	
	final String filePath = "/home/pi/sakura/fw";
	File file;
	
	try {
		List fileItems = upload.parseRequest(request);
		
	    Iterator i = fileItems.iterator();
	    
	    while (i.hasNext()){
	    	FileItem fi = (FileItem)i.next();
	    	if (!fi.isFormField()){
	    		String fieldName = fi.getFieldName();
	            String fileName = fi.getName();
	            String contentType = fi.getContentType();
	            boolean isInMemory = fi.isInMemory();
	            long sizeInBytes = fi.getSize();
	            // Write the file
	            if( fileName.lastIndexOf("\\") >= 0 ){
	               file = new File( filePath + 
	               fileName.substring( fileName.lastIndexOf("\\"))) ;
	            }else{
	               file = new File( filePath + 
	               fileName.substring(fileName.lastIndexOf("\\")+1)) ;
	            }
	            fi.write( file ) ;
	            System.out.println("Uploaded Filename: " + fileName);
	         }
	      }
	} catch (Exception e){
		System.out.println("Error when uploading firmware: " + e);
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
                    <h1 class="page-header">Firmware Upload</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="alert alert-info">
            			<i class="fa fa-info-circle"></i> Firmware upload was not implemented and it is very platform dependent. 
            		</div>
            	</div>
            </div>
            
            <div class="row">
            	<div class="col-lg-12">
            		<div class="panel panel-info">
            			<div class="panel-heading">
            				<i class="fa fa-download"></i> Upload firmware
            			</div>
            			<div class="panel-body">
            				<div class="row">
            					<div class="col-lg-12">
            						<div class="alert alert-danger">
            							<i class="fa fa-excalmation-circle"></i> <b>Warning:</b> A invalid/corrupt firmware can break the system. You can only upload *.skafw files. If unfortunately you break the system, you have to login to SSH and place a new JAR and web-app files to the running directory.
            						</div>
            					</div>
            				</div>
            				
            				<form method="post" enctype="multipart/form-data">
            					<div class="form-group">
            						<input type="file" name="file" size="50" accept=".skafw" />
            					</div>
            					<input type="submit" class="btn btn-success" value="Install" />
            				</form>
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