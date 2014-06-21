package com.jamonapi.proxy;

import com.jamonapi.proxy.JAMonDriver.URLInfo;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;


public class JAMonDriverTest {

    @Test
    public void parseDriverUrl() {
        String driver="jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost";
        String realDriverName="com.sybase.jdbc3.jdbc.SybDriver";
        String realUrl="jdbc:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost&jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver2";
        realDriverName="com.sybase.jdbc3.jdbc.SybDriver2";
        realUrl="jdbc:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost&";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&HOSTNAME=myhost";
        realDriverName="com.sybase.jdbc3.jdbc.SybDriver";
        realUrl="jdbc:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:hsqldb:.jamonrealdriver = org.hsqldb.jdbcDriver;";
        realDriverName="org.hsqldb.jdbcDriver";
        realUrl="jdbc:hsqldb:.";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:hsqldb:.jamonrealdriver = org.hsqldb.jdbcDriver:";
        realDriverName="org.hsqldb.jdbcDriver";
        realUrl="jdbc:hsqldb:.";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:hsqldb:.jamonrealdriver = org.hsqldb.jdbcDriver";
        realDriverName="org.hsqldb.jdbcDriver";
        realUrl="jdbc:hsqldb:.";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:microsoft:sqlserver://localhost:1433;jamonrealdriver=com.microsoft.jdbc.sqlserver.SQLServerDriver";
        realDriverName="com.microsoft.jdbc.sqlserver.SQLServerDriver";
        realUrl="jdbc:microsoft:sqlserver://localhost:1433;";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:microsoft:sqlserver:sybase:Tds:127.0.0.1:5000/dbname;jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver";
        realDriverName="com.sybase.jdbc3.jdbc.SybDriver";
        realUrl="jdbc:microsoft:sqlserver:sybase:Tds:127.0.0.1:5000/dbname;";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);

        driver="jdbc:jamon:informix-sqli://161.144.202.206:3000:INFORMIXSERVER=stars:jamonrealdriver=   com.informix.jdbc.IfxDriver:";
        realDriverName="com.informix.jdbc.IfxDriver";
        realUrl="jdbc:informix-sqli://161.144.202.206:3000:INFORMIXSERVER=stars:";
        assertDriverInfoCorrect(driver, realDriverName, realUrl);
    }

    private void assertDriverInfoCorrect(String driver, String realDriverName, String realUrl) {
        URLInfo ui=new URLInfo(driver, null);
        assertThat(ui.getJAMonURL()).isEqualTo(driver);
        assertThat(ui.getRealDriverName()).isEqualTo(realDriverName);
        assertThat(ui.getRealURL()).isEqualTo(realUrl);
    }

}
