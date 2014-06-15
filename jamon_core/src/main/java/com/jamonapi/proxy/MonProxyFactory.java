package com.jamonapi.proxy;



import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * This static MonProxyFactory simply uses an instance of MonProxyFactoryImp to do its work.  MonProxyFactoryImp can also be
 * created in its own right directly.  See its java docs for further info.
 * 
 * <p>Mainly kept this class for backwards compatibility reasons.</p>
 */

public class MonProxyFactory {

    private static final MonProxyFactoryImp proxyFactory=new MonProxyFactoryImp();

    public static void setLabelFactory(MonProxyLabelerInt factory) {
        proxyFactory.setLabelFactory(factory);
    }

    public static MonProxyLabelerInt getLabelFactory() {
        return proxyFactory.getLabelFactory();
    }


    public static MonProxy getMonProxy(Proxy proxy) {
        return proxyFactory.getMonProxy(proxy);
    }



    public static Object monitor(Object object) {
        return proxyFactory.monitor(object);
    }


    public static Object monitor(Object object, Class[] interfaces) {
        return proxyFactory.monitor(object, interfaces);
    }



    public static Object monitor(Object object, Class iface) {
        return proxyFactory.monitor(object, iface);
    }

    public static Connection monitor(Connection conn) {
        return proxyFactory.monitor(conn);
    }

    public static ResultSet monitor(ResultSet rs) {
        return proxyFactory.monitor(rs);
    }


    public static Statement monitor(Statement statement) {
        return proxyFactory.monitor(statement);
    }

    public static PreparedStatement monitor(PreparedStatement statement) {
        return proxyFactory.monitor(statement);
    }

    public static CallableStatement monitor(CallableStatement statement) {
        return proxyFactory.monitor(statement);
    }


    static Object monitorJDBC(Object object) {
        return proxyFactory.monitorJDBC(object);
    }


    public static Class[] getInterfaces(Class cls) {
        return proxyFactory.getInterfaces(cls);
    }




    public static int getExceptionBufferSize() {
        return proxyFactory.getExceptionBufferSize();
    }

    public static void setExceptionBufferSize(int exceptionBufferSize) {
        proxyFactory.setExceptionBufferSize(exceptionBufferSize);
    }

    public static void resetExceptionDetail() {
        proxyFactory.resetExceptionDetail();
    }



    public static boolean isInterfaceEnabled() {
        return proxyFactory.isInterfaceEnabled();
    }

    public static void enableInterface(boolean enable) {
        proxyFactory.enableInterface(enable);
    }



    public static boolean isExceptionSummaryEnabled() {
        return proxyFactory.isExceptionSummaryEnabled();
    }

    public static void enableExceptionSummary(boolean enable) {
        proxyFactory.enableExceptionSummary(enable);
    }

    public static boolean isExceptionDetailEnabled() {
        return proxyFactory.isExceptionDetailEnabled();
    }


    public static void enableExceptionDetail(boolean enable) {
        proxyFactory.enableExceptionDetail(enable);

    }




    public static boolean isSQLSummaryEnabled() {
        return proxyFactory.isSQLSummaryEnabled();
    }

    public static void enableSQLSummary(boolean enable) {
        proxyFactory.enableSQLSummary(enable);
    }



    public static boolean isSQLDetailEnabled() {
        return proxyFactory.isSQLDetailEnabled();
    }

    public static void enableSQLDetail(boolean enable) {
        proxyFactory.enableSQLDetail(enable);
    }


    public static boolean isResultSetEnabled() {
        return proxyFactory.isResultSetEnabled();
    }


    public static void enableResultSet(boolean enable) {
        proxyFactory.enableResultSet(enable);
    }


    public static boolean isEnabled() {
        return proxyFactory.isEnabled();
    }


    public static void enableAll(boolean enable) {
        proxyFactory.enableAll(enable);
    }

    public static boolean isAllEnabled() {
        return proxyFactory.isAllEnabled();
    }


    public static void enable(boolean enable) {
        proxyFactory.enable(enable);
    }

    static Params getParams() {
        return proxyFactory.getParams();
    }



    public static String[] getExceptionDetailHeader() {
        return proxyFactory.getExceptionDetailHeader();
    }


    public static Object[][] getExceptionDetail() {
        return proxyFactory.getExceptionDetail();
    }



    public static int getSQLBufferSize() {
        return proxyFactory.getSQLBufferSize();
    }


    public static void setSQLBufferSize(int sqlBufferSize) {
        proxyFactory.setSQLBufferSize(sqlBufferSize);
    }


    public static void resetSQLDetail() {
        proxyFactory.resetSQLDetail();
    }


    public static String[] getSQLDetailHeader() {
        return proxyFactory.getSQLDetailHeader();
    }


    public static Object[][] getSQLDetail() {
        return proxyFactory.getSQLDetail();
    }

    public static List getMatchStrings() {
        return proxyFactory.getMatchStrings();
    }


    public static void setMatchStrings(List ms) {
        proxyFactory.setMatchStrings(ms);
    }
}
