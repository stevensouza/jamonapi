package com.jamonapi.jmx;

import com.jamonapi.utils.Misc;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating configurable jamon jmx mbeans.  It will create jmx bean with time ranges for monitors with
 * units 'ms.'. With any other units these ranges won't be added to the mbean.  It can also create delta jmx
 * mbeans.  The user can create their own configurable jmx mbeans by configuring the jamonapi.properties file.
 */
public class MonitorMXBeanFactory {

    /**
     * This method takes a string and parses it by breaking it at ','.  It requires any number of
     * label, and units, and optionally can take 1 name. All of the following are valid:
     * <br>mylabel, myunits</br>
     * <br>mylabel, myunits, myname</br>
     * <br>mylabel1, myunits1, mylabel2, mylabel2</br>
     * <br>mylabel1, myunits1, mylabel2, mylabel2, myname</br>
     *
     * <p>If the number of values is odd then the last value is taken as the name for the jmx entry, or
     * else the first label will be used as the name.  Passing in both label, and units is required for all entries.
     * </p>
     *
     * @param propertyValue
     * @return
     */
     static List<JamonJmxBeanProperty> getJmxBeanProperties(String propertyValue) {
        if (propertyValue==null || propertyValue.trim().equals("")) {
            throw new RuntimeException("The passed in propertyValue can not be empty or null");
        }

        String[] propsArray = Misc.trim(propertyValue.split(","));
        if (propsArray.length<2) {
            throw new RuntimeException("Both JAMon monitor label and units are required to create a Jmx bean: "+propertyValue);
        }

        String name = null;
        if (isEven(propsArray.length)) {
            // if 'mylabel1, myunits1, mylabel2, myunits2' then use mylabel1 as name
            name = propsArray[0];
        } else {
            // if 'mylabel1, myunits1, mylabel2, myunits2, myname' then use myname as name
            name = propsArray[propsArray.length-1];
        }

        List<JamonJmxBeanProperty> jmxProperties = new ArrayList<JamonJmxBeanProperty>();
        // elements come in pairs (label, units) so divide by 2.
        int elements=propsArray.length/2;
        for (int i=0;i<elements;i++) {
            int labelIndex = i*2;
            int unitsIndex = labelIndex+1;
            jmxProperties.add(new JamonJmxBeanPropertyDefault(propsArray[labelIndex], propsArray[unitsIndex], name));
        }


        return jmxProperties;
    }

    static boolean isEven(int n) {
       return (n % 2) == 0;
    }

    public static MonitorMXBean create(String propertyValue) {
        List<JamonJmxBeanProperty> jmxProperties = getJmxBeanProperties(propertyValue);
        String units = jmxProperties.get(0).getUnits();
        MonitorMXBean bean = null;
        if ("ms.".equals(units)) {
            bean = new MonitorMsMXBeanImp(jmxProperties);
        } else {
            bean = new MonitorMXBeanImp(jmxProperties);
        }

        return bean;
    }

    @Deprecated
     static MonitorMXBean create(String label, String units, String name) {
         if (name == null || "".equals(name.trim())) {
             name = label;
         }

         return create(label + ", " + units + ", " + name);
    }

    public static MonitorMXBean createDelta(String propertyValue) {
        List<JamonJmxBeanProperty> jmxProperties = getJmxBeanProperties(propertyValue);
        String units = jmxProperties.get(0).getUnits();
        MonitorMXBean bean = null;
        if ("ms.".equals(units)) {
            bean = new MonitorDeltaMsMXBeanImp(jmxProperties);
        } else {
            bean = new MonitorDeltaMXBeanImp(jmxProperties);
        }

        return bean;
    }

    @Deprecated
     static MonitorMXBean createDelta(String label, String units, String name) {
        if (name == null || "".equals(name.trim())) {
            name = label;
        }

        return createDelta(label+", "+units+", "+name);
    }


    public static ObjectName getObjectName(MonitorMXBean beanImp) {
        return JmxUtils.getObjectName(beanImp.getClass().getPackage().getName() + ":type=current,name="+beanImp.getName());
    }

    public static ObjectName getDeltaObjectName(MonitorMXBean beanImp) {
        return JmxUtils.getObjectName(MonitorMXBean.class.getPackage().getName() + ":type=delta,name="+beanImp.getName());
    }

}
