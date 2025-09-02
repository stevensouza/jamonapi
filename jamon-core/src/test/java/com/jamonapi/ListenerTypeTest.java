package com.jamonapi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ListenerTypeTest {

    @Test
    public void testHasListener() {
        ListenerType lt = createListenerType();

        assertThat(lt.hasListener("cl1")).isTrue();
        assertThat(lt.hasListener("cl2")).isTrue();
        assertThat(lt.hasListener("buff1")).isTrue();
        assertThat(lt.hasListener("buff2")).isTrue();
        assertThat(lt.hasListener("buff3")).isTrue();
        assertThat(lt.hasListener("buff4")).isTrue();
        assertThat(lt.hasListener("IDoNotExist")).isFalse();

        lt.removeListener("buff4");
        assertThat(lt.hasListener("buff4")).isFalse();
    }


    @Test
    public void testListenerTypeData() {
        ListenerType lt = createListenerType();
        assertThat(toList(lt)).containsOnly("buff1","buff2","buff3","buff4","buff5");
        assertThat(lt.hasListener("cl2")).isTrue();
        assertThat(lt.hasListener("IDoNotExist")).isFalse();
        JAMonListener cl2=lt.getListener("cl2");

        lt.removeListener("buff4");
        assertThat(toList(lt)).containsOnly("buff1","buff2","buff3","buff5");

        lt.removeListener("cl2");
        assertThat(toList(lt)).containsOnly("buff1","buff2","buff5");

        lt.removeListener("buff1");
        assertThat(toList(lt)).containsOnly("buff2","buff5");

        lt.removeListener("buff2");
        assertThat(toList(lt)).containsOnly("buff5");

        lt.removeListener("buff5");
        assertThat(toList(lt)).isEmpty();
        assertThat(lt.hasListeners()).isFalse();

        lt.addListener(cl2);
        assertThat(lt.hasListeners()).isTrue();
        assertThat(toList(lt)).containsOnly("buff3");
    }


    private ListenerType createListenerType() {
        CompositeListener cl1=new CompositeListener("cl1");
        cl1.addListener(new JAMonBufferListener("buff1"));
        cl1.addListener(new JAMonBufferListener("buff2"));

        CompositeListener cl2=new CompositeListener("cl2");
        cl2.addListener(new JAMonBufferListener("buff3"));
        cl2.addListener(new JAMonBufferListener("buff4"));

        cl1.addListener(cl2);

        ListenerType lt=new ListenerType(new Object());
        lt.addListener(cl1);
        lt.addListener(new JAMonBufferListener("buff5"));
        return lt;
    }


    private static List<String> toList(ListenerType lt) {
        Object[][] data=lt.getData();
        int rows=(data==null) ? 0 : data.length;
        List<String> list=new ArrayList<String>();
        for (int i=0;i<rows;i++)
            for (int j=0;j<data[0].length;j++) {
                list.add(data[i][j].toString());
            }

        return list;
    }

}
