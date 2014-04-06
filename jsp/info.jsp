<%@ include file="/WEB-INF/jspf/header.html"%>
Solaris System Information
<%@ include file="/WEB-INF/jspf/banner.html"%>

<%@ include file="/WEB-INF/jspf/import.jspf"%>

<h2>Available Commands</h2>

<table><tr><td width="20%" valign="top">

<ul>
<%
InfoCommandList iclist = new InfoCommandList();
for (InfoCommand infocmd : iclist) {
%>
<li><a href="info.jsp?info=<%=infocmd.toString()%>"><%=infocmd.toString()%></a></li>
<%
}
%>
</ul>

</td><td valign="top">

<%
String querypkg = request.getParameter("info");
if (querypkg != null) {
  InfoCommand infocmd = iclist.getCmd(querypkg);
  if (infocmd == null) {
%>
Unrecognized command <%=querypkg%>.
<%
  } else {
%>
<%=iclist.infoTable(infocmd)%>
<%
  }
}
%>

</td></tr></table>

<%@ include file="/WEB-INF/jspf/trailer.html"%>
