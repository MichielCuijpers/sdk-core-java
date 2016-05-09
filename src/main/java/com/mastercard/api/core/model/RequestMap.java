/*
 * Copyright 2015 MasterCard International.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * Neither the name of the MasterCard International Incorporated nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF 
 * SUCH DAMAGE.
 *
 */

package com.mastercard.api.core.model;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Map object that extends the LinkedHashMap map with support for insertion and retrieval of keys using special
 * key path values.  The key path support nested maps and array values.
 * <p>
 * A key path consists of a sequence of key values separated by '.' characters.  Each part of the key path
 * consists of a separate map.  For example a key path of 'k1.k2.k3' is a map containing a key 'k1' whose
 * value is a map containing a key 'k2' whose values is a map containing a key 'k3'.   A key path can also
 * contain an array notation '[<number'] in which case the value of 'a' in the map is a list containing
 * a map.  For example 'a[1].k2' refers to the key value 'k2' in the 2nd element of the list referred to by
 * the value of key 'a' in the map.  If no index value is given (i.e., '[]') then a put() method appends
 * to the list while a get() method returns the last value in the list.
 * <p>
 * When using the array index notation the value inserted must be a map; inserting values is not permitted.
 * For example using <code>put("a[3].k1", 1)</code> is permitted while <code>put("a[3]", 1)</code> results
 * in an <code>IllegalArgumentException</code>.
 * <p>
 * <p>
 * Examples:
 * <pre>
 * RequestMap map  = new RequestMap();
 * map.put("card.number", "5555555555554444");
 * map.put("card.cvc", "123");
 * map.put("card.expMonth", 5);
 * map.put("card.expYear", 15);
 * map.put("currency", "USD");
 * map.put("amount", 1234);
 * </pre>
 * There is also an set() method which is similar to put() but returns the map providing a fluent map builder.
 * <pre>
 * RequestMap map = new RequestMap()
 *      .set("card.number", "5555555555554444")
 *      .set("card.cvc", "123")
 *      .set("card.expMonth", 5)
 *      .set("card.expYear", 15)
 *      .set("currency", "USD")
 *      .set("amount", 1234);
 * </pre>
 * Both of these examples construct a RequestMap containing the keys 'currency', 'amount' and 'card'.  The
 * value for the 'card' key is a map containing the key 'number', 'cvc', 'expMonth' and 'expYear'.
 */
public class RequestMap extends LinkedHashMap<String, Object> {
    private static final Pattern arrayIndexPattern = Pattern.compile("(.*)\\[(.*)\\]");

    /**
     * Constructs an empty map with the default capacity and load factor.
     */
    public RequestMap() {
        super();
    }

    /**
     * Constructs a map with the same mappings as in the specifed map.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public RequestMap(Map<String, Object> map) {
        super(map);
    }

    /**
     * Consturcts a map based of the speficied JSON string.
     *
     * @param jsonMapString the JSON string used to construct the map
     */
    public RequestMap(String jsonMapString) {
        super();
        putAll((Map<? extends String, ? extends Object>) JSONValue.parse(jsonMapString));
    }

    /**
     * Constructs a map with an initial mapping of keyPath to value.
     * @param keyPath key path with which the specified value is to be associated.
     * @param value value to be associated with the specified key path.
     */
    public RequestMap(String keyPath, Object value) {
        put(keyPath, value);
    }

    /**
     * Associates the specified value to the specified key path.
     *
     * @param keyPath key path to which the specified value is to be associated.
     * @param value   the value which is to be associated with the specified key path.
     * @throws IllegalArgumentException  if part of the key path does not match the expected type.
     * @throws IndexOutOfBoundsException if using an array index in the key path is out of bounds.
     */
    @Override
    public Object put(String keyPath, Object value) {
        String[] properties = keyPath.split("\\.");
        Map<String, Object> destinationObject = this;

        if (properties.length > 1) {
            for (int i = 0; i < (properties.length - 1); i++) {
                String property = properties[i];
                if (property.contains("[")) {
                    destinationObject = getDestinationMap(property, destinationObject, i == properties.length - 1);
                } else {
                    destinationObject = getPropertyMapFrom(property, destinationObject);
                }
            }
        } else if (keyPath.contains("[")) {
            destinationObject = getDestinationMap(keyPath, this, true);
        }

        // TODO: need to take care of the case where we are inserting a value into an array rather than
        // map ( eg map.put("a[2]", 123);

        if (destinationObject == this) {
            return super.put(keyPath, value);
        } else if (value instanceof Map) {     // if putting a map, call put all
            destinationObject.clear();
            RequestMap m = new RequestMap();
            m.putAll((Map<? extends String, ? extends Object>) value);
            destinationObject.put(properties[properties.length - 1], m);
            return destinationObject;
        } else {
            return destinationObject.put(properties[properties.length - 1], value);
        }
    }

