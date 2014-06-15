package com.jamonapi;

import com.jamonapi.utils.*;
import java.util.Date;

public  class JAMonBufferListener implements JAMonListener {

	BufferList list=new BufferList(new String[] {"Label","Value","Date"},500);
	
	//private static JAMonValue jval=new JAMonValue("label", (double)100.0,100L);
	public void processEvent(Monitor mon) {
   //   list.addRow(new JAMonValue("label", value, System.currentTimeMillis()));
//		list.addRow(new Object[]{"label", new Double(value), new Date(System.currentTimeMillis())});
	//	list.addRow(jval);
		// the following use of protected variables makes a 10% performance improvement.
	//	list.addRow(new JAMonDetailValue(stats.getDetailLabel(),bs.lastValue,bs.lastAccess));
		list.addRow(mon.getJAMonDetailValue());
	//	list.addRow(new JAMonValue(stats.getDetailLabel(),stats.getLastValue(),(long)stats.getHits()));
					
	}
	
	public BufferList getBufferList() {
		return list;
	}
	
//	private static final class JAMonValue {
//		private String label;
//		private double value;
//		private long time;
//		public JAMonValue(String label,double value,long time) {
//			this.label=label;
//			this.value=value;
//			this.time=time;
//		}
//		
//		public Object[] getRow() {
//			return new Object[]{label,new Double(value), new Date(time)};
//		}
//	}

}
