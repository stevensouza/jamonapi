package com.jamonapi.distributed;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jamonapi.*;
import com.jamonapi.utils.Misc;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

/**  Class that interacts with HazelCast to save jamon data to it so data from any jvms in the hazelcast cluster
 * can be visible via the jamon web app.  Note in must cases hazelcast exceptions are not bubbled up in this class
 * as I would still like jamon to be availalbe even if HazelCast has issues.  The exceptions and stack traces can be
 * seen in jamon however.
 * Created by stevesouza on 7/6/14.
 */

    // monitor.hasListeners() being wrong
//Time too live
//        Timer period
//http://localhost:8080/mancenter
    // try jdk 1.6
    //  also make sure jamon.jar works in jdk.  not sure if being generated for it or not

// timer not working
// timeer configurable
// hazelcast.xml.  does it need the same name?
// timetolive and other configs in the hazelcast.xml
// , 1, TimeUnit.HOURS
// page with hazelcast stats
    // SNAPSHOTS MAKE SURE SEND EMAIL
    //         System.out.println("***** hazelcast stats for jamonmap: "+jamonDataMap.getLocalMapStats());
// x GET Rid of main
    // x use jamon to determine that only one put can happen at a time.
    //better tests.
//<map name="com.jamonapi.*">
//        <!--
//        Maximum number of seconds for each entry to stay idle in the map. Entries that are
//        idle(not touched) for more than <max-idle-seconds> will get
//        automatically evicted from the map. Entry is touched if get, put or containsKey is called.
//        Any integer between 0 and Integer.MAX_VALUE. 0 means infinite. Default is 0.
//        -->
//        <!-- 1 hour = 3600, 24 hr = 86400 MAYBE DIFFERENT TIME FOR EACH MAP -->
//        <max-idle-seconds>86400</max-idle-seconds>
//        </map>
    // in jsp page use resultsetconverter System.out.println("***** hazelcast stats for jamonmap: " + jamonDataMap.getLocalMapStats());
// 1) monmanage.jsp not working.  print results and see if i can figure it out.
// 2) don't let this exception prevent jamon from coming up. failback to regular jamon maybe iwth error in listbox or on screen.
// org.apache.jasper.JasperException: An exception occurred processing JSP page /jamonadmin.jsp at line 192

//189:     <th align="right"><a href="javascript:helpWin();" style="color:#C5D4E4;">Help</a></th>
//        190:     </tr>
//        191:     <tr class="even">
//        192:     <th><%=fds.getDropDownListBox(instanceNameHeader, getInstanceData(jamonData.getInstances()), instanceName)%></th>
// x 3) tomcat error
    // x 5) move startup of hazelcast to first put or get
    // x 7) i hazelcast is not available don't have jamon fail
    // x 8) do a cluster with 2 nodes test.
    // x 9) test on vagrant
    // 10) change serialid to versoin number 278
    // documentation
    // properties http://www.mkyong.com/java/java-properties-file-examples/
// String library = System.getProperty("parser.library", "datapipeline");
    //         System.out.println("  -Dparser.library=datapipeline (possible values: datapipeline or supercsv. datapipeline is the default)");
// config file better.
    // or can you set a system property.  do test program...

