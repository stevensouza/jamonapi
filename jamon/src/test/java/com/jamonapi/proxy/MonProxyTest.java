package com.jamonapi.proxy;

import com.jamonapi.FactoryEnabled;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


/** Class that tests the various JAMon proxy classes via the main method */

public class MonProxyTest {

    private static final String HELLO_WORLD="hello world";
    private static int testCounter = 0;
    private static final String[] STR_ARRAY={""};


    @Before
    public void setUp() throws Exception {
        MonProxyFactory.reset();
        // Reset JAMon before each test method.  The Monitors are static and so would otherwise stick around
        MonitorFactory.reset();
    }


    @Test
    public void testSqlNoProxy() throws Exception {
        int times = 2000;
        Params params = new Params();
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        mainTestMethod("Non monitored connection first time", conn, times, params, mf);

        assertThat(MonProxyFactory.getSQLDetail()).isNull();
        assertThat(MonProxyFactory.getExceptionDetail()).isNull();
        assertThat(MonitorFactory.getRootMonitor().getBasicData()).isNull();
    }

    @Test
    public void testSqlNoProxy2() throws Exception {
        int times = 2000;
        Params params = new Params();
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        MonProxyFactory.enableAll(false);
        // returns regular connection
        conn = MonProxyFactory.monitor(conn);
        mainTestMethod( "MonProxyFactory disabled at creation of Connection (should be fast as regular connection)", conn, times, params, mf);

        assertThat(MonProxyFactory.getSQLDetail()).isNull();
        assertThat(MonProxyFactory.getExceptionDetail()).isNull();
        assertThat(MonitorFactory.getRootMonitor().getBasicData()).isNull();
    }

    @Test
    public void testConnection() throws Exception {
        JAMonDriver.register();
        for (int i=0;i<5;i++) {
            Properties props=new Properties();
            props.put("user","sa");
            props.put("password","");
            Connection conn=null;
            if (i%2==0) {
                props.put("jamonrealdriver", "org.hsqldb.jdbcDriver");
                conn = DriverManager.getConnection("jdbc:hsqldb:mem:.",  props);
            } else
                conn = DriverManager.getConnection("jdbc:jamon:hsqldb:mem:.jamonrealdriver=org.hsqldb.jdbcDriver",  props);

            Statement s=conn.createStatement();
            s.close();
            conn.close();
        }

        assertThat(MonitorFactory.getNumRows()).isEqualTo(4);
        assertThat(MonitorFactory.getReport()).contains("connect");
        assertThat(MonitorFactory.getReport()).contains("Connection.createStatement()");
        assertThat(MonitorFactory.getReport()).contains(".close()"); // statement, and connection close
    }

    @Test
    public void testSqlProxy() throws Exception {
        int times = 2000;
        Params params = new Params();
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        // returns monitored connection
        MonProxyFactory.enableAll(true);
        conn = MonProxyFactory.monitor(conn);

        mainTestMethod("MonProxyFactory defaults first time", conn, times, params, mf);

        assertThat(MonProxyFactory.getSQLDetail().length).isEqualTo(100);
        assertThat(MonProxyFactory.getExceptionDetail().length).isEqualTo(1);
        assertSqlMonitoringEnabled(MonitorFactory.getReport());
        assertThat(MonitorFactory.getReport()).doesNotContain("ResultSet.next()");
    }

    @Test
    public void testSqlProxy_ResultSetEnabled() throws Exception {
        int times = 2000;
        Params params = new Params();
        params.isResultSetEnabled = true;
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        // returns monitored connection
        MonProxyFactory.enableAll(true);
        conn = MonProxyFactory.monitor(conn);

        mainTestMethod("MonProxyFactory ResultSet enabled", conn, times, params, mf);

        assertThat(MonProxyFactory.getSQLDetail().length).isEqualTo(100);
        assertThat(MonProxyFactory.getExceptionDetail().length).isEqualTo(1);
        assertSqlMonitoringEnabled(MonitorFactory.getReport());
        assertThat(MonitorFactory.getReport()).contains("ResultSet.next()");
    }

    @Test
    public void testSqlProxy_Disabled() throws Exception {
        int times = 2000;
        Params params = new Params();
        params.isEnabled = false;
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        // returns monitored connection
        MonProxyFactory.enableAll(true);
        conn = MonProxyFactory.monitor(conn);

        mainTestMethod("MonProxyFactory disabled (uses monitored connection but disabled)", conn, times, params, mf);

        assertThat(MonProxyFactory.getSQLDetail()).isNull();
        assertThat(MonProxyFactory.getExceptionDetail()).isNull();;
        assertThat(MonitorFactory.getRootMonitor().getBasicData()).isNull();
    }

