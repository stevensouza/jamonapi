package com.jamonapi.distributed;

import com.jamonapi.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Get a list of listeners associated with a monitor.
 */
class DistributedUtils {
    private static final String FIFO_BUFFER = "FIFOBuffer";
    static final String FIFO_BUFFER_SUFFIX = "combined";
    static final int DEFAULT_BUFFER_SIZE = 250;

    public static List<ListenerInfo> getAllListeners(Monitor monitor) {
        List<ListenerInfo> list = new ArrayList<ListenerInfo>();
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
     */
    public static void copyJamonBufferListenerData(Monitor from, Monitor to, String instanceName) {
        List<DistributedUtils.ListenerInfo> listeners = DistributedUtils.getAllListeners(from);
        Iterator<DistributedUtils.ListenerInfo> iter = listeners.iterator();
        while (iter.hasNext()) {
            DistributedUtils.ListenerInfo listenerInfo = iter.next();
            // only copying jamon buffer listener data - for now. ignoring other listeners
            if (listenerInfo.getListener() instanceof JAMonBufferListener) {
                String fifoName = getFifoBufferName(listenerInfo.getListener().getName());
                JAMonBufferListener fromJamonBufferListener = (JAMonBufferListener) listenerInfo.getListener();
                JAMonBufferListener toJamonBufferListener = null;
                if (to.hasListener(listenerInfo.getListenerType(), fifoName)) {
                    toJamonBufferListener = (JAMonBufferListener) to.getListenerType(listenerInfo.getListenerType()).getListener(fifoName);
                } else {
                    toJamonBufferListener = createBufferListener(fifoName, fromJamonBufferListener);
                    toJamonBufferListener.getBufferList().reset();
                    to.addListener(listenerInfo.getListenerType(), toJamonBufferListener);
                }

                copyBufferListenerData(fromJamonBufferListener, toJamonBufferListener, instanceName);
            }
        }
    }

    /**
     * Copy buffer data from the from/source listener to the to/destination buffer listener.
     *
     * @param from source data
     * @param to   destination data
     */
    private static void copyBufferListenerData(JAMonBufferListener from, JAMonBufferListener to, String instanceName) {
        if (from.hasData()) {
            final int INSTANCE_NAME_INDEX = 0;
            Object[][] data = from.getBufferList().getDetailData().getData();
            for (Object[] row : data) {
                // add instance name to monitor label - example: select * from table where name='steve' - (tomcat8_production).
                // The instanceName is passed in and changed as all of them are 'local' without doing that.
                // if the key setDetails was called label will be that value or else it will be the monitor label
                if (row != null && !"local".equals(instanceName)) {
                    row[INSTANCE_NAME_INDEX] = instanceName;
                }
                // note addRow will honor the rules of the given buffer listener. For example a fifo buffer listener will always
                // add the row, whereas a max listener will only add it if it is a new max.
                to.addRow(row);
            }
        }

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
        fifo.getBufferList().setBufferSize(DEFAULT_BUFFER_SIZE);
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

    static String getFifoBufferName(String name) {
        return name + "_" + FIFO_BUFFER_SUFFIX;
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
