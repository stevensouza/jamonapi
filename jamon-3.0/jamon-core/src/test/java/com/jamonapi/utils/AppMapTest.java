package com.jamonapi.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


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
