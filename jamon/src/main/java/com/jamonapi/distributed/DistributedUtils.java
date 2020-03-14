package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.FIFOBufferHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Get a list of listeners associated with a monitor.
 */
public class DistributedUtils {
    private static final String FIFO_BUFFER = "FIFOBuffer";
    static final String BUFFER_SUFFIX = "combined";
    static final int COMBINED_FIFO_BUFFER_SIZE = Integer.valueOf(JamonPropertiesLoader.PROPS.getProperty("monitorCompositeCombiner.combinedFifoBufferSize", "250"));
    private static final int INSTANCE_NAME_INDEX = 0;


    public static List<DistributedUtils.ListenerInfo> getAllListeners(Monitor monitor) {
        List<DistributedUtils.ListenerInfo> list = new ArrayList<>();
        // min, max, value, maxactive
        addAllListeners(list, monitor, "value");
        addAllListeners(list, monitor, "min");
        addAllListeners(list, monitor, "max");
        addAllListeners(list, monitor, "maxactive");
        return list;
    }

    /**
     * Copy
     *
     * @param from
     * @param to
     * @param numInstances Represents the total number of instances that will be sharing this buffer. This number is used to
     *                     determine how many rows from each instance are put into a FIFOBuffer. The amount of rows put in each
     *                     the buffer by each buffer is maxRowsInBuffer/numInstances. If maxRowsInBuffer=400 and numInstances=10 then
     *                     each instance could put a max of 40 rows in the buffer.
     *                     Note this number doesn't apply to other types of buffers such as max buffer where any server can dominate.
     */
    public static void copyJamonBufferListenerData(Monitor from, Monitor to, int numInstances) {
        List<DistributedUtils.ListenerInfo> listeners = DistributedUtils.getAllListeners(from);
        Iterator<DistributedUtils.ListenerInfo> iter = listeners.iterator();
        while (iter.hasNext()) {
            DistributedUtils.ListenerInfo listenerInfo = iter.next();
            // only copying jamon buffer listener data - for now. ignoring other listeners
            if (listenerInfo.getListener() instanceof JAMonBufferListener) {
                String fifoName = getBufferName(listenerInfo.getListener().getName());
                JAMonBufferListener fromJamonBufferListener = (JAMonBufferListener) listenerInfo.getListener();
                JAMonBufferListener toJamonBufferListener = null;
                // get/create JAMonBufferListener
                if (to.hasListener(listenerInfo.getListenerType(), fifoName)) {
                    toJamonBufferListener = (JAMonBufferListener) to.getListenerType(listenerInfo.getListenerType()).getListener(fifoName);
                } else {
                    toJamonBufferListener = createBufferListener(fifoName, fromJamonBufferListener);
                    toJamonBufferListener.getBufferList().reset();// copied buffer in from buffer listener, so it has data in it still.
                    to.addListener(listenerInfo.getListenerType(), toJamonBufferListener);
                }

                // populate buffer listener
                copyBufferListenerData(fromJamonBufferListener, toJamonBufferListener, numInstances);
            }
        }
    }

    /**
     * Copy
     *
     * @param from
     * @param to
     */
    public static void copyJamonBufferListenerData(Monitor from, Monitor to) {
        copyJamonBufferListenerData(from, to, 1);
    }

    /**
     * change monitor composites instanceName as well as all of its monKey instanceNames and instance names in any
     * JAMonBufferListener's.
     * <p>
     * Make a copy of the local monitor composite before passing to this method if you don't want
     * to change the instance name of the 'local' monitor which you probably don't.
     */
    public static MonitorComposite changeInstanceName(String instanceName, MonitorComposite monitorComposite) {
        List<Monitor> list = new MonitorCompositeIterator(Arrays.asList(monitorComposite)).toList();
        list.stream().forEach(mon -> {
            // done so monitors can be identified by instance when viewed in jamonadmin.jsp
            mon.getMonKey().setInstanceName(instanceName);
            changeJamonBufferListenerDataInstanceName(instanceName, mon);
        });

        return new MonitorComposite(list.toArray(new Monitor[]{})).setInstanceName(instanceName);
    }


    private static void changeJamonBufferListenerDataInstanceName(String instanceName, Monitor mon) {
        List<DistributedUtils.ListenerInfo> listeners = DistributedUtils.getAllListeners(mon);
        Iterator<DistributedUtils.ListenerInfo> iter = listeners.iterator();
        while (iter.hasNext()) {
            DistributedUtils.ListenerInfo listenerInfo = iter.next();
            // only copying jamon buffer listener data - for now. ignoring other listeners
            if (listenerInfo.getListener() instanceof JAMonBufferListener) {
                JAMonBufferListener jamonBufferListener = (JAMonBufferListener) listenerInfo.getListener();
                jamonBufferListener.getBufferList().getCollection().stream().forEach(bufferRow -> {
                    if (bufferRow instanceof JAMonDetailValue) {
                        JAMonDetailValue jaMonDetailValue = (JAMonDetailValue) bufferRow;
                        jaMonDetailValue.getMonKey().setInstanceName(instanceName);
                    } else if (bufferRow instanceof Object[]) {
                        Object[] row = (Object[]) bufferRow;
                        row[INSTANCE_NAME_INDEX] = instanceName;
                    }
                });
            }
        }
    }


