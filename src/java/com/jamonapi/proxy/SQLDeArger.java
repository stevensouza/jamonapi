package com.jamonapi.proxy;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fest.util.VisibleForTesting;

import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.AppMap;
/** SQLDeArger takes a sql statement and 1) replaces argument values ('souza', "souza", 'souza''s', 100, 100.5, 0xff, 10e9) with question marks
 *  It makes full sql statements look more like a prepared statement.  2) Returns a sql type which is simply the first word of the command
 *  (typically 'select', 'update' etc. 3) Returns any specified keywords that are in the parsed sql.  This is a good way to return table names.
 * 
 *  <p>A normal sql statement with argument values would generate too much data for JAMon and wouldn't be very good for understanding how your query
 *  performed.  Coneceptually the following queries are the same: 1) select * from table where name='steve', 2) select * from table where name='mindy'.
 *  However, if you passed both strings to jamon the 'sameness' wouldn't show up in the stats as each is a different string.  However by putting
 *  question marks in place of the values this problem can be resolved (i.e. select * from table where name=?).  One issue with the way this is done
 *  at this point is numbers or strings in other places can be replaced too. This shouldn't affect monitoring however.  For example
 *  This "select abs(200) from table", would be parsed to "select abs(?) from table".</p>
 * 
 *  <p>However, numbers of the format 100.00, really are multiple tokens. And will appear in the returned
 *   strings as ?.?.</p>
 * 
 *  <p>The class name SQLDeArger refers to the fact that argument values are removed from SQL statements.</p>
 * 
 *  <p>This class is also useful for logging sql statements.</p>
 */

public class SQLDeArger {
    private int parseSize;// size of the sql statement to parse
    private StringBuffer parsedSQL=new StringBuffer();// where the resulting sql statement will reside
    private char[] sqlChars;// characters in sql string
    private String sqlToParse;// original sql
    private int delimCounter=0;//character counter
    private int currentCharNum=0;// current character in sql that is being proceesed
    private boolean isInString=false;// indicates whether or not the parser is currently in a string i.e. 'steve'
    private char delim; // string delimeter which will be either ' or "
    private int totalDelims;// a counter of how many times delimeter appears in a string.
    private boolean firstToken=true;// the first token processed.  usually will be select, update, insert etc.
    private String sqlType;// the first token such as select, update, delete, insert
    private List matchStrings;// a list of strings to match in the sql statement.  table names are a good example
    private String[] matches;// the list of returned matches.
    @VisibleForTesting
    static final String THRESHOLD_EXCEEDED="sqlSizeExceedsThreshold";
    @VisibleForTesting
    static final String THRESHOLD_EXCEEDED_LENGTH="MonProxy-SQL-"+THRESHOLD_EXCEEDED+".length";

    private static Map sqlTypes=AppMap.createInstance();// case insensitive map.
    static {
        // These values are just checked for existance so the value can be null.  These indicate the type of sql statement.
        sqlTypes.put("select","select");
        sqlTypes.put("update","update");
        sqlTypes.put("delete","delete");
        sqlTypes.put("insert","insert");
        sqlTypes.put("truncate","truncate");
        sqlTypes.put(THRESHOLD_EXCEEDED,THRESHOLD_EXCEEDED);

        sqlTypes.put("exec","exec");
        sqlTypes.put("create","create");
        sqlTypes.put("drop","drop");
        sqlTypes.put("alter","alter");

        sqlTypes.put("commit","commit");
        sqlTypes.put("rollback","rollback");
        sqlTypes.put("save","save");

        sqlTypes.put("grant","grant");
        sqlTypes.put("revoke","revoke");
    }

    /** Accepts string to parse */
    public SQLDeArger(String sql) {
        this(sql,null);

    }


    /** Accepts strings to parse and a List of strings to check to see if they are in the sql statement.  A good use for this is to
     * pass table names into the constructor.  After the constructor is called the sql will already have been parsed
     *
     * @param sql
     * @param matchStrings
     */
    public SQLDeArger(String sql, List matchStrings) {
        this(sql, matchStrings, 0);
    }


