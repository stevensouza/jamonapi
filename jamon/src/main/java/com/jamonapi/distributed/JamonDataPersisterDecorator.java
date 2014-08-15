package com.jamonapi.distributed;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.FileUtils;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**  Class that wraps another JamonDataPersister and tracks its performance and any exceptions it may throw.
 * The exceptions and stack traces can be seen in jamon though they aren't bubbled up through the application.
 *
 * Created by stevesouza on 7/6/14.
 */

public class JamonDataPersisterDecorator implements JamonDataPersister {

    private LocalJamonDataPersister localJamonData;
    private JamonDataPersister jamonDataPersister;
    private Properties jamonProperties;

    public JamonDataPersisterDecorator() {
        this(null, new LocalJamonDataPersister());
    }

    public JamonDataPersisterDecorator(JamonDataPersister persister) {
        this(persister, new LocalJamonDataPersister());
    }

    JamonDataPersisterDecorator(JamonDataPersister persister, LocalJamonDataPersister localJamonData) {
        this.jamonDataPersister = persister;
        this.localJamonData = localJamonData;
        jamonProperties = JamonDataPersisterFactory.getJamonProperties();
    }

    @Override
    public Set<String> getInstances() {
        Set<String> allInstances = new TreeSet<String>(localJamonData.getInstances());
        allInstances.addAll(getDecoratedInstances());
        return allInstances;
    }

    @Override
    /** Put jamon data into the hazelcast map */
    public void put() {
        localJamonData.put();
        put(getInstance());
    }

    @Override
    public void put(String instanceKey) {
        Monitor mon = MonitorFactory.getTimeMonitor(getJamonLabel(".put()"));
        // only allow 1 process to put at the sametime.
        if (mon.getActive() < 1) {
            mon.start();
            try {
                if (instanceKey!=null) {
                  jamonDataPersister.put(instanceKey);
                }
            } catch(Throwable t) {
                MonitorFactory.addException(mon, t);
            } finally {
                mon.stop();
            }
        }
    }


    @Override
    public MonitorComposite get(String key) {
        MonitorComposite monitorComposite = localJamonData.get(key);
        if (monitorComposite == null) {
            Monitor mon = MonitorFactory.start(getJamonLabel(".get()"));
            try {
                monitorComposite = jamonDataPersister.get(key);
            } catch(Throwable t) {
                MonitorFactory.addException(mon, t);
                return localJamonData.get("local");
            } finally {
                mon.stop();
            }
        }

        return monitorComposite;
    }

    @Override
    public void remove(String instanceKey) {
        localJamonData.remove(instanceKey);
        Monitor mon = MonitorFactory.start(getJamonLabel(".remove()"));
        try {
           jamonDataPersister.remove(instanceKey);
        } catch(Throwable t) {
           MonitorFactory.addException(mon, t);
        } finally {
           mon.stop();
        }
    }

    @Override
    public String getInstance() {
       try {
           String prefix = jamonProperties.getProperty("jamonDataPersister.label.prefix");
           String label = jamonProperties.getProperty("jamonDataPersister.label");
           if ("".equals(label)) {
               label = jamonDataPersister.getInstance();
           }

           return FileUtils.makeValidFileName(prefix+label);
       } catch (Throwable t) {
          MonitorFactory.addException(t);
       }

        return null;
    }

    /**
     *
     * @return The wrapped/decorated JamonDataPersister
     */
    public JamonDataPersister getJamonDataPersister() {
        return jamonDataPersister;
    }



    // I don't ever want to not display data when there is a hazelcast error.
    private Set<String> getDecoratedInstances() {
        try {
            return jamonDataPersister.getInstances();
        } catch (Throwable e) {
            MonitorFactory.addException(e);
            Set<String> error=new HashSet<String>();
            error.add("ExceptionThrown");
            return error;
        }
    }

    private String getJamonLabel(String name) {
        return getClass().getCanonicalName() + name;
    }


}