    /**
     * Copy buffer data from the from/source listener to the to/destination buffer listener.
     *
     * @param from         source data
     * @param to           destination data
     * @param numInstances used to determine how many rows to put in from the fifo buffer.
     */

    private static void copyBufferListenerData(JAMonBufferListener from, JAMonBufferListener to, int numInstances) {
        if (from.hasData()) {
            Object[][] data = from.getBufferList().getDetailData().getData();
            int firstFifoIndexToBeAdded = getFifoFirstIndexToBeAdded(COMBINED_FIFO_BUFFER_SIZE, data.length, numInstances);
            int startIndex = isFifoBufferListener(from) ? firstFifoIndexToBeAdded : 0;
            // note fifo buffer only puts the most recent rows in and tries to put an equal amount in from each instance buffer.
            for (int i=startIndex; i<data.length; i++) {
                // note addRow will honor the rules of the given buffer listener. For example a fifo buffer listener will always
                // add the row, whereas a max listener will only add it if it is a new max.
                to.addRow(data[i]);
            }
        }
    }

    private static boolean isFifoBufferListener(JAMonBufferListener listener) {
        return listener.getBufferList().getBufferHolder() instanceof FIFOBufferHolder;
    }

    // If there are 10 instances adding to a fifo buffer of size 400 then each of them can only add a max of 40 rows.
    // Let's say an instances own buffer size is 100 then assuming 0 indexing the rows 60-99 would be added inclusive.
    // This example will be played out in the example below.
    static int getFifoFirstIndexToBeAdded(int combinedFifoBufferSize, int sourceBufferSize, int numInstances) {
        int numFifoBufferRowsToAdd = combinedFifoBufferSize / numInstances; // 400/10=40
        return numFifoBufferRowsToAdd >= sourceBufferSize ? 0 : sourceBufferSize - numFifoBufferRowsToAdd; // 100-40=60 (first index to add)
    }

    private static void addAllListeners(List<ListenerInfo> list, Monitor monitor, String listenerTypeName) {
        ListenerType listenerType = monitor.getListenerType(listenerTypeName);
        if (listenerType == null) {
            return;
        }

        JAMonListener listener = listenerType.getListener();
        if (listener == null) {
            return;
        }

        addAllListeners(list, listenerTypeName, listener);

    }

    private static void addAllListeners(List<ListenerInfo> list, String listenerTypeName, JAMonListener listener) {
        if (listener instanceof CompositeListener) {
            CompositeListener compositeListener = (CompositeListener) listener;
            Iterator iterator = compositeListener.iterator();
            while (iterator.hasNext()) {
                addAllListeners(list, listenerTypeName, (JAMonListener) iterator.next());
            }
        } else if (listener != null) {
            list.add(new ListenerInfo(listenerTypeName, listener));
        }

    }

    private static JAMonBufferListener createBufferListener(String name, JAMonBufferListener from) {
        JAMonBufferListener fifo = createBufferListener(from);
        fifo.getBufferList().setBufferSize(COMBINED_FIFO_BUFFER_SIZE);
        fifo.setName(name);
        return fifo;
    }

    // The following method tries to create a cloned version of the passed in JAMonBufferListener. If it fails
    // we don't want routine to fail so we fallback to creating a FIFO buffer. The primary reason it would fail is
    // for a required library to not exist and so a serialization error would occur.  This could happen for example if
    // the log4j library is not an available library and a log4j buffer listener was used.
    private static JAMonBufferListener createBufferListener(JAMonBufferListener from) {
        try {
            return (JAMonBufferListener) from.copy();
        } catch (RuntimeException e) {
            return (JAMonBufferListener) JAMonListenerFactory.get(FIFO_BUFFER);
        }
    }

    static String getBufferName(String name) {
        return name + "_" + BUFFER_SUFFIX;
    }


    public static class ListenerInfo {
        public ListenerInfo(String listenerType, JAMonListener listener) {
            this.listenerType = listenerType;
            this.listener = listener;
        }

        private String listenerType;
        private JAMonListener listener;

        public String getListenerType() {
            return listenerType;
        }

        public JAMonListener getListener() {
            return listener;
        }

    }
}