    @Test
    public void testSqlProxy_InterfaceDisabled() throws Exception {
        int times = 2000;
        Params params = new Params();
        params.isInterfaceEnabled = false;
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        // returns monitored connection
        MonProxyFactory.enableAll(true);
        conn = MonProxyFactory.monitor(conn);

        mainTestMethod("MonProxyFactory interface disabled", conn, times, params, mf);

        String report=MonitorFactory.getReport();
        assertThat(report).contains("MonProxy-SQL-Type: All");
        assertThat(report).contains("MonProxy-SQL-Type: select");
        assertThat(report).doesNotContain("MonProxy-Interface");
        assertThat(report).contains("MonProxy-Exception");

        assertThat(MonProxyFactory.getSQLDetail().length).isEqualTo(100);
        assertThat(MonProxyFactory.getExceptionDetail().length).isEqualTo(1);
    }


    @Test
    public void testSqlProxy_SqlDetailAndInterfaceDisabled() throws Exception {
        int times = 2000;
        Params params = new Params();
        params.isInterfaceEnabled = false;
        params.isSQLDetailEnabled = false;
        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        // returns monitored connection
        MonProxyFactory.enableAll(true);
        conn = MonProxyFactory.monitor(conn);

        mainTestMethod("MonProxyFactory - sql detail and interface disabled", conn, times, params, mf);

        String report=MonitorFactory.getReport();
        assertThat(report).contains("MonProxy-SQL-Type: All");
        assertThat(report).contains("MonProxy-SQL-Type: select");
        assertThat(report).doesNotContain("MonProxy-Interface");
        assertThat(report).contains("MonProxy-Exception");

        assertThat(MonProxyFactory.getSQLDetail()).isNull();
        assertThat(MonProxyFactory.getExceptionDetail().length).isEqualTo(1);
    }


    @Test
    public void testSqlProxy_WithMatchStrings() throws Exception {
        int times = 2000;
        Params params = new Params();
        List list = new ArrayList();
        list.add("SYSTEM_TYPEINFO");// could be tables or other keywords
        list.add("LOCAL_TYPE_NAME");
        MonProxyFactory.setMatchStrings(list);

        FactoryEnabled mf = new FactoryEnabled();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");

        // returns monitored connection
        MonProxyFactory.enableAll(true);
        conn = MonProxyFactory.monitor(conn);

        mainTestMethod("MonProxy defaults with keyword match", conn, times, params, mf);

        assertThat(MonProxyFactory.getSQLDetail().length).isEqualTo(100);
        assertThat(MonProxyFactory.getExceptionDetail().length).isEqualTo(1);
        assertSqlMonitoringEnabled(MonitorFactory.getReport());
        assertThat(MonitorFactory.getReport()).doesNotContain("ResultSet.next()");
        assertThat(MonitorFactory.getReport()).contains("MonProxy-SQL-Match: LOCAL_TYPE_NAME");
        assertThat(MonitorFactory.getReport()).contains("MonProxy-SQL-Match: SYSTEM_TYPEINFO");
    }

    @Test
    public void testProxyEquals() {
        Tag0 noProxyObj = new MyClass0();
        Tag0 proxyObj = (Tag0) MonProxyFactory.monitor(noProxyObj);
        Tag0 proxyObj2 = (Tag0) MonProxyFactory.monitor(noProxyObj);
        Tag0 proxyObjTwice = (Tag0) MonProxyFactory.monitor(proxyObj);

        assertThat( proxyObj.equals(noProxyObj)).isTrue();
        assertThat( proxyObj.equals(proxyObj)).isTrue();
        assertThat( proxyObj.equals(proxyObj2)).isTrue();
        assertThat( proxyObj2.equals(proxyObj)).isTrue();
        assertThat( proxyObj.equals(proxyObjTwice)).isTrue();
        assertThat( noProxyObj.equals(noProxyObj)).isTrue();

        assertThat(  noProxyObj.equals(proxyObj)).isFalse();
        assertThat(  proxyObj.equals(null)).isFalse();
    }


    @Test
    public void testMapProxy() {
        Map map= (Map) MonProxyFactory.monitor(new HashMap());
        map.put("key","value");
        map.get("key");

        Map mapExpected=new HashMap();
        mapExpected.put("key", "value");

        assertThat(map).isEqualTo(mapExpected);
        assertThat(MonitorFactory.getReport()).contains("Map.put(");
        assertThat(MonitorFactory.getReport()).contains("Map.get(");
    }

