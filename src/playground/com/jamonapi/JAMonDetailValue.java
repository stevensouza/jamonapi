package com.jamonapi;

import java.util.Date;

public final class JAMonDetailValue {
	final String label;
	final double value;
	final long time;
	public JAMonDetailValue(String label,double value,long time) {
		this.label=label;
		this.value=value;
		this.time=time;
	}
	private Object[] row;

	public Object[] getRow() {
		if (row==null)
		 row = new Object[]{label,new Double(value), new Date(time)};
		
		return row;
	}
}
