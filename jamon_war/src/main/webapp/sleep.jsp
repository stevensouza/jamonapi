<%@ page language="java" buffer="8kb" autoFlush="true" isThreadSafe="true" isErrorPage="false" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <META http-equiv="Content-Type" content="text/html" ; charset=ISO-8859-1">
    <link rel="stylesheet" type="text/css" href="css/JAMonStyles.css">
    <title>Random sleep to generate interesting data for jamon</title>


</head>
<body>

<br><br>
<div align="center">
    <form action="sleep.jsp" method="post">
        <input type="submit" name="generateData" value="Sleep randomly up to 5 seconds">
    </form>


</div>


<br><br>
<hr>

<%
    long duration = sleepRandom(5);
%>
<p>The page slept for <%=duration%> seconds.</p>
<p><b>You can see the results in JAMonAdmin.jsp. Filter on 'sleep.jsp'</b></p>


<br>
<div align="center" style="padding-top : 30px;">
    <hr width="580" align="center"/>
    <a href="menu.jsp">Home</a> | <a href="jamonadmin.jsp">JAMonAdmin</a> | <a href="sql.jsp">SQL Buffer</a> | <a
        href="exceptions.jsp">Exceptions</a>
    <hr width="580" align="center"/>
</div>

<br><br>
<td>
    <table border='0' align='center' width='25%'>
        <tr>
            <th nowrap><a href="http://www.jamonapi.com"><img src="images/jamon_small.jpg" id="monLink" border="0"/></a>
            </th>
            <th nowrap><a href="http://www.fdsapi.com"><img height=40 width=80 src="images/fds_logo_small.jpg"
                                                            id="monLink" border="0"/></a></th>
        </tr>
    </table>
</td>


</body>
</html>

<%!

    long sleepRandom(int seconds) {
        try {
            long duration = (long) (Math.random() * seconds * 1000);
            Thread.sleep(duration);
            return duration;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }


%>
