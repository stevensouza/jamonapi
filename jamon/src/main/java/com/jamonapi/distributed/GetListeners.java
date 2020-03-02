package com.jamonapi.distributed;

import com.jamonapi.CompositeListener;
import com.jamonapi.JAMonListener;
import com.jamonapi.ListenerType;
import com.jamonapi.Monitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Get a list of listeners associated with a monitor.
 */
class GetListeners {

    public List<ListenerInfo> getAll(Monitor monitor) {
        List<ListenerInfo> list = new ArrayList<ListenerInfo>();
        // min, max, value, maxactive
        addAllListeners(list, monitor, "value");
        addAllListeners(list, monitor, "min");
        addAllListeners(list, monitor, "max");
        addAllListeners(list, monitor, "maxactive");

        return list;
    }

    void addAllListeners(List<ListenerInfo> list, Monitor monitor, String listenerTypeName) {
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

    private void addAllListeners(List<ListenerInfo> list, String listenerTypeName, JAMonListener listener) {
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
