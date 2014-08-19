package com.jamonapi.utils;


import java.io.*;

/**
 * Class that can serialize and deserialize objects.
 * Created by stevesouza on 7/23/14.
 */
public class SerializationUtils {

    /** note object should be serializable **/
    public static void serialize(Serializable object, OutputStream outputStream) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(object);
        }  finally {
            if (out != null) {
              out.close();
            }
        }
    }

    public static void serializeToFile(Serializable object, String fileName) throws IOException {
        OutputStream outputStream = FileUtils.getOutputStream(fileName);
        serialize(object, outputStream);
    }

    public static <T> T deserialize(InputStream inputStream) throws Throwable {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(inputStream);
            T obj = (T) in.readObject();
            return obj;
        } finally {
           if (in != null) {
             in.close();
           }
        }
    }

    public static <T> T deserializeFromFile(String fileName) throws Throwable {
        InputStream inputStream = FileUtils.getInputStream(fileName);
        return deserialize(inputStream);
    }

    /** Create a deep copy/clone of any serializable object */
    public static <T> T deepCopy(Serializable object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            serialize(object, outputStream);
            return deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (Throwable e) {
            throw new RuntimeException("Failed in performing a deep copy", e);
        }
    }
}