/*

    //   MonitorComposite composite =  driver.getMonitors(nodeName);
//                    System.out.println("****distributed mapsize: " + driver.getMap().size() + ", MonitorComposite rows: " + composite.getNumRows());
//                    System.out.println("**** cluster members: " + driver.hazelCast.getCluster().getMembers());
//                    System.out.println("****"+driver.hazelCast.getCluster().getLocalMember().toString());
//                    System.out.println("****"+driver.hazelCast.getName());
//                    System.out.println(driver.jamonDataMap.getLocalMapStats());

14-07-09 23:59:47.932:WARN:oejs.ServletHandler:qtp957394696-17:
        org.apache.jasper.JasperException: An exception occurred processing JSP page /jamonadmin.jsp at line 78||75: String outputText;|76: JamonData jamonData = JamonDataFactory.get();|77: MonitorComposite mc =  jamonData.getMonitors(instanceName);|78: Date refreshDate = mc.getDateCreated();|79: mc = mc.filterByUnits(rangeName);|80: session.setAttribute("monitorComposite",mc);|81: |||Stacktrace:
        at org.apache.jasper.servlet.JspServletWrapper.handleJspException(JspServletWrapper.java:568)
        at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:470)
        at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:403)
        at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:347)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
        at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:751)
        at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:566)
        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:143)
        at org.eclipse.jetty.security.SecurityHandler.handle(SecurityHandler.java:578)
        at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:221)
        at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1111)
        at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:498)
        at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:183)
        at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1045)
        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)
        at org.eclipse.jetty.server.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:199)
        at org.eclipse.jetty.server.handler.HandlerCollection.handle(HandlerCollection.java:109)
        at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:98)
        at org.eclipse.jetty.server.Server.handle(Server.java:461)
        at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:284)
        at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:244)
        at org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:534)
        at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:607)
        at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:536)
        at java.lang.Thread.run(Thread.java:744)
        Caused by:
        java.lang.NullPointerException
        at org.apache.jsp.jamonadmin_jsp._jspService(jamonadmin_jsp.java:468)
        at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:70)


        Macintosh-c8bcc88febc6:webapp stevesouza$ grep MonitorFactory *.jsp
x exceptions.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
x exceptions.jsp:     //MonitorFactory.add("cellCount","count",rsc.getColumnCount()*rsc.getRowCount());
jamonadmin.jsp:if (!MonitorFactory.isEnabled())
jamonadmin.jsp:    <th><%=fds.getDropDownListBox(MonitorFactory.getRangeHeader(), getRangeNames(), rangeName)%></th>
jamonadmin.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
jamonadmin.jsp:    MonitorFactory.reset();
jamonadmin.jsp:    MonitorFactory.setEnabled(true);
jamonadmin.jsp:    MonitorFactory.setEnabled(false);
jamonadmin.jsp:     MonitorFactory.enableActivityTracking(true);
jamonadmin.jsp:     MonitorFactory.enableActivityTracking(false);
jamonadmin.jsp:     //MonitorFactory.add("cellCount","count",rsc.getColumnCount()*rsc.getRowCount());
jamonadmin.jsp:   Object[][] range=MonitorFactory.getRangeNames();
log4j.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
menu.jsp:  MonitorFactory.enable();
menu.jsp:  MonitorFactory.disable();
menu.jsp:<title>JAMon <%=MonitorFactory.getVersion()%> Menu - Support Pages</title>
menu.jsp:<th class="sectHead">JAMon <%=MonitorFactory.getVersion()%> Support Pages</th>
menu.jsp:<%=enabled("JAMon = ",MonitorFactory.isEnabled())%>,
menu.jsp:<%=enabled("Activity Tracking = ",MonitorFactory.isActivityTrackingEnabled())%>
menu.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
menu.jsp:    long totalKeySize=MonitorFactory.getTotalKeySize();
mondetail.jsp:  if (MonitorFactory.exists(key))
mondetail.jsp:    listener=MonitorFactory.getMonitor(key).getListenerType(listenerType).getListener(currentListenerName);
mondetail.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
mondetail.jsp:   Object[][] range=MonitorFactory.getRangeNames();
monmanage.jsp:if (MonitorFactory.exists(key)) {
monmanage.jsp:  mon=MonitorFactory.getMonitor(key);
monmanage.jsp:    <%= MonitorFactory.exists(key)%><br>
monmanage.jsp:    <%=MonitorFactory.getMonitor(key) %><br>
monmanage.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
monmanage.jsp:   if (MonitorFactory.exists(key)) {
monmanage.jsp:     Monitor mon=MonitorFactory.getMonitor(key);
monmanage.jsp:   if (MonitorFactory.exists(key)) {
monmanage.jsp:     Monitor mon=MonitorFactory.getMonitor(key);
monmanage.jsp:   if (MonitorFactory.exists(key)  && MonitorFactory.getMonitor(key).hasListeners(listenerType)) {
monmanage.jsp:      return getCurrentListeners(MonitorFactory.getMonitor(key).getListenerType(listenerType).getListener());
monmanage.jsp:if (MonitorFactory.exists(key)) {
monmanage.jsp:    MonitorFactory.getMonitor(key).reset();
monmanage.jsp:    MonitorFactory.getMonitor(key).enable();
monmanage.jsp:    MonitorFactory.getMonitor(key).disable();
query.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
sql.jsp:    <th nowrap>JAMon <%=MonitorFactory.getVersion()%></th>
sql.jsp:  //MonitorFactory.add("cellCount","count",rsc.getColumnCount()*rsc.getRowCount());
*/

