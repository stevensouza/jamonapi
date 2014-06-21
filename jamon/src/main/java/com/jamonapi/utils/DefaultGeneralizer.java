package com.jamonapi.utils;

import com.jamonapi.proxy.SQLDeArger;

public class DefaultGeneralizer implements Generalizer {

    /** Replaces numbers and quoted strings with '?'.  For example
     * <ul>
     *     <li>Original=ERROR Invalid login name:  'ssouza', 404
     *     --> becomes=ERROR Invalid login name:  ?,?
     * 
     *     <li>Original=ERROR Invalid login name:  ssouza, _404
     *     --> becomes (no change)=ERROR Invalid login name:  ssouza, _404
     * </ul>
     */
    public String generalize(String detail) {
        return new SQLDeArger(detail).getParsedSQL();
    }



}
