package com.jamonapi.utils;

import java.io.*;

/**
 * Class that can serialize and deserialize objects.
 * Created by stevesouza on 7/23/14.
 */
public class SerializationUtils {

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
}