    @Test
    public void testInterfaces_MyClass0() {
        List<String> interfaces=testInterfaces(new MyClass0().getClass());
        List<String> expectedInterfaces=new ArrayList<String>();
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Tag0");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base1");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base0");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base2");
        assertThat(interfaces).containsOnly(expectedInterfaces.toArray(STR_ARRAY));
    }

    @Test
    public void testInterfaces_MyClass1() {
        List<String> interfaces=testInterfaces(new MyClass1().getClass());
        List<String> expectedInterfaces=new ArrayList<String>();
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Tag0");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base1");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base0");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base2");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Tag1");
        assertThat(interfaces).containsOnly(expectedInterfaces.toArray(STR_ARRAY));
    }

    @Test
    public void testInterfaces_MyClass1AsObject() {
        Object obj=new MyClass1();
        List<String> interfaces=testInterfaces(obj.getClass());
        List<String> expectedInterfaces=new ArrayList<String>();
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Tag0");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base1");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base0");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Base2");
        expectedInterfaces.add("com.jamonapi.proxy.MonProxyTest.Tag1");
        assertThat(interfaces).containsOnly(expectedInterfaces.toArray(STR_ARRAY));
    }

    @Test
    public void testInterfaces_Null() {
        List<String> interfaces=testInterfaces(null);
        assertThat(interfaces).isEmpty();
    }

    @Test
    public void testInterfaces_StringArrayClass() {
        List<String> interfaces=testInterfaces(String[].class);
        List<String> expectedInterfaces=new ArrayList<String>();
        expectedInterfaces.add("java.io.Serializable");
        expectedInterfaces.add("java.lang.Cloneable");
        assertThat(interfaces).containsOnly(expectedInterfaces.toArray(STR_ARRAY));
    }

    private void assertSqlMonitoringEnabled(String report) {
        assertThat(report).contains("MonProxy-SQL-Type: All");
        assertThat(report).contains("MonProxy-SQL-Type: select");
        assertThat(report).contains("MonProxy-Interface");
        assertThat(report).contains("MonProxy-Exception");
    }


    private static ResultSet testStatement(Connection conn) throws Exception {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select 'hello world' from INFORMATION_SCHEMA.SYSTEM_TYPEINFO");
        return rs;
    }


    private static ResultSet testPreparedStatement(Connection conn)  throws Exception {
        PreparedStatement ps = conn.prepareStatement("select 'hello world' from INFORMATION_SCHEMA.SYSTEM_TYPEINFO where LOCAL_TYPE_NAME=?");
        ps.setString(1, "INTEGER");
        ResultSet rs = ps.executeQuery();
        return rs;
    }


    private static void throwException(Connection conn) {
        try {
            Statement st = conn.createStatement();
            st.executeQuery("select * from I_DO_NOT_EXIST where LOCAL_TYPE_NAME='INTEGER'");
        } catch (Exception e) {
            // typical of code people do in hiding exceptions
        }

    }

    // use extra column here to have different stats for prepared statement reuse and not reuse.
    private static PreparedStatement getPreparedStatement(Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement("select SI.*, 'PreparedStatement Reuse Query' from INFORMATION_SCHEMA.SYSTEM_TYPEINFO SI where LOCAL_TYPE_NAME=?");
        ps.setString(1, "INTEGER");
        return ps;
    }


    private static void assertResultSetValues(ResultSet rs) throws Exception {
        while (rs.next()) {
            assertThat(rs.getObject(1).toString()).isEqualTo(HELLO_WORLD);
        }
    }

    /** Test classes for interface heirarchy checking */
    private interface Tag0 {
    }

    private interface Tag1 {
    }

    private interface Base0 extends Tag0 {
    }

    private interface Base1 extends Base0 {
    }

    private interface Base2 extends Base1, Tag0, Base0 /* note Base0 twice in heirarchy */{
    }

    private static class MyClass0 implements Base2 {
    }

    private static class MyClass1 extends MyClass0 implements Tag1 {
    }

    private static List<String> testInterfaces(Class cls) {
        List<String> list=new ArrayList();
        Class[] ifaces = MonProxyFactory.getInterfaces(cls);

        int len = (ifaces == null) ? 0 : ifaces.length;
        for (int i = 0; i < len; i++) {
            list.add(ifaces[i].getCanonicalName());
        }

        return list;
    }

    private static void mainTestMethod(String name, Connection conn, int times, Params params, FactoryEnabled mf) throws Exception {
        testCounter++;
        System.out.println("\n\n\n" + testCounter + ") " + name + "** "+ params);

        Monitor mon = mf.start(testCounter + ") " + name + " ** "+ params.toString());
        Monitor monTotal = mf.start("totalTime");
        MonProxyFactory.enableResultSet(params.isResultSetEnabled);
        MonProxyFactory.enableInterface(params.isInterfaceEnabled);
        MonProxyFactory.enableSQLDetail(params.isSQLDetailEnabled);
        MonProxyFactory.enableSQLSummary(params.isSQLSummaryEnabled);
        MonProxyFactory.enableExceptionSummary(params.isExceptionSummaryEnabled);
        MonProxyFactory.enableExceptionDetail(params.isExceptionDetailEnabled);
        MonProxyFactory.enable(params.isEnabled);

        PreparedStatement ps = getPreparedStatement(conn);
        for (int i = 0; i < times; i++) {
            ps.executeQuery();
            assertResultSetValues(testStatement(conn));
            assertResultSetValues(testPreparedStatement(conn));
        }

        throwException(conn);
        conn.close();

        monTotal.stop();
        String message = name + " execution time: " + mon.stop().getLastValue();
        System.out.println(message);
        System.out.println(MonitorFactory.getReport());
    }



}
