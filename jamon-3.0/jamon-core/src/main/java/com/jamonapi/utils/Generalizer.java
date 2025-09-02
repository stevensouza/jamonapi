package com.jamonapi.utils;

/** This class interface will return a detail form in the getDetailLabel method which
 * is appropriate for logging.  getSummaryLabel returns a label that is appropriate for jamon
 * (i.e. not too unique).  toString() should always return the more general/summary label.
 * 
 * <p>Example:
 * <ol>
 *  <li>getDetailLabel()=select * from table where key=100 and name='mindy'
 *  <li>getLabel()=select * from table where key=? and name=?
 *  <li>toString()=getLabel()
 * </ol>
 */
public interface Generalizer {
    public String generalize(String detail);
}
