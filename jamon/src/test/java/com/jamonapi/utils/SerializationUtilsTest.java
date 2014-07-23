package com.jamonapi.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.fest.assertions.api.Assertions.assertThat;


public class SerializationUtilsTest {

    @Test
    public void testSerializeDeserialize() throws Throwable {
        String message = "serialize/deserialize me steve";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SerializationUtils.serialize(message, outputStream);
        String answer = SerializationUtils.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(answer).isEqualTo(message);
    }


}