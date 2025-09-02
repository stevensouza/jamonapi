package com.jamonapi.utils;

import java.io.Serializable;

/** Interface for array tabular data */
public interface DetailData extends Serializable {
    public String[] getHeader();
    public Object[][] getData();
}
