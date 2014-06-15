package com.jamonapi.utils;

import java.util.*;

/** This object can contain a configurable number of items in a buffer. The items kept are rows in a 2 dim
 *  array and so the data can be viewed in a table.  It is used in jamon to store recent exceptions, and 
 *  sql queries from the various proxy classes.  However it may be used elsewhere.  It is thread safe.
 *  By default the buffer holds 50 elements, but this can be overridden in the constructor.
 * @author steve souza
 *
 */

public class BufferList {

	    private boolean enabled=true;
	    private int bufferSize=50;
	    private String[] header;
	    private LinkedList bufferList=new LinkedList();
	
	    /** Constructor that takes the header of the structure of the rows that are stored.
	     *  For example header could be {"Time", "Exception info",};
	     * @param header
	     */
	    
	    
	    public BufferList(String[] header) {
	    	this.header=header;
	    }
	    
	    public BufferList(String[] header, int bufferSize) {
	    	this.header=header;
	    	this.bufferSize=bufferSize;
	    }
	    
	       
	      /** 
	       * Get the number of Exceptions that can be stored in the buffer before the oldest entries must
	       * be removed.
	       * 
	       */
	      public synchronized int getBufferSize() {
	        return bufferSize;
	      }
	      
	      /** 
	       * Set the number of Exceptions that can be stored in the buffer before the oldest entries must
	       * be removed.  A value of 0 will disable the collection of Exceptions in the buffer.  Note if 
	       * MonProxy is disabled exceptions will also not be put in the buffer.
	       * 
	       */
	      
	      public synchronized void setBufferSize(int newBufferSize) {
	    	  
	    	if (bufferSize>newBufferSize)
	    	  resetBuffer(reduceBuffer(newBufferSize));
	    	
	        bufferSize=newBufferSize;
	        
	      }
          
          /** Reduce size of buffer while not losing current elements */
          private LinkedList reduceBuffer(int newSize) {
              LinkedList newBuffer=new LinkedList();
              Collections.reverse(bufferList);// reverse to save the most recent values
              Iterator iter=bufferList.iterator();
              int i=0;
              while (iter.hasNext() && i<newSize) {
                  newBuffer.add(iter.next());
                  i++;
              }
              
              return newBuffer;
          }
	      
	      /** 
	       * Remove all Exceptions from the buffer.
	       *
	       */
	      public synchronized void resetBuffer() {
	        resetBuffer(new LinkedList());  
	      }
          
          private void resetBuffer(LinkedList bufferList) {
              this.bufferList=bufferList;
              
          }
          
          public boolean isEmpty() {
        	  return getRowCount()==0;  
          }
          
          public boolean hasData() {
        	  return !isEmpty();
          }
          
          public int getRowCount() {
        	  return (bufferList==null) ? 0 : bufferList.size();  
          }
	      
	      
	      /** Returns true if MonProxy is enabled.  */
	      public synchronized boolean isEnabled() {
	        return enabled;
	      }
	      
	      /** Enable monitoring */
	      public synchronized void enable() {
	        enabled=true;
	      }
	      
	      /** Disable monitoring */
	      public synchronized void disable() {
	        enabled=false;
	      }
	      
	      /** Reset BufferList.  It will empty the buffer and leave its size at the current value */
	      public synchronized void reset() {
	    	bufferList=new LinkedList();
	      }
	      
	      /** Get the header that can be used to display the Exceptions buffer */
	      public String[] getHeader() {
	        return header;
	      }
	      
	      /** Get the exception buffer as an array, so it can be displayed */
	      public Object[][] getData() {
	          
	        Object[] bufferListArray=null;
	        synchronized(this) {
	          if (bufferList.size()>0) 
	        	  bufferListArray= bufferList.toArray();
	        }
	            
	        if (bufferListArray==null)
	          return null;
	        else {
	          Object[][] data=new Object[bufferListArray.length][];
	          for (int i=0;i<bufferListArray.length;i++) {
	        	  if (bufferListArray[i] instanceof Object[])
	                data[i]=(Object[])bufferListArray[i];
	        	  else
	                data[i]=new Object[]{bufferListArray[i]};
	          }
	          
	          return data; 
	        } 
	      }
	      
	   
	      /** Add a row to be held in the buffer.  If the buffer is full the oldest one will be removed.  */
	      public synchronized void addRow(Object[] row) {
	    	  addRow((Object)row);              
	      }
	      
	      public synchronized void addRow(Object obj) {
		    	 if (!enabled || bufferSize<=0)
			    	   return;

			     // remove the oldest element if the buffer is to capacity.  
			     if (bufferList.size()==bufferSize)
			        bufferList.removeFirst();
			            
			     // Always add the new item
			     bufferList.addLast(obj);	    	  
	      }
	          


}
