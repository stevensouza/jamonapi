package com.jamonapi.proxy;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.DataSource;
/** The datasource is incomplete.  the object factory is not done.  It should
 *  be able to wrap an existing DataSource however.
 */
public class JAMonDataSource implements DataSource, Referenceable, Serializable {
    private DataSource realDataSource;

    public JAMonDataSource(DataSource realDataSource) {
        this.realDataSource=realDataSource;
    }

    public JAMonDataSource() {

    }

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     */
    private static final long serialVersionUID = 0xABCDABC1;

    public Connection getConnection() throws SQLException {
        return MonProxyFactory.monitor(realDataSource.getConnection());
    }

    public Connection getConnection(String userName, String passWord) throws SQLException {
        return MonProxyFactory.monitor(realDataSource.getConnection(userName, passWord));
    }

    public int getLoginTimeout() throws SQLException {
        return realDataSource.getLoginTimeout();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return realDataSource.getLogWriter();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        realDataSource.setLoginTimeout(seconds);

    }

    public void setLogWriter(PrintWriter output) throws SQLException {
        realDataSource.setLogWriter(output);

    }

    public Reference getReference() throws NamingException {
        return ((Referenceable)realDataSource).getReference();
    }


    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        return realDataSource.isWrapperFor(arg0);
    }

    public <T> T unwrap(Class<T> arg0) throws SQLException {
        return realDataSource.unwrap(arg0);
    }

}
