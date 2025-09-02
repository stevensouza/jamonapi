package com.jamonapi.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;


public class SerializationUtilsTest {

    @Test
    public void testSerializeDeserialize() throws Throwable {
        String message = "serialize/deserialize me steve";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SerializationUtils.serialize(message, outputStream);
        String answer = SerializationUtils.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(answer).isEqualTo(message);
    }

    @Test
    public void testDeepCopy() throws Throwable {
        String message = "serialize/deserialize me steve";
        String answer = SerializationUtils.deepCopy(message);
        assertThat(answer).isEqualTo(message);
    }


}
