<%@ include file="/WEB-INF/jspf/header.html"%>
Solaris Services
<%@ include file="/WEB-INF/jspf/banner.html"%>

<%@ include file="/WEB-INF/jspf/import.jspf"%>

<%
SmfUtils smfu = new SmfUtils();
String qstatus = request.getParameter("status");
String qservice = request.getParameter("service");
%>

<%
if (qstatus == null) {
%>
<h2>All Available Services</h2>
<%
} else {
%>
<h2>Services in state <%=qstatus%></h2>
<%
}
%>

<table border="1" class="header" width="100%"><tr>
<td>Service states:</td>
<%
for (String status : new TreeSet <String> (smfu.getStatusses())) {
%>
<td><a href="services.jsp?status=<%=status%>"><%=status%></a></td>
<%
}
%>
</tr></table>

<table><tr><td width="50%" valign="top">

<ul>
<%
for (SmfService ss : (qstatus == null) ? smfu.getServices() : smfu.getServices(qstatus)) {
String s = ss.getFMRI();
String slim = s.substring(s.lastIndexOf("/")+1).replace(":default", "");
  if (qstatus == null) {
%>
<li><a href="services.jsp?service=<%=s%>" title="<%=s%>"><%=slim%></a></li>
<%
  } else {
%>
<li><a href="services.jsp?service=<%=s%>&status=<%=qstatus%>" title="<%=s%>"><%=slim%></a></li>
<%
  }
}
%>
</ul>

</td><td valign="top">

<%
if (qservice == null) {
%>
Select a service from the list shown on the left to see details about
that service.
<%
} else {
  String servString = smfu.getHtmlInfo(smfu.getService(qservice));
  StringBuffer sb = new StringBuffer();
  Pattern servPattern = Pattern.compile("http://[\\S&&[^<]]*");
  Matcher servMatcher = servPattern.matcher(servString);
  boolean gotit = false;
  while ((gotit = servMatcher.find())) {
    String matchStr = servMatcher.group();
    String newStr = "<a href=\"" + matchStr + "\" target=\"_blank\">" + matchStr + "</a>";
    servMatcher.appendReplacement(sb, newStr);
  }
  servMatcher.appendTail(sb);
  String svcString = sb.toString();
  StringBuffer sb2 = new StringBuffer();
  Pattern svcPattern = Pattern.compile("svc:/[\\S]*");
  Matcher svcMatcher = svcPattern.matcher(svcString);
  gotit = false;
  while ((gotit = svcMatcher.find())) {
    String matchStr = svcMatcher.group();
    String newStr = "<a href=\"services.jsp?service=" + matchStr + "\">" + matchStr + "</a>";
    svcMatcher.appendReplacement(sb2, newStr);
  }
  svcMatcher.appendTail(sb2);
%>
<%=sb2%>
<%
  String sd = smfu.getDepInfo(smfu.getService(qservice));
  if (sd != null) {
%>
<hr>
<%=sd%>
<%
  }
}
%>

</td></tr></table>

<%@ include file="/WEB-INF/jspf/trailer.html"%>