    /** This constructor will truncate any sql statements longer than sqlMaxSize before the JAMon label is created
     * 
     * @param sql
     * @param matchStrings
     * @param sqlMaxSize
     */
    public SQLDeArger(String sql, List matchStrings, int sqlMaxSize) {
        if (sqlMaxSize>0 && sql.length()>sqlMaxSize) {
            int len=sql.length();
            sql=THRESHOLD_EXCEEDED+" query time (size="+len+")";
            MonitorFactory.add(THRESHOLD_EXCEEDED_LENGTH, "bytes", len);
        }
        this.sqlToParse=sql.trim()+" ";// ensures that the last character is always a space and not part of the query.
        parseSize=sqlToParse.length()-1; // exclude space which lets me not have to worry about next char.
        sqlChars=sqlToParse.toCharArray();// characters that will be parsed
        setMatchStrings(matchStrings);
        parse();
    }


    /** Parse the passed in where clause and break it along token lines.   */
    private SQLDeArger parse() {
        // tokens are strings broken on word boundaries like spaces
        while (hasTokens()) {
            String token=getNextToken();
            if (firstToken) {
                setSQLType(token);
                firstToken=false;
            }

            // if the token is a number such as 100, 100.5 0xff, 10E9 or a quoted string such as 'steve'
            // the replace it with a '?'.  The final case where token is null and still is in a string can happen more often
            // now that the sql length can arbitrarily be limited. For example: select * from table where name='ste
            // This would return null as the last token and yet be in string.  The following will return '?' in this case.
            token=(isFloatString(token) || isQuotedString(token) || (token==null && isInString())) ? "?" : token;
            parsedSQL.append(token);
        }

        parseMatches();

        return this;
    }


    /** Return sql with original argument values replaced with '?'.  For example:  select * from table where name=? */
    public String getParsedSQL() {
        return parsedSQL.toString();
    }


    /**Get sql that was passed in to parse. */
    public String getSQLToParse() {
        return sqlToParse;
    }


    /** Return the first word from the sql command.  These would include:  select, update, delete, create, insert, commit,...
     * If the word is not recognized then 'other' is returned.
     */
    public String getSQLType() {
        return sqlType;
    }


    /** Returns an array of Strings that matched the Strings specified in
     *  the matches arraylist.   Note that the matches are performed after arg
     *  values have been replaced on the sql with '?'.
     */
    public String[] getMatches() {
        return matches;
    }


    /** Returns true if there were any matches against the match Strings */
    public boolean hasMatches() {
        return (matches==null || matches.length==0) ? false : true;
    }


    /** Returns the number of matches or 0 if there were none */
    public int getNumMatches() {
        return (hasMatches()) ? matches.length : 0;
    }


    /** One for the statement, one for the keyword type of the statment, and the other numbers are for the matches */
    int getNumAll() {
        return 3+getNumMatches();
    }


    /** Return an array that has 1) all sql, 2) the sql type, 3) the parsed sql, 4) any matched strings if they exist.  The array will be at least
     *  2 long.   This is useful to pass all the strings in the array to jamon to track stats associated with the query.
     */
    String[][] getAll() {

        int size=getNumAll(); // 2 represents the sql and sqltype values, so that is always there.
        String[][] allData=new String[size][];

        // start at position 2 assigning any matches to the array.
        for (int i=0, j=0;i<size;i++) {
            allData[i]=new String[3];// ms., and value
            allData[i][1]=getSQLToParse();
            allData[i][2]="ms.";
            // note this loop matches one in SQLDeArgMon constructor and positions are important
            // The constructor must be changed if this method changes - kind of ugly...
            if (i==0)  // All
                allData[i][0]="All";
            else if (i==1) // SQL Type
                allData[i][0]=getSQLType();
            else if (i==2) // parsed SQL
                allData[i][0]=getParsedSQL();
            else // Matches in sql such as table names.
                allData[i][0]=matches[j++];
        }


        return allData;
    }


    /** Add string to see if it matches in the query */
    public void addMatchString(String matchString) {
        matchStrings.add(matchString);
    }


    /** Determine if the matches strings are in the parsed sql */
    private void parseMatches() {
        if (matchStrings!=null) {
            String sql=getParsedSQL();
            List matchesList=new ArrayList();

            Iterator iter=matchStrings.iterator();
            while (iter.hasNext()) {
                Object matchObj = iter.next(); // passed in matches such as table names
                String matchStr = (matchObj==null) ? null : matchObj.toString();
                if (sql.indexOf(matchStr)>0)// i.e. a match found
                    matchesList.add(matchStr);
            }

            matches=(String[]) matchesList.toArray(new String[0]);// convert matches into an array
        }

    }

    /** Note matchStrings should contain Strings.  If it doesn't toString() will be called on the objects */
    void setMatchStrings(List matchStrings) {
        this.matchStrings=matchStrings;
    }


