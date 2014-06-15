package com.jamonapi.proxy;

/** Class that tests the various JAMon proxy classes via the main method */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jamonapi.FactoryEnabled;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class MonProxyTester {

	private static void printDebug(String title, Object[][] data) {
		System.out.println("\n***" + title + "***");
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				System.out.print("row=" + i + " - ");
				for (int j = 0; j < data[0].length; j++)
					System.out.print(data[i][j] + ", ");

				System.out.println();
			}
		}

	}

	private static ResultSet testStatement(Connection conn) throws Exception {

		Statement st = conn.createStatement();
		ResultSet rs = st
				.executeQuery("select * from SYSTEM_TYPEINFO where LOCAL_TYPE_NAME='INTEGER'");
		return rs;
	}

	private static ResultSet testPreparedStatement(Connection conn)
			throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("select * from SYSTEM_TYPEINFO where LOCAL_TYPE_NAME=?");
		ps.setString(1, "INTEGER");

		ResultSet rs = ps.executeQuery();
		return rs;
	}

	private static void throwException(Connection conn) {

		try {
			Statement st = conn.createStatement();
			st
					.executeQuery("select * from I_DO_NOT_EXIST where LOCAL_TYPE_NAME='INTEGER'");
		} catch (Exception e) {
			//typical of code people do in hiding exceptions
		}

	}

	// use extra column here to have different stats for prepared statement reuse and not reuse.
	private static PreparedStatement getPreparedStatement(Connection conn)
			throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("select *,'PreparedStatement Reuse Query' from SYSTEM_TYPEINFO where LOCAL_TYPE_NAME=?");
		ps.setString(1, "INTEGER");
		return ps;

	}

	private static void testLoopResultSet(ResultSet rs) throws Exception {
		while (rs.next()) {
			rs.getObject(1); // Is SQL the first column is indexed
		}
	}

	private static void testDisplayResultSet(ResultSet rs) throws Exception {
		while (rs.next()) {
			System.out.println(rs.getObject(1) + " ");
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

	private static void testInterfaces(Class cls) {

		System.out.println("\nInterfaceHeirarchy for " + cls);
		Class[] ifaces = MonProxyFactory.getInterfaces(cls);

		int len = (ifaces == null) ? 0 : ifaces.length;
		for (int i = 0; i < len; i++)
			System.out.println(i + ") " + ifaces[i]);

	}

	private static void mainTestMethod(String name, Connection conn, int times,
			Params params, FactoryEnabled mf) throws Exception {
		testCounter++;

		System.out.println("\n\n\n" + testCounter + ") " + name + "** "
				+ params);
		Monitor mon = mf.start(testCounter + ") " + name + " ** "
				+ params.toString());
		Monitor monTotal = mf.start("totalTime");
		MonProxyFactory.enableResultSet(params.isResultSetEnabled);
		MonProxyFactory.enableInterface(params.isInterfaceEnabled);
		MonProxyFactory.enableSQLDetail(params.isSQLDetailEnabled);
		MonProxyFactory.enableSQLSummary(params.isSQLSummaryEnabled);
		MonProxyFactory
				.enableExceptionSummary(params.isExceptionSummaryEnabled);
		MonProxyFactory.enableExceptionDetail(params.isExceptionDetailEnabled);
		MonProxyFactory.enable(params.isEnabled);

		PreparedStatement ps = getPreparedStatement(conn);
		for (int i = 0; i < times; i++) {
			ps.executeQuery();
			testLoopResultSet(testStatement(conn));
			testLoopResultSet(testPreparedStatement(conn));
		}

		throwException(conn);

		monTotal.stop();
		String message = name + " execution time: " + mon.stop().getLastValue();
		System.out.println(message);

		printDebug(testCounter + ".1) " + "sqlBuffer", MonProxyFactory
				.getSQLDetail());
		printDebug(testCounter + ".2) " + "exceptionsBuffer", MonProxyFactory
				.getExceptionDetail());
		printDebug(testCounter + ".3) " + "jamon data - " + message,
				MonitorFactory.getRootMonitor().getBasicData());

		MonProxyFactory.resetSQLDetail();
		MonProxyFactory.resetExceptionDetail();
		MonitorFactory.reset();

	}

	private static int testCounter = 0;

	private static void testEquals() {

		Tag0 noProxyObj = new MyClass0();
		Tag0 proxyObj = (Tag0) MonProxyFactory.monitor(noProxyObj);
		Tag0 proxyObj2 = (Tag0) MonProxyFactory.monitor(noProxyObj);
		Tag0 proxyObjTwice = (Tag0) MonProxyFactory.monitor(proxyObj);

		System.out.println("\nAll of the following should equal true");
		System.out.println("proxy.equals(noproxy)="
				+ proxyObj.equals(noProxyObj));
		System.out.println("proxy.equals(proxy)=" + proxyObj.equals(proxyObj));
		System.out.println("proxy1.equals(proxy2)=" + proxyObj.equals(proxyObj2));
		System.out.println("proxy2.equals(proxy1)=" + proxyObj2.equals(proxyObj));
		System.out.println("proxyObjTwice.equals(proxy1)=" + proxyObj.equals(proxyObjTwice));
		System.out.println("noproxy.equals(noproxy)="
				+ noProxyObj.equals(noProxyObj));

		System.out.println("\nAll of the following will equal false");
		System.out.println("noproxy.equals(proxy)="
				+ noProxyObj.equals(proxyObj)+" - will be false as the nonproxied class will not no that a proxied class is the same.");
		System.out.println("proxyObjTwice.equals(null)=" + proxyObj.equals(null));



	}

	public static void main(String[] args) throws Exception {

		int times = 2000;
		Params params = new Params();
		FactoryEnabled mf = new FactoryEnabled();

		Class.forName("org.hsqldb.jdbcDriver");
		Connection conn = DriverManager
				.getConnection("jdbc:hsqldb:.", "sa", "");

		mainTestMethod("Non monitored connection first time", conn, times,
				params, mf);
		mainTestMethod("Non monitored connection second time", conn, times,
				params, mf);

		MonProxyFactory.enableAll(false);
		// returns regular connection
		conn = MonProxyFactory.monitor(conn);
		mainTestMethod(
				"MonProxyFactory disabled at creation of Connection (should be fast as regular connection)",
				conn, times, params, mf);

		// returns monitored connection
		MonProxyFactory.enableAll(true);
		conn = MonProxyFactory.monitor(conn);

		mainTestMethod("MonProxyFactory defaults first time", conn, times,
				params, mf);
		mainTestMethod("MonProxyFactory defaults second time", conn, times,
				params, mf);

		params = new Params();
		params.isResultSetEnabled = true;
		mainTestMethod("MonProxyFactory all enabled", conn, times, params, mf);

		params = new Params();
		params.isEnabled = false;
		mainTestMethod(
				"MonProxyFactory disabled (uses monitored connection but disabled)",
				conn, times, params, mf);

		params = new Params();
		params.isInterfaceEnabled = false;
		mainTestMethod("interface disabled", conn, times, params, mf);

		params = new Params();
		params.isInterfaceEnabled = false;
		params.isSQLDetailEnabled = false;
		mainTestMethod("sql detail and interface disabled", conn, times,
				params, mf);

		params = new Params();
		List list = new ArrayList();
		list.add("SYSTEM_TYPEINFO");// could be tables or other keywords
		list.add("LOCAL_TYPE_NAME");
		MonProxyFactory.setMatchStrings(list);
		mainTestMethod("MonProxy defaults with keyword match", conn, times,
				params, mf);
		MonProxyFactory.setMatchStrings(null);

		System.out.println("\n\n**** ResultSets *****");
		testDisplayResultSet(testStatement(conn));
		testDisplayResultSet(testPreparedStatement(conn));

		conn.close();

		printDebug("totals", mf.getRootMonitor().getBasicData());

		// Check to ensure all classes return all interfaces they implement
		testInterfaces(new MyClass0().getClass());
		testInterfaces(new MyClass1().getClass());
		testInterfaces(null);
		testInterfaces(String[].class);
		Object obj = new MyClass1();
		testInterfaces(obj.getClass());

		testEquals();

	}

}
