package com.jamonapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class that allows for iterating a collection that contains multiple instances of MonitorComposites and iterates
 * through them as if they were one MonitorComposite.  A sort of composite/collection of MonitorComposites.
 *
 * Created by stevesouza on 8/16/14.
 * @since  2.79
 */
    public class MonitorCompositeIterator implements Iterator<Monitor> {
    private Iterator<MonitorComposite> iter;

    private MonitorComposite currentMonitorComposite;
    private int index=-1;

    public MonitorCompositeIterator(Collection<MonitorComposite> monitorComposites) {
        iter = monitorComposites.iterator();
        currentMonitorComposite = iter.next();
    }

    @Override
    public boolean hasNext() {
      boolean hasMore;
      if (index < currentMonitorComposite.getNumRows() - 1) {
        hasMore = true;
      } else {
        hasMore = setNextMonitorComposite();
      }

      index++;
      return hasMore;
    }

    private boolean setNextMonitorComposite() {
        index = -1;
        if (iter.hasNext()) {
          currentMonitorComposite = iter.next();
          return currentMonitorComposite.getNumRows() > 0;
        }

        return false;
    }

    @Override
    public Monitor next() {
       return currentMonitorComposite.getMonitors()[index];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in this class");
    }

    public MonitorComposite getCurrentMonitorComposite() {
        return currentMonitorComposite;
    }

    public MonitorComposite toMonitorComposite() {
        return new MonitorComposite(toList().toArray(new Monitor[]{}));
    }
    /** Return all the MonitorComposites passed into the constructor as one List */
    public List<Monitor> toList() {
        List<Monitor> list = new ArrayList<Monitor>();
        while (hasNext()) {
            Monitor mon = next();
            if (!currentMonitorComposite.isLocalInstance()) {
              // done so monitors can be identified by instance when viewed in jamonadmin.jsp
              // Don't want to do this for the local instance as it is not needed as it is the default and
              // it is the only one that is not a copy/clone of the original.
              mon.getMonKey().setInstanceName(currentMonitorComposite.getInstanceName());
            }

            list.add(mon);
        }

        return list;
    }
}
