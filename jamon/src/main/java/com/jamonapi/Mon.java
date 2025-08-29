package com.jamonapi;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * Servlet that enables the ability to take advantage of jamon by passing arbitrary strings to this servlet which
 * subsequently calls start/stop/add on the MonitorFactory.  The distribution comes with some bash scripts which should work
 * on mac/linux/unix that allow you to take advantage of jamon facilities from your scripts.  This can be used to track nightly
 * running processes such as backups to name just one.  The scripts simply take arguments and reformat them to calls to this servlet
 * via either curl or wget whichever is installed.   See them as well as jamon_readme.txt in the distribution zip in the directory
 * osmon for more info.
 *
 * <p>Note if the calls was unsuccessful (to add/start/stop/get) then the error message is returned which starts with the keyword: Error.  OK will be  returned by
 * add/start/stop if they are successful, but not get.  get simply returns the value or error if there was a problem.  script calls to jgetdata.sh don't call
 * this servlet and so they don't return error if they fail, however you can check to see if they contain data.
 * </p>
 * 
 * <p>Happy monitoring!</p>
 * 
 * <p>put this in web.xml file</p>
 * 
 * <pre>{@code
 *  <servlet>
 *       <servlet-name>Mon</servlet-name>
 *       <servlet-class>com.jamonapi.Mon</servlet-class>
 *  </servlet>
 *
 *  <servlet-mapping>
 *    <servlet-name>Mon</servlet-name>
 *    <url-pattern>/Mon</url-pattern>
 *  </servlet-mapping>
 *
 * }</pre>
 *
 * @author stevesouza
 *
 */

public class Mon extends HttpServlet {

    private static final long serialVersionUID = 278L;
    private static final String DEFAULT_UNITS="ms.";
    private static final String ERROR="Error";
    private static final String OK="OK";
    private static String help= "This servlet allows one to take advantage of JAMon by calling a servlet. This allows \n"+
    "calls to be made from operating system scripts for example.  Typically you would make \n"+
    "calls by using curl or wget to make http calls to the JAMon Mon Servlet. start/stop work like \n"+
    "a stopwatch. You pass in a label that will appear in the jamon report. You pass the same label to \n" +
    "both start and stop the monitor. For more information look at osmon/jamon_readme.txt as well as the\n"+
    "scripts in the same directory.  These scripts allow you to access JAMon from the OS and can also be altered"+
    "to meet your needs if that is your requirement.  (http://www.jamonapi.com)";

    private Map map=new ConcurrentHashMap();// used to find monitors to stop or skip.

    @Override
    public void doGet(HttpServletRequest request,  HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        if (hasValue(request.getParameter("start")))
            start(request, response);
        else if (hasValue(request.getParameter("stop")))
            stop(request, response);
        else if (hasValue(request.getParameter("add")))
            add(request, response);
        else if (hasValue(request.getParameter("skip")))
            skip(request, response);
        else if (hasValue(request.getParameter("get")))
            getValue(request, response);
        else
            displayHelp(response);
    }

    /** create a key from passed in label, details and units.  note that details aren't really part of the key
     * 
     * @param label Monitors key (Example: myUnixBackup)
     * @param detail Any string that you would want to access via a listener (Example: 400MB backup succeeded)
     * @param units Monitors unit (Example: minutes)
     * @return The monitors key
     */
    private MonKey getMonKey(String label, String detail, String units) {
        if (detail==null)
            detail=label;

        return  new MonKeyImp(label, detail, units);
    }

    /** start a monitor by providing a summary label and optionally a detail string. note this won't be called unless
     *  a label was passed.
     * 
     * @param request Servlet request
     * @param response Servlet response
     * @throws IOException
     */
    private void start(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String label=request.getParameter("start");
        String detail=request.getParameter("detail");
        MonKey key=getMonKey(label, detail, DEFAULT_UNITS);
        startMon(key);
        PrintWriter out = response.getWriter();
        out.println(OK+": start successfully called on - "+label);
    }

    /** start a monitor and put it in the map so it can be retrieved when stop or skip are called.  note add doesn't
     * need the monitor in the map.
     */
    private void startMon(MonKey key) {
        map.put(key, MonitorFactory.start(key));
    }

