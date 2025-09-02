<%@ page language="java" buffer="8kb" autoFlush="true" isThreadSafe="true" isErrorPage="false"  %>
<%@ page import="com.fdsapi.*, com.fdsapi.arrays.*, java.util.Properties" %>
<%@ page import="com.jamonapi.*, com.jamonapi.proxy.*, com.jamonapi.utils.*, com.jamonapi.distributed.*" %>
<%@ page import="org.apache.logging.log4j.LogManager, org.apache.logging.log4j.Logger" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html>
<head>
<META http-equiv="Content-Type" content="text/html"; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/JAMonStyles.css">
<title>Test log4j JAMon Appender</title>
<script type="text/javascript">
<!--
// Row highlighter
var objClass

function rollOnRow(obj, txt) {
    objClass = obj.className
    obj.className = "rowon";
    obj.title = txt;
}

function rollOffRow(obj) {
    obj.className = objClass;
}

function selectAll(obj, numRows) {
    state = (obj.checked) ? true : false;

    for (var i = 1; i < numRows + 1; i ++) {
        currRow = eval("obj.form.row_" + i);
        currRow.checked = state;
    }
}

function helpWin() {
    newWin = window.open('JAMonHelp.htm', 'helpWin', 'resizable=no,scrollbars=yes,height=550,width=450,screenX=100,screenY=100');
    if (newWin.opener == null) newWin.opener = self;
}
// -->
</script>

</head>
<body>

<br><br>
<div align="center">
<form action="log4j.jsp" method="post">  
   <input type="submit" name="generateData" value="Generate Data!">
 </form>


</div>


<br><br>
<hr>

<%
boolean generateData=(request.getParameter("generateData")==null) ? false : true;

if (generateData) {
  logTest();
%>

<p><b>You can see the results of the log4j settings in JAMonAdmin.jsp.  Filter on 'log4j'</b></p>


<%
} // end if (generateData)

%>

<br>
<div align="center" style="padding-top : 30px;">
<hr width="580" align="center" />
<a href="menu.jsp">Home</a> | <a href="jamonadmin.jsp">JAMonAdmin</a>  | <a href="sql.jsp">SQL Buffer</a> | <a href="exceptions.jsp">Exceptions</a>
<hr width="580" align="center" />
</div>

<br><br>
<td><table border='0' align='center' width='25%'>
    <tr>
    <th nowrap><a href="http://www.jamonapi.com"><img src="images/jamon_small.jpg" id="monLink" border="0" /></a></th>
    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
    <th nowrap><a href="http://www.fdsapi.com"><img height=40 width=80 src="images/fds_logo_small.jpg" id="monLink" border="0" /></a></th>
    </tr>
</table></td>



</body>
</html>

<%!

  void logTest() {
      // note put log4j2.xml somewhere accesible by the server.
      // in jetty i put it in resources/

      Logger logger = LogManager.getLogger();
      for (int i = 0; i < 1000; i++) {
          Exception e=new Exception("This is my demo exception string");
          logger.trace("trace message " + i);
          logger.debug("debug message " + i);
          logger.error("error message " + i, e);
          logger.info("info message " + i);
          logger.warn("warn message " + i);
          logger.fatal("fatal message " + i);
    }
}


%>