public class DistributedJamonHazelcast implements JamonData {

    // could be Map if we don't want the instance methods of hazelcast
    private IMap<String, MonitorComposite> jamonDataMap;
    // This should really be an ISet, but ISet doesn't support time-to-live methods
    private IMap<String, Date> instances;
    private HazelcastInstance hazelCast;
    private LocalJamonData localJamonData = new LocalJamonData();

    public DistributedJamonHazelcast() {
        hazelCast = Hazelcast.newHazelcastInstance();
    }

    public DistributedJamonHazelcast(HazelcastInstance hazelCast) {
        this.hazelCast = hazelCast;
    }

    @Override
    public Set<String> getInstances() {
        Set<String> allInstances = localJamonData.getInstances();
        allInstances.addAll(getHazelcastInstances());
        return allInstances;
    }

    @Override
    /** Put jamon data into the hazelcast map */
    public void put() {
        String label = DistributedJamonHazelcast.class.getCanonicalName()+".put()";
        Monitor mon = MonitorFactory.getTimeMonitor(label);
        // only allow 1 process to put at the sametime.
        if (mon.getActive() < 1) {
            mon.start();
            try {
                intitialize();
                String key = getInstance();
                jamonDataMap.set(key, MonitorFactory.getRootMonitor().setInstanceName(key));
                instances.set(key, new Date());
            } catch(Throwable t) {
                MonitorFactory.addException(mon, t);
            } finally {
                mon.stop();
            }
        }
    }


    @Override
    public MonitorComposite getMonitors(String key) {
        MonitorComposite monitorComposite = localJamonData.getMonitors(key);
        if (monitorComposite == null) {
            String label = DistributedJamonHazelcast.class.getCanonicalName() + ".get()";
            Monitor mon = MonitorFactory.start(label);
            try {
                intitialize();
                // done purely to ensure instance and data live the same amount of time.
                instances.get(key);
                monitorComposite = jamonDataMap.get(key);
            } catch(Throwable t) {
                MonitorFactory.addException(mon, t);
                return localJamonData.getMonitors("local");
            } finally {
                mon.stop();
            }
        }

        return monitorComposite;
    }

    private String getInstance() {
       try {
           intitialize();
           return hazelCast.getCluster().getLocalMember().toString();
       } catch (Throwable t) {
          MonitorFactory.addException(t);
       }

        return null;
    }

    public void shutDownHazelCast() {
        try {
            intitialize();
            hazelCast.shutdown();
        } catch (Throwable t) {
            MonitorFactory.addException(t);
        }
    }

    // I don't ever want to not display data when there is a hazelcast error.
    private Set<String> getHazelcastInstances() {
        try {
            intitialize();
            return instances.keySet();
        } catch (Throwable e) {
            MonitorFactory.addException(e);
            Set<String> error=new HashSet<String>();
            error.add("HazelcastExceptionThrown");
            return error;
        }
    }

    private void intitialize() {
        if (jamonDataMap == null) {
            jamonDataMap = hazelCast.getMap(MonitorComposite.class.getCanonicalName());
            instances = hazelCast.getMap("com.jamonapi.instances");
        }
    }

}