    /**
     * Associates the specified value to the specified key path and returns a reference to
     * this map.
     *
     * @param keyPath key path to which the specified value is to be associated.
     * @param value   the value which is to be associated with the specified key path.
     * @return this map
     * @throws IllegalArgumentException  if part of the key path does not match the expected type.
     * @throws IndexOutOfBoundsException if using an array index in the key path is out of bounds.
     */
    public RequestMap set(String keyPath, Object value) {
        put(keyPath, value);
        return this;
    }

    /**
     * Returns the value associated with the specified key path or null if there is no associated value.
     *
     * @param keyPath key path whose associated value is to be returned
     * @return the value to which the specified key is mapped
     * @throws IllegalArgumentException  if part of the key path does not match the expected type.
     * @throws IndexOutOfBoundsException if using an array index in the key path is out of bounds.
     */
    @Override
    public Object get(Object keyPath) {
        String[] keys = ((String) keyPath).split("\\.");

        if (keys.length <= 1) {
            Matcher m = arrayIndexPattern.matcher(keys[0]);
            if (!m.matches()) {             // handles keyPath: "x"
                return super.get(keys[0]);
            } else {                                                                                        // handle the keyPath: "x[]"
                String key = m.group(1);                                                                    // gets the key to retrieve from the matcher
                Object o = super.get(key);  // get the list from the map
                if (!(o instanceof List)) {
                    throw new IllegalArgumentException("Property '" + key + "' is not an array");
                }
                List<Map<String, Object>> l = (List<Map<String, Object>>) o;  // get the list from the map

                Integer index = l.size() - 1;                                        //get last item if none specified
                if (!"".equals(m.group(2))) {
                    index = Integer.parseInt(m.group(2));
                }
                return l.get(index);        // retrieve the map from the list
            }
        }

        RequestMap map = findLastMapInKeyPath((String) keyPath);     // handles keyPaths beyond 'root' keyPath. i.e. "x.y OR x.y[].z, etc."
        if (map == null) {
            return null;
        }

        // retrieve the value at the end of the object path i.e. x.y.z, this retrieves whatever is in 'z'
        return map.get(keys[keys.length - 1]);
    }

    /**
     * Returns true if there is a value associated with the specified key path.
     *
     * @param keyPath key path whose associated value is to be tested
     * @return true if this map contains an value associated with the specified key path
     * @throws IllegalArgumentException  if part of the key path does not match the expected type.
     * @throws IndexOutOfBoundsException if using an array index in the key path is out of bounds.
     */
    @Override
    public boolean containsKey(Object keyPath) {
        String[] keys = ((String) keyPath).split("\\.");

        if (keys.length <= 1) {
            Matcher m = arrayIndexPattern.matcher(keys[0]);
            if (!m.matches()) {             // handles keyPath: "x"
                return super.containsKey(keys[0]);
            } else {                                                                                        // handle the keyPath: "x[]"
                String key = m.group(1);
                Object o = super.get(key);  // get the list from the map
                if (!(o instanceof List)) {
                    throw new IllegalArgumentException("Property '" + key + "' is not an array");
                }
                List<Map<String, Object>> l = (List<Map<String, Object>>) o;  // get the list from the map

                Integer index = l.size() - 1;
                if (!"".equals(m.group(2))) {
                    index = Integer.parseInt(m.group(2));
                }
                return index >= 0 && index < l.size();
            }
        }

        RequestMap map = findLastMapInKeyPath((String) keyPath);
        if (map == null) {
            return false;
        }

        return map.containsKey(keys[keys.length - 1]);
    }