    /** SQL types are the first word that is in a sql statement.  Examples are
     * insert, delete, update, and select.  However, any word that you add by calling this
     * method will be detected as a sql type.  Note the JDBCMonProxy uses this info
     * to add a monitor for whenever a select, insert etc are executed.  This gives the number
     * of times and performances of the sql types.  A list of all the default
     * sql types follows: select, update, delete, insert, truncate, exec, create, drop, alter
     * commit, rollback, grant, revoke, save.  Any value that isn't on the list will return
     * 'other'.	The getSQLType method returns the SQL type value in the sql statement passed to the
     * constructor.
     * 
     * @param type
     */
    public static void putSQLType(String type) {
        sqlTypes.put(type,type);
    }

    private void setSQLType(String type) {
        sqlType = (String)sqlTypes.get(type);
        if (sqlType==null)
            sqlType="other";

    }

    // returns true as long as there are characters to process
    private boolean hasTokens() {
        return (currentCharNum<parseSize) ? true : false;
    }

    // Returns String tokens such as: select, from, where, table, etc.
    private String getNextToken() {
        int start=currentCharNum;
        int end=0;

        // loop until word boundary
        while (end==0 && currentCharNum<parseSize) {
            setStringDelim();

            // an example of a word boundary would be next char of space while not in a string, or a puctuation mark
            if (isWordBoundary(getCurrentChar(), getNextChar()) )
                end=currentCharNum+1;

            currentCharNum++;
        }

        // return the word as a token if one was found
        if (end>0)
            return sqlToParse.substring(start,end);
        else
            return null;


    }

    // determine if the character is punctuation or not.
    private boolean isPunctuation(char ch) {
        return (!isInString() && !Character.isLetterOrDigit(ch));
    }

    private boolean isWordBoundary(char currentCh, char nextCh) {
        // word boundaries are special puncutation, when not in a string.
        // select * from table where key=100
        // first part of conditional would be triggered with convert(200. if currentchar was '('
        // 2nd part:  select *, convert(char(20)... from.  if characther is 't' of select or 't'
        // of 'convert'
        return (isPunctuation(currentCh) && Character.isLetterOrDigit(nextCh)) ||
        (!isInString() && (Character.isWhitespace(nextCh) || !Character.isLetterOrDigit(nextCh)));
    }

    // Indicates if the character is embedded in a where clause string surrounded by ' or ".
    // i.e. this would return true for any characters in between double quotes "steve's"
    private boolean isInString() {
        return isInString;
    }

    private void setIsInString(boolean isInString) {
        this.isInString=isInString;
    }

    // can either be ' or "
    private void setStringDelim(char delim) {
        this.delim=delim;
    }

    private char getStringDelim() {
        return delim;
    }


    // Get the current character of processing
    private char getCurrentChar() {
        return sqlChars[currentCharNum];
    }

    // get next charater of processing.
    private char getNextChar() {
        return sqlChars[currentCharNum+1];
    }

    // Determine if the delimeter is " or '
    private void setStringDelim() {
        // if first delimiter in a string like 'steve' then prepare for a string to be
        // processed

        // if in the string count delimeters to know when to end.
        boolean isDelim=(isInString() && getCurrentChar()==getStringDelim());
        if (isDelim)
            totalDelims++;

        if (!isInString() && (getCurrentChar()=='\'' || getCurrentChar()=='"')) {
            delimCounter++;
            totalDelims++;
        } // else it is the last ' or " in 'steve' , 'mindy''s'''
        else if (isInString() && getCurrentChar()==getStringDelim() && getNextChar()!=getStringDelim() && totalDelims%2==0) {
            delimCounter--;
        }


        if (!isInString() && delimCounter==1) {
            setStringDelim(getCurrentChar());
            setIsInString(true);
        } else if (isInString() && delimCounter==0)
            setIsInString(false);


    }


    // Any token that starts with a 0 returns true.  i.e. 1000 0xFF, 10E9, would return true.
    private  boolean isFloatString(String str){
        // if null string or the first character is not a digit then this is not a number
        if (str==null || !Character.isDigit(str.charAt(0)))
            return false;
        else // else it is a number
            return true;
    }


    private  boolean isQuotedString(String str) {
        if (str==null || "".equals(str.trim()))
            return false;

        char first=str.charAt(0);
        char last=str.charAt(str.length()-1);

        // either 'souza' or "souza" is a good quoted string.
        if ((first=='\'' || first=='"') && first==last)
            return true;
        else
            return false;
    }

}
