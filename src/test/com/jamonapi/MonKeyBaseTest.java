package com.jamonapi;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.junit.Test;


public class MonKeyBaseTest {


    @Test
    public void testEquality() {
        LinkedHashMap<String, Object> steveMap=new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> mindyMap=new LinkedHashMap<String, Object>();

        steveMap.put("fn",new MonKeyItemBase("steve summary","steve detail"));
        steveMap.put("ln","souza");
        steveMap.put("age", new Long(44));

        mindyMap.put("fn","mindy");
        mindyMap.put("ln","bobish");
        mindyMap.put("age", new Long(33));

        MonKey steveMonKey=new MonKeyBase("Steves Range", steveMap);
        MonKey steveMonKey2=new MonKeyBase(steveMap);
        // don't need details to match...
        assertThat(steveMonKey).isEqualTo(steveMonKey2);

        MonKey mindyMonKey=new MonKeyBase(mindyMap);
        // map does have to match
        assertThat(steveMonKey).isNotEqualTo(mindyMonKey);
    }

    @Test
    public void testGetValueAndData() {
        LinkedHashMap<String, Object> steveMap=new LinkedHashMap<String, Object>();
        steveMap.put("fn",new MonKeyItemBase("steve summary","steve detail"));
        steveMap.put("ln","souza");
        steveMap.put("age", new Long(44));
        MonKey steveMonKey=new MonKeyBase("Steves Range", steveMap);

        // getting values out assertions
        assertThat(steveMonKey.getValue("fn").toString()).isEqualTo("steve summary");
        assertThat(steveMonKey.getValue("ln").toString()).isEqualTo("souza");
        assertThat((Long)steveMonKey.getValue("age")).isEqualTo(44);
        assertThat(steveMonKey.getRangeKey()).isEqualTo("Steves Range");

        // header assertions
        assertThat(steveMonKey.getBasicHeader(new ArrayList())).containsOnly("Label");
        assertThat(steveMonKey.getHeader(new ArrayList())).containsExactly("fn","ln","age");
        assertThat(steveMonKey.getDisplayHeader(new ArrayList())).containsExactly("fn","ln","age");

        // data assertions
        assertThat(steveMonKey.getBasicRowData(new ArrayList()).get(0).toString()).isEqualTo("steve summary, souza, 44");
        assertThat(steveMonKey.getRowData(new ArrayList())).containsExactly(new MonKeyItemBase("steve summary","steve detail"),"souza",new Long(44));
        assertThat(steveMonKey.getRowDisplayData(new ArrayList())).containsExactly(new MonKeyItemBase("steve summary","detail doesn't need to match"),"souza",new Long(44));
        assertThat(steveMonKey.getDetails().toString()).isEqualTo("[steve detail, souza, 44]");
    }

}