    /**
     * Removes the value associated with the specified key path from the map.
     *
     * @param keyPath key path whose associated value is to be removed
     * @throws IllegalArgumentException  if part of the key path does not match the expected type.
     * @throws IndexOutOfBoundsException if using an array index in the key path is out of bounds.
     */
    @Override
    public Object remove(Object keyPath) {

        String[] keys = ((String) keyPath).split("\\.");

        if (keys.length <= 1) {
            Matcher m = arrayIndexPattern.matcher(keys[0]);
            if (!m.matches()) {
                return super.remove(keys[0]);
            } else {                                                                                        // handle the keyPath: "x[]"
                String key = m.group(1);                                                                    // gets the key to retrieve from the matcher
                Object o = super.get(key);  // get the list from the map
                if (!(o instanceof List)) {
                    throw new IllegalArgumentException("Property '" + key + "' is not an array");
                }
                List<Map<String, Object>> l = (List<Map<String, Object>>) o;  // get the list from the map

                Integer index = l.size() - 1;                                        //get last item if none specified
                if (!"".equals(m.group(2))) {
                    index = Integer.parseInt(m.group(2));
                }
                return l.remove(index.intValue());
            }
        }

        RequestMap map = findLastMapInKeyPath((String) keyPath);
        if (map == null) {
            return null;
        }

        return map.remove(keys[keys.length - 1]);
    }

    private RequestMap findLastMapInKeyPath(String keyPath) throws  IllegalArgumentException {
        String[] keys = ((String) keyPath).split("\\.");

        Map<String, Object> map = null;
        for (int i = 0; i <= (keys.length - 2); i++) {
            Matcher m = arrayIndexPattern.matcher(keys[i]);
            String thisKey = keys[i];
            if (m.matches()) {
                thisKey = m.group(1);

                Object o = null;
                if (null == map) {    // if we are at the "root" of the object path
                    o = super.get(thisKey);
                } else {
                    o = map.get(thisKey);
                }

                if (!(o instanceof List)) {
                    throw new IllegalArgumentException("Property '" + thisKey + "' is not an array");
                }
                List<Map<String, Object>> l = (List<Map<String, Object>>) o;

                Integer index = l.size() - 1;                                        //get last item if none specified

                if (!"".equals(m.group(2))) {
                    index = Integer.parseInt(m.group(2));
                }

                map = (Map<String, Object>) l.get(index);

            } else {
                if (null == map) {
                    if (super.containsKey(thisKey)) {
                        Object tmpObject = super.get(thisKey);
                        if (tmpObject instanceof Map) {
                            map = (Map<String, Object>) super.get(thisKey);
                        } else {
                            throw new IllegalArgumentException("Property '" + thisKey + "' is not a map");
                        }
                    } else {
                        return null;
                    }

                } else {
                    if (map.containsKey(thisKey)) {
                        Object tmpObject = map.get(thisKey);
                        if (tmpObject instanceof Map) {
                            map = (Map<String, Object>) map.get(thisKey);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }

            }

        }

        return new RequestMap(map);
    }

    private Map<String, Object> getDestinationMap(String property, Map<String, Object> destinationObject, boolean createMap) {

        Matcher m = arrayIndexPattern.matcher(property);
        if (m.matches()) {
            String propName = m.group(1);
            Integer index = null;
            if (!"".equals(m.group(2))) {
                index = Integer.parseInt(m.group(2));
            }
            return findOrAddToList(destinationObject, propName, index, createMap);
        }

        return destinationObject;

    }

    private Map<String, Object> findOrAddToList(Map<String, Object> destinationObject, String propName, Integer index, boolean createMap) {
        //

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        // find existing list or put the new list
        if (destinationObject.containsKey(propName)) {
            Object o = destinationObject.get(propName);
            if (!(o instanceof List)) {
                throw new IllegalArgumentException("Property '" + propName + "' is not an array");
            }
            list = (List<Map<String, Object>>) o;
        } else {
            destinationObject.put(propName, list);
        }

        // get the existing object in the list at the index
        Map<String, Object> propertyValue = null;
        if (index != null && list.size() > index) {
            propertyValue = list.get(index);
        }

        // no object at the index, create a new map and add it
        if (null == propertyValue) {
            propertyValue = new LinkedHashMap<String, Object>();
            if (null == index) {
                list.add(propertyValue);
            } else {
                list.add(index, propertyValue);
            }
        }

        // return the map retrieved from or added to the list
        destinationObject = propertyValue;

        return destinationObject;
    }

    private Map<String, Object> getPropertyMapFrom(String property, Map<String, Object> object) {
        // create a new map at the key specified if it doesnt already exist
        if (!object.containsKey(property)) {
            Map<String, Object> val = new LinkedHashMap<String, Object>();
            object.put(property, val);
        }

        Object o = object.get(property);
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        } else {
            throw new IllegalArgumentException("cannot change nested property to map");
        }
    }
}
