package com.jamonapi.proxy;

import com.jamonapi.MonitorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class SQLDeArgerTest {

    @Test
    public void testStandardSqlStatements() {
        String sql="select col0, 'mindy', 'mindy''s', 'mindy''s''''', col2, convert('steve',100, 0xff, 100.0),* from table where salary>=100000 or salary<=200000 or name like 'steve%' and name in ('mindy','steve') and short= 20 or sand='no sand' or short=30 and sand='no sand' and sand='no sand' or salary in (select max(salary/2),100 from emps group by name having count(*)>5)";
        String parsedSql="select col0, ?, ?, ?, col2, convert(?,?, ?, ?.?),* from table where salary>=? or salary<=? or name like ? and name in (?,?) and short= ? or sand=? or short=? and sand=? and sand=? or salary in (select max(salary/?),? from emps group by name having count(*)>?)";
        assertStandardSql(sql, parsedSql, "select", false, 0);

        sql="select ?,*,? from table where name like ?";
        parsedSql="select ?,*,? from table where name like ?";
        assertStandardSql(sql, parsedSql, "select", false, 0);

        sql="select 'mindy', \"mindy\" from table where name like 'mindy'";
        parsedSql="select ?, ? from table where name like ?";
        assertStandardSql(sql, parsedSql, "select", false, 0);

        sql="     delete from table where  key name like          'mindy%'   ";
        parsedSql="delete from table where  key name like          ?";
        assertStandardSql(sql, parsedSql, "delete", false, 0);

        sql="myproc";
        parsedSql="myproc";
        assertStandardSql(sql, parsedSql, "other", false, 0);

        sql="exec myproc";
        parsedSql="exec myproc";
        assertStandardSql(sql, parsedSql, "exec", false, 0);

        // max size for sql is set but query is under that.
        sql="select 'hi',* from table where name=1234";
        parsedSql="select ?,* from table where name=?";
        assertStandardSql(sql, parsedSql, "select", false, 40);

        // SQLDeArger works with any string, not just sql
        sql="pageHits 'ssouza' jamon mb 100.5:pageHits jamon:pagehits.ssouza";
        parsedSql="pageHits ? jamon mb ?.?:pageHits jamon:pagehits.ssouza";
        assertStandardSql(sql, parsedSql, "other", false, 0);
    }

    @Test
    public void testSqlStatementsWithMatches() {
        String sql="select * from employees as e, customers as c, dependents as d where e.id=c.id and c.id=d.id and e.name in (select * from favorite where name like 'j%' and salary > 50000)";
        String parsedSql="select * from employees as e, customers as c, dependents as d where e.id=c.id and c.id=d.id and e.name in (select * from favorite where name like ? and salary > ?)";
        assertStandardSql(sql, parsedSql, "select", true, 0);

        sql="select * from employees as e, customers as c, dependents as d where e.id=c.id and c.id=d.id and e.name not in ('steve','souza','jeff','beck') and salary in (100000,20000, 50000) and age!=50";
        parsedSql="select * from employees as e, customers as c, dependents as d where e.id=c.id and c.id=d.id and e.name not in (?,?,?,?) and salary in (?,?, ?) and age!=?";
        assertStandardSql(sql, parsedSql, "select", true, 0);

        sql="select 10_name_10, name_10_name,* from employees where age<50 and age!=10.0 and age!=-50.0 and age < 0xFF and age < 0x0123456789aAbBcCdDeEfF and age<+10E09";
        parsedSql="select ?_name_?, name_?_name,* from employees where age<? and age!=?.? and age!=-?.? and age < ? and age < ? and age<+?";
        assertStandardSql(sql, parsedSql, "select", true, 0);

        sql="select 10/22/06, date102206, m10/d22/y06, 10name, $10_name, _10_name, 10_name_10, name_10_na1010me,* from employees where age<50 and age!=10.0 and age!=-50.0 and age < 0xFF and age < 0x0123456789aAbBcCdDeEfF and age<+10E09 and age<1.72E3F";
        parsedSql="select ?/?/?, date102206, m10/d22/y06, ?, $?_name, _?_name, ?_name_?, name_?_na1010me,* from employees where age<? and age!=?.? and age!=-?.? and age < ? and age < ? and age<+? and age<?.?";
        assertStandardSql(sql, parsedSql, "select", true, 0);
    }

    @Test
    public void testSqlExceedsMaxSize() {
        int charactersInQuery=0;
        charactersInQuery+=assertExceedsThresholdSql("select 'hi'",5);
        charactersInQuery+=assertExceedsThresholdSql("select 'hi'",8);
        charactersInQuery+=assertExceedsThresholdSql("select 'hi'",9);
        charactersInQuery+=assertExceedsThresholdSql("select 'hi',* from table where name='joel' and age=20",40);
        charactersInQuery+=assertExceedsThresholdSql("select 'hi',* from table where date='10/12/13'",40);
        charactersInQuery+=assertExceedsThresholdSql("select 'hi',* from table where name='1234",40);
        charactersInQuery+=assertExceedsThresholdSql("select 'hi',fname, lname, * from table",30);
        assertThat(MonitorFactory.getMonitor(SQLDeArger.THRESHOLD_EXCEEDED_LENGTH, "bytes").getHits()).isEqualTo(7);
        assertThat(MonitorFactory.getMonitor(SQLDeArger.THRESHOLD_EXCEEDED_LENGTH, "bytes").getTotal()).isEqualTo(charactersInQuery);
    }

    private void assertStandardSql(String sql, String parsedSql, String sqlType, boolean hasMatches, int maxSqlSize) {
        SQLDeArger sqlDearger=createSqlDearger(sql, maxSqlSize);
        // The extra whitespace on the end of the string to parse should be fixed, but was done as it simplified code.
        assertThat(sqlDearger.getSQLToParse()).isEqualTo(sql.trim()+" ");
        assertThat(sqlDearger.getParsedSQL()).isEqualTo(parsedSql);
        assertThat(sqlDearger.getSQLType()).isEqualTo(sqlType);
        assertThat(sqlDearger.hasMatches()).isEqualTo(hasMatches);
        if (sqlDearger.hasMatches()) {
            assertThat(sqlDearger.getNumMatches()).isGreaterThanOrEqualTo(1);
            assertThat(sqlDearger.getAll().length).isGreaterThanOrEqualTo(4);

        } else {
            assertThat(sqlDearger.getNumMatches()).isEqualTo(0);
            assertThat(sqlDearger.getAll().length).isEqualTo(3);
        }
    }

    // returns excess bytes if there were any
    private int assertExceedsThresholdSql(String sql, int maxSqlSize) {
        SQLDeArger sqlDearger=createSqlDearger(sql, maxSqlSize);
        // The extra whitespace on the end of the string to parse should be fixed, but was done as it simplified code.
        assertThat(sqlDearger.getSQLToParse()).isEqualTo("sqlSizeExceedsThreshold query time (size="+sql.length()+") ");
        assertThat(sqlDearger.getParsedSQL()).isEqualTo("sqlSizeExceedsThreshold query time (size=?)");
        assertThat(sqlDearger.getSQLType()).isEqualTo(SQLDeArger.THRESHOLD_EXCEEDED);
        assertThat(sqlDearger.hasMatches()).isEqualTo(false);
        assertThat(sqlDearger.getNumMatches()).isEqualTo(0);
        assertThat(sqlDearger.getAll().length).isEqualTo(3);
        return (sql.length()>maxSqlSize) ? sql.length() : 0;
    }

    private static SQLDeArger createSqlDearger(String sql, int sqlMaxSize) {
        List matches=new ArrayList();
        matches.add(" employees ");
        matches.add(" dependents ");
        matches.add(" orders ");
        return new SQLDeArger(sql, matches, sqlMaxSize);
    }

}
