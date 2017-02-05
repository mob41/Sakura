<%@ page import="java.util.List,com.github.mob41.sakura.api.SakuraServer,com.github.mob41.sakura.hash.AesUtil,com.github.mob41.sakura.security.PermManager,com.github.mob41.sakura.page.CustomPage,com.github.mob41.sakura.page.CustomPageManager,com.github.mob41.sakura.notification.AlertManager,com.github.mob41.sakura.notification.Alert,com.github.mob41.sakura.notification.NotificationManager,com.github.mob41.sakura.notification.Notification" %>
<%
SakuraServer srv = (SakuraServer) application.getAttribute(SakuraServer.WEB_APP_SERVER_ATTRIBUTE);

String username = (String) session.getAttribute("username");

CustomPage[] pages = srv.getCustomPageManager().getPages();
%>
		<!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.jsp">Sakura</a>
            </div>
            <!-- /.navbar-header -->

            <ul class="nav navbar-top-links navbar-right">
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-envelope fa-fw"></i>  <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-messages">
                    	<% if (session == null || session.isNew() || session.getAttribute("username") == null) {%>
                    	<li>
                            <a href="login.jsp">
                                <div>
                                    <strong>Login in</strong>
                                    <span class="pull-right text-muted">
                                        <em>---</em>
                                    </span>
                                </div>
                                <div>Please login before using the notification system.</div>
                            </a>
                        </li>
                        <% } else { %>
                        <% 
                        	Notification nt;
                        	List<Notification> ntl = srv.getNotificationManager().getNotifications((String) session.getAttribute("username"));
                        	if (ntl == null || ntl.size() < 1){
                        %>
                        <li>
                            <a href="#">
                                <div>
                                    <strong>No notifications</strong>
                                    <span class="pull-right text-muted">
                                        <em>---</em>
                                    </span>
                                </div>
                                <div>Woo hoo! No notifications!</div>
                            </a>
                        </li>
                        <%
                        	} else {
                        		for (int i = 0; i < ntl.size(); i++){
                        		nt = ntl.get(i);
                        %>
                    	<li>
                            <a href="#">
                                <div>
                                    <strong><%= nt.getTitle() %></strong>
                                    <span class="pull-right text-muted">
                                        <em><%= nt.getTime().getTime().toString() %></em>
                                    </span>
                                </div>
                                <div><%= nt.getMessage() %></div>
                            </a>
                        </li>
                        <% 		if (i != ntl.size() - 1) {%>
                        <li class="divider"></li>
                        <% 		} %>
                        <% 	 	} %>
                        <% 	 } %>
                        <li class="divider"></li>
                        <li>
                            <a class="text-center" href="notifications.jsp">
                                <strong>Read All Messages</strong>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </li>
                        <% } %>
                    </ul>
                    <!-- /.dropdown-messages -->
                </li>
                <!-- /.dropdown -->
                <li class="dropdown">
                		<%
                    	Alert al;
                    	List<Alert> als = srv.getAlertManager().getAlerts();
                		%>
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-bell<% if (als != null && als.size() != 0){ %>-o<% } %> fa-fw"></i>  <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-alerts">
                    	<%
                    	if (als != null && als.size() != 0){
                    		for (int i = 0; i < als.size() && i < 5; i++){
                    			al = als.get(i);
                    	%>
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-<%= al.getFAIconStr() %> fa-fw"></i> <%= al.getMessage() %>
                                    <span class="pull-right text-muted small"><%= al.getFormattedTime() %></span>
                                </div>
                            </a>
                        </li>
                        <% 		if (i != als.size() - 1) { %>
                        <li class="divider"></li>
                        <%
                        		}
                    		}
                    	} else {
                        %>
                        <li>
                            <a href="#">
                                <div>
                                    <i class="fa fa-comment fa-fw"></i> No alerts
                                    <span class="pull-right text-muted small">---</span>
                                </div>
                            </a>
                        </li>
                        <% } %>
                        <li>
                            <a class="text-center" href="alerts.jsp">
                                <strong>See All Alerts</strong>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </li>
                    </ul>
                    <!-- /.dropdown-alerts -->
                </li>
                <!-- /.dropdown -->
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-user fa-fw"></i>  <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-user"><% if (session == null || session.isNew() || session.getAttribute("username") == null) {%>
                    	<li><a href="login.jsp"><i class="fa fa-sign-in fa-fw"></i> Login</a>
                        </li>
                    <% } else { %>
                        <li><a href="userprofile.jsp"><i class="fa fa-user fa-fw"></i> User Profile</a>
                        </li>
                        <li><a href="usersettings.jsp"><i class="fa fa-gear fa-fw"></i> Settings</a>
                        </li>
                        <li class="divider"></li>
                        <li><a href="index.jsp?logout"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                        </li><% } %>
                    </ul>
                    <!-- /.dropdown-user -->
                </li>
                <!-- /.dropdown -->
            </ul>
            <!-- /.navbar-top-links -->

            <div class="navbar-default sidebar" role="navigation">
                <div class="sidebar-nav navbar-collapse">
                    <ul class="nav" id="side-menu">
                        <li class="sidebar-search">
                            <div class="input-group custom-search-form">
                                <input type="text" class="form-control" placeholder="Search...">
                                <span class="input-group-btn">
                                <button class="btn btn-default" type="button">
                                    <i class="fa fa-search"></i>
                                </button>
                            </span>
                            </div>
                            <!-- /input-group -->
                        </li>
                        <li>
                            <a href="index.jsp"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
                        </li>
                        <% if (session != null && session.getAttribute("username") != null){ %>
                        <li>
                            <a href="notifications.jsp"><i class="fa fa-info-circle fa-fw"></i> Notifications</a>
                        </li>
                        <% } %>
                        <li>
                            <a href="alerts.jsp"><i class="fa fa-warning fa-fw"></i> Alerts</a>
                        </li>
                        <% if (username != null && srv.getPermManager().isUserPermitted(username, "system.control")){ %>
                        <li>
                            <a href="scenes.jsp"><i class="fa fa-list-alt fa-fw"></i> Scenes</a>
                        </li>
                        <li>
                            <a href="triggers.jsp"><i class="fa fa-chain fa-fw"></i> Triggers</a>
                        </li>
                        <li>
                            <a href="appliances.jsp"><i class="fa fa-desktop fa-fw"></i> Appliances</a>
                        </li>
                        <li>
                            <a href="poweruse.jsp"><i class="fa fa-bolt fa-fw"></i> Power Usage</a>
                        </li>
                        <% for (int i = 0; i < pages.length; i++){ %><li>
                        	<a href="custom.jsp?page=<%= pages[i].getName().replaceAll("\\s", "%20") %>"><i class="fa <%= pages[i].getFontAwesomeIconStr() %> fa-fw"></i> <%= pages[i].getName() %></a>
                        </li><% } %>
                        <% } %>
                        <% if (username != null && srv.getPermManager().isUserPermitted(username, "system.admin")){ %>
                        <li>
                        	<a href="#"><i class="fa fa-gear fa-fw"></i> Administration<span class="fa arrow"></span></a>
                        	<ul class="nav nav-second-level">
                        		<li>
                            		<a href="power.jsp"><i class="fa fa-bolt fa-fw"></i> Power Usage Management</a>
                        		</li>
                        		<li>
                            		<a href="actions.jsp"><i class="fa fa-code fa-fw"></i> Actions Management</a>
                        		</li>
                        		<li>
                            		<a href="perms.jsp"><i class="fa fa-lock fa-fw"></i> Permissions Management</a>
                        		</li>
                        		<li>
                            		<a href="users.jsp"><i class="fa fa-users fa-fw"></i> User Management</a>
                        		</li>
                        		<li>
                        			<a href="firmware.jsp"><i class="fa fa-download fa-fw"></i> Firmware Upload</a>
                        		</li>
                        	</ul>
                        </li>
                        <li>
                            <a href="plugins.jsp"><i class="fa fa-plus-square fa-fw"></i> Plugins</a>
                        </li>
                        <% } %>
                    </ul>
                </div>
                <!-- /.sidebar-collapse -->
            </div>
            <!-- /.navbar-static-side -->
        </nav>