    /** stop a previously started monitor if it exists. */
    private void stop(HttpServletRequest request,  HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String label=request.getParameter("stop");
        Monitor mon=removeMon(label, null, DEFAULT_UNITS);
        if (mon==null) {
            out.println(ERROR+": stop requires that an existing label is passed in.  For example: curl http://localhost:8080/jamon/Mon?stop=mylabel");
        } else {
            mon.stop();
            out.println(OK+": stop successfully called on - "+mon);
        }

    }

    /** skip a previously started monitor if it exists.  doesn't add value */
    private void skip(HttpServletRequest request,  HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String label=request.getParameter("skip");
        Monitor mon=removeMon(label, null, DEFAULT_UNITS);
        if (mon==null) {
            out.println(ERROR+": skip requires that an existing label is passed in.  For example: curl http://localhost:8080/jamon/Mon?skip=mylabel");
        } else {
            mon.skip();
            out.println(OK+": skip successfully called on - "+mon);
        }

    }

    /** remove monitor from map and also return it.  This is done in conjunction with calls to stop and skip. */
    private Monitor removeMon(String label, String detail, String units) {
        MonKey key=getMonKey(label, detail, units);
        Monitor mon=(Monitor) map.get(key);
        if (mon!=null)
            map.remove(mon);

        return mon;
    }

    /** process calls to add which is an implemetation of a counter or adder. */
    private void add(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        String label=request.getParameter("add"); // jamon label
        String units=request.getParameter("units"); // something like bytes
        String value=request.getParameter("value"); // number
        String detail=request.getParameter("detail"); // can view via jamon listeners.

        Double val=toDouble(value);
        if (isEmpty(label) || isEmpty(units) || isEmpty(val)) {
            out.println(ERROR+": add requires that a label, units and numeric value are passed in.  For example: curl http://localhost:8080/jamon/Mon?add=mylabel&units=bytes&value=1234");
            out.println("  Optionally a detail may be passed in: curl http://localhost:8080/jamon/Mon?add=os.mylabel&units=bytes&value=1234&detail=Backup%20script%20from%20host%20backupserver\n");
        } else {
            MonKey key=getMonKey(label, detail, units);
            Monitor mon=MonitorFactory.add(key, val);
            out.println(OK+": add successfully called on - "+label+" - "+mon);
        }

    }

    /** return a value out of the monitor such as hits, max, min etc. */
    private void getValue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String label=request.getParameter("get");
        String type=request.getParameter("type");
        String units=request.getParameter("units");
        if (isEmpty(units))
            units=DEFAULT_UNITS;

        if (isEmpty(label) || isEmpty(type)) {
            out.println(ERROR+": get requires that a label, and type be passed in.  units is optional and will default to a time monitor if not provided (ms.). ");
            out.println("  Type is the type of value you want from the given monitor.  Examples are: hits, avg, min, max, total, firstaccess, lastaccess, active. ");
            out.println("   For example: curl http://localhost:8080/jamon/Mon?get=mylabel&type=avg");
            out.println("   For example: curl http://localhost:8080/jamon/Mon?get=mylabel&type=max&units=bytes");
        } else {  // OK get value
            MonKey key=getMonKey(label, null, units);
            Monitor mon=MonitorFactory.getMonitor(key);
            if (mon==null)
                out.println(ERROR+": Key didn't exist ("+label+","+units+")");
            else {
                Object obj=null;
                if ("mon".equalsIgnoreCase(type))
                    obj=mon.toString();
                else
                    obj=mon.getValue(type);

                if (obj==null)
                    out.println(ERROR+": type didn't exist ("+type+")");
                else // success return value
                    out.println(obj);
            }

        }
    }


    /** convert string to Double */
    private Double toDouble (String numStr) {
        try {
            return Double.valueOf(numStr);
        } catch (Exception e) {
            return null;
        }
    }

    private void displayHelp( HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(help);
    }

    /** return true if string is null or empty */
    private boolean isEmpty(Object str) {
        return (str==null || "".equals(str));
    }

    /** return true if string has a value i.e. if string is not empty */
    private boolean hasValue(Object str) {
        return !isEmpty(str);
    }


    @Override
    public void doPost(HttpServletRequest request,  HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
