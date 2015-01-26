package com.jamonapi;

import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

// also try to test by putting arguments in the command line.  They should take precedence over the config file:
// -DdistributedDataRefreshRateInMinutes=20 -DjamonDataPersister=MYPERSISTER
public class JamonPropertiesLoaderTest {

    @Test
    public void shouldUseDefaults() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("I_DO_NOT_EXIST.properties");
        Properties props = loader.getJamonProperties();
        assertThat(props.getProperty("distributedDataRefreshRateInMinutes")).isEqualTo("5");
        assertThat(props.getProperty("jamonDataPersister")).isEqualTo("com.jamonapi.distributed.HazelcastFilePersister");
        assertThat(props.getProperty("jamonDataPersister.label")).isEqualTo("");
        assertThat(props.getProperty("jamonDataPersister.label.prefix")).isEqualTo("");
        assertThat(props.getProperty("jamonDataPersister.directory")).isEqualTo("jamondata");
        assertThat(props.getProperty("jamonListener.type")).isEqualTo("value");
        assertThat(props.getProperty("jamonListener.name")).isEqualTo("FIFOBuffer");
        assertThat(props.getProperty("jamonListener.size")).isEqualTo("50");
        assertThat(props.getProperty("jamonJmxBean.size")).isEqualTo("50");
    }

    @Test
    public void shouldUseFile() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        Properties props = loader.getJamonProperties();
        assertThat(props.getProperty("distributedDataRefreshRateInMinutes")).isEqualTo("2");
        assertThat(props.getProperty("jamonDataPersister")).isEqualTo("com.jamonapi.distributed.DistributedJamonHazelcastPersister2");
        assertThat(props.getProperty("jamonDataPersister.label")).isEqualTo("myapplication name");
        assertThat(props.getProperty("jamonDataPersister.label.prefix")).isEqualTo("myprefix:");
        assertThat(props.getProperty("jamonDataPersister.directory")).isEqualTo("persistence/jamondata");
    }


    @Test
    public void configDirectory() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        assertThat(loader.getPropertiesDirectory().toString()).contains("file:/");
    }

    @Test
    public void shouldReturnDefaultListeners() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("I_DO_NOT_EXIST.properties");
        List<JamonPropertiesLoader.JamonListener> listeners = loader.getListeners();

        assertThat(listeners).hasSize(0);
    }

    @Test
    public void shouldReturnListeners() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        List<JamonPropertiesLoader.JamonListener> listeners = loader.getListeners();

        assertThat(listeners).hasSize(4);
        JamonPropertiesLoader.JamonListener listener = listeners.get(0);
        assertThat(listener.getLabel()).isEqualTo("com.jamonapi.Exceptions");
        assertThat(listener.getUnits()).isEqualTo("Exception");
        assertThat(listener.getListenerType()).isEqualTo("value");
        assertThat(listener.getListenerName()).isEqualTo("FIFOBuffer");

        listener = listeners.get(1);
        assertThat(listener.getLabel()).isEqualTo("java.lang.RuntimeException");
        assertThat(listener.getUnits()).isEqualTo("Exception");
        assertThat(listener.getListenerType()).isEqualTo("value");
        assertThat(listener.getListenerName()).isEqualTo("FIFOBuffer");

        // This one tests defaults as value and FIFOBuffer aren't in the properties file.
        listener = listeners.get(2);
        assertThat(listener.getLabel()).isEqualTo("com.jamonapi.http.JAMonJettyHandlerNew.request.allPages");
        assertThat(listener.getUnits()).isEqualTo("ms.");
        assertThat(listener.getListenerType()).isEqualTo("value");
        assertThat(listener.getListenerName()).isEqualTo("FIFOBuffer");

        listener = listeners.get(3);
        assertThat(listener.getLabel()).isEqualTo("com.jamonapi.log4j.JAMonAppender.ERROR");
        assertThat(listener.getUnits()).isEqualTo("log4j");
        assertThat(listener.getListenerType()).isEqualTo("value");
        assertThat(listener.getListenerName()).isEqualTo("FIFOBuffer");
    }

    @Test
    public void shouldResetListeners() {
        // test for bug where running multiple calls to getListeners didn't first remove old listeners.
        // so wasn't indempotent
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        loader.getListeners();
        List<JamonPropertiesLoader.JamonListener> listeners = loader.getListeners();

        assertThat(listeners).hasSize(4);
    }

    @Test
    public void shouldReturnJmxBeans() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        List<JamonPropertiesLoader.JamonJmxBeanProperty> mxBeans = loader.getMxBeans();

        assertThat(mxBeans).hasSize(3);

        JamonPropertiesLoader.JamonJmxBeanProperty mxBean = mxBeans.get(0);
        assertThat(mxBean.getLabel()).isEqualTo("com.jamonapi.http.JAMonJettyHandlerNew.request.allPages");
        assertThat(mxBean.getUnits()).isEqualTo("ms.");
        assertThat(mxBean.getName()).isEqualTo("JettyPageRequests");

        mxBean = mxBeans.get(1);
        assertThat(mxBean.getLabel()).isEqualTo("MonProxy-SQL-Type: All");
        assertThat(mxBean.getUnits()).isEqualTo("ms.");
        assertThat(mxBean.getName()).isEqualTo("Sql");

        mxBean = mxBeans.get(2);
        assertThat(mxBean.getLabel()).isEqualTo("MonProxy-SQL-Type: All");
        assertThat(mxBean.getUnits()).isEqualTo("ms.");
        assertThat(mxBean.getName()).isEqualTo("");
    }
}
