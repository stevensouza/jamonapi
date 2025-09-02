package com.jamonapi.utils;
import java.util.Iterator;
import java.util.Map;


/**
 * Case Insensitive HashMap() - If the maps key is a string then the following keys are all considered equal:
 *   myKey<br>
 *   MYKEY<br>
 *   MyKey<br>
 *   ...
 * 
 * Other than that this class works like a regular HashMap.
 */
public class AppMap extends java.util.HashMap {
    private static final long serialVersionUID = 278L;
    public AppMap() {
    }
    /** Constructs an empty HashMap with the default initial capacity (16) and the default load factor (0.75).
     * 
     */

    public AppMap(int initialCapacity) {
        super(initialCapacity);
    }

    /** Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75). */
    public AppMap(int initialCapacity, float loadFactor) {
        super(initialCapacity,loadFactor);
    }

    /** Constructs an empty HashMap with the specified initial capacity and load factor. */
    public AppMap(Map m) {
       putAll(m);
    }

    /**
     * Note up to jdk 8 putAll(m) called the put(...) method below similar to the implementation below.
     * jdk8 must have changed putAll(m) to not call put.  This made it so the keys weren't converted to
     * be case insensitive breaking tests.  Hence the following code was added to make this implementation explicit.
     * @since 2.79
     * @param m
     */
    @Override
    public void putAll(Map m) {
        Iterator<Map.Entry> iter = m.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object put(Object key, Object object) {
        return super.put(convertKey(key), object);
    }

    @Override
    public boolean containsKey(Object key) {
        // The normal case is done first as a performance optimization.  It seems to make checks around 30% faster
        // due to only converting the case of the string comparison only when required.
        return super.containsKey(key) || super.containsKey(convertKey(key));
    }

    @Override
    public Object get(Object key) {
        return super.get(convertKey(key));
    }

    public static Object get(Map map, Object key) throws AppBaseException {
        Object object = map.get(key);

        if (object!=null)
            return object;
        else
            throw new AppBaseException(key+" does not exist in the HashMap.");
    }

    protected Object convertKey(Object key) {
        if (key instanceof String && key!=null)
            key = key.toString().toLowerCase();

        return key;

    }

    public static Map createInstance() {
        return new AppMap();
    }

}

