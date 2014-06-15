package com.jamonapi.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class AppMapTest {

    @Test
    public void testCaseInsensitivity() {
        Map map=new HashMap();
        map.put("HeLLo", "world");
        assertThat(map.get("HELLO")).isNull();
        map=new AppMap(map);
        assertThat(map.get("HELLO")).isEqualTo("world");
    }

}
