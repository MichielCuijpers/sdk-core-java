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
 * contain an array notation ['number'] in which case the value of 'a' in the map is a list containing
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
public class SmartMap extends LinkedHashMap<String, Object> {
    private static final Pattern arrayIndexPattern = Pattern.compile("(.*)\\[(.*)\\]");

    /**
     * Constructs an empty map with the default capacity and load factor.
     */
    public SmartMap() {
        super();
    }

    /**
     * Constructs a map with the same mappings as in the specifed map.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public SmartMap(Map<String, Object> map) {
        super(map);
    }

    /**
     * Consturcts a map based of the speficied JSON string.
     *
     * @param jsonMapString the JSON string used to construct the map
     */
    public SmartMap(String jsonMapString) {
        super();
        putAll((Map<? extends String, ? extends Object>) JSONValue.parse(jsonMapString));
    }

    /**
     * Constructs a map with an initial mapping of keyPath to value.
     * @param keyPath key path with which the specified value is to be associated.
     * @param value value to be associated with the specified key path.
     */
    public SmartMap(String keyPath, Object value) {
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
        String[] keys = ((String) keyPath).split("\\.");

        Map<String, Object> map = null;
        for (int i = 0; i <= (keys.length - 2); i++) {
            String thisKey = keys[i];
            Object tmpObject = _get(thisKey, map, false);
            if (tmpObject == null) {
                tmpObject = _create(thisKey, null, map, true);
                if (tmpObject instanceof Map) {
                    map = (Map<String, Object>) tmpObject;
                } else {
                    throw new IllegalArgumentException("Property '" + thisKey + "' is not a map");
                }
            } else {
                if (tmpObject instanceof Map) {
                    map = (Map<String, Object>) tmpObject;
                } else {
                    throw new IllegalArgumentException("Property '" + thisKey + "' is not a map");
                }
            }
        }

        return _create(keys[keys.length-1], value, map, false);
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
    public SmartMap set(String keyPath, Object value) {
        put(keyPath, value);
        return this;
    }

    /**
     * This is the private method which is used internally to create the map / lit structure if
     * it doesn't exists. For example the User.Address[0].Name will invoke 3 times this method, one
     * for User, one for Address[0] and one for Name
     * @param key
     * @param value
     * @param map
     * @param createSubObject - true if
     * @return
     */
    private Object _create(String key, Object value, Map<String,Object> map, boolean createSubObject) {
        Matcher m = arrayIndexPattern.matcher(key);
        boolean hasValue = (value == null) ? false : true;

        if (!m.matches()) {
            //this could return any object
            if (map == null) {
                if (!hasValue && createSubObject) {
                    Map<String,Object> tmpMap = new LinkedHashMap();
                    super.put(key, tmpMap);
                    return  tmpMap;
                }
                else {
                    super.put(key, value);
                    return value;
                }

            } else {
                if (!hasValue && createSubObject) {
                    Map<String,Object> tmpMap = new LinkedHashMap();
                    map.put(key, tmpMap);
                    return tmpMap;
                }
                else {
                    map.put(key, value);
                    return value;
                }
            }
        } else {
            // handle the keyPath: "x[]"
            String keyName = m.group(1);
            // gets the key to retrieve from the matcher

            Object tmpObject = null;

            if (map == null) {
                tmpObject = super.get(keyName);  // get the list from the map
                if (tmpObject == null) {
                    tmpObject = new ArrayList<Object>();
                    super.put(keyName, tmpObject);

                }
            } else {
                tmpObject = map.get(keyName);
                if (tmpObject == null) {
                    tmpObject = new ArrayList<Object>();
                    map.put(keyName, tmpObject);
                }
            }

            if (!(tmpObject instanceof List)) {
                throw new IllegalArgumentException("Property '" + key + "' is not an array");
            }

            // get the list from the map
            List<Object> tmpList = (List<Object>) tmpObject;

            //get last item if none specified
            Integer index = tmpList.size() - 1;
            if (!"".equals(m.group(2))) {
                index = Integer.parseInt(m.group(2));
            }

            if (hasValue) {
                // this is the assignment
                tmpList.add(index, value);
                return value;
            } else {
                Map<String,Object> tmpMap = new LinkedHashMap<String, Object>();
                tmpList.add(index, tmpMap);
                return tmpMap;
            }
        }
    }

    /**
     * this is the main method which is used for the recursive iteration.
     * It evaluates the key and returns the correct an element in a map or list.
     * @param key
     * @param map
     * @return
     * @throws IllegalArgumentException
     */
    private Object _get(String key, Map<String, Object> map) throws IllegalArgumentException {
        return _get(key, map, true);
    }

    /**
     *
     * @param key
     * @param map
     * @param retrievalMode (if calling _get from put then retrievalMode is false)
     * @return
     * @throws IllegalArgumentException
     */
    private Object _get(String key, Map<String, Object> map, boolean retrievalMode) throws IllegalArgumentException {
        Matcher m = arrayIndexPattern.matcher(key);
        if (!m.matches()) {
            //this could return any object
            if (map == null) {
                return super.get(key);
            } else {
                return map.get(key);
            }

        } else {
            // handle the keyPath: "x[]"
            String keyName = m.group(1);
            // gets the key to retrieve from the matcher

            Object tmpObject = null;
            if (map == null) {
                tmpObject = super.get(keyName);  // get the list from the map
            } else {
                tmpObject = map.get(keyName);
            }

            if (!(tmpObject instanceof List)) {
                // Only if we are retrieving from the map
                if (retrievalMode) {
                    throw new IllegalArgumentException("Property '" + key + "' is not an array");
                }
                // If we are not retrieving i.e. put was called then return null and the array will be created
                else {
                    return null;
                }
            }

            List<Map<String, Object>> tmpList = (List<Map<String, Object>>) tmpObject;  // get the list from the map

            Integer index = tmpList.size() - 1;                                        //get last item if none specified
            if (!"".equals(m.group(2))) {
                index = Integer.parseInt(m.group(2));
            }

            // Code against IndexOutOfBoundsException
            if (tmpList.size() > index) {
                return tmpList.get(index);
            }
            else {
                return null;
            }
        }
    }

    /**
     * this is the main method which is used for the recursive iteration.
     * It evaluates the key and returns the correct an element in a map or list.
     * @param key
     * @param map
     * @return
     * @throws IllegalArgumentException
     */
    private Object _remove(String key, Map<String, Object> map) throws IllegalArgumentException {
        Matcher m = arrayIndexPattern.matcher(key);
        if (!m.matches()) {
            //this could return any object
            if (map == null) {
                return super.remove(key);
            } else {
                return map.remove(key);
            }

        } else {
            // handle the keyPath: "x[]"
            String keyName = m.group(1);
            // gets the key to retrieve from the matcher
            Object tmpObject = null;
            if (map == null) {
                tmpObject = super.get(keyName);
            }  else {
                tmpObject = map.get(keyName);  // get the list from the map
            }
            if (!(tmpObject instanceof List)) {
                throw new IllegalArgumentException("Property '" + key + "' is not an array");
            }

            List<Map<String, Object>> tmpList = (List<Map<String, Object>>) tmpObject;  // get the list from the map

            int index = tmpList.size() - 1;                                        //get last item if none specified
            if (!"".equals(m.group(2))) {
                index = Integer.parseInt(m.group(2));
            }

            return tmpList.remove(index);
        }
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
            return _get(keys[0], null);
        }

        Map<String, Object> map = findLastMapInKeyPath((String) keyPath);     // handles keyPaths beyond 'root' keyPath. i.e. "x.y OR x.y[].z, etc."
        if (map == null) {
            return null;
        }

        // retrieve the value at the end of the object path i.e. x.y.z, this retrieves whatever is in 'z'
        return _get(keys[keys.length - 1], map);
    }

    /**
     * Returns true if there is a value associated with the specified key path.
     *
     * @param keyPath key path whose associated value is to be tested
     * @return true if this map contains an value associated with the specified key path
     */
    @Override
    public boolean containsKey(Object keyPath) {
        String[] keys = ((String) keyPath).split("\\.");

        if (keys.length <= 1) {
            try {
                if (_get(keys[0], null) != null) {
                    return true;
                }
            } catch (Exception e ) {
            }
            return false;
        }

        Map<String, Object> map = findLastMapInKeyPath((String) keyPath);
        if (map == null) {
            return false;
        }

        try {
            if (_get(keys[keys.length - 1], map) !=  null){
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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
            return _remove(keys[0], null);
        }

        Map<String, Object> map = findLastMapInKeyPath((String) keyPath);
        if (map == null) {
            return null;
        }

        return _remove(keys[keys.length - 1], map);
    }

    private Map<String, Object> findLastMapInKeyPath(String keyPath) throws  IllegalArgumentException {
        String[] keys = ((String) keyPath).split("\\.");

        Map<String, Object> map = null;
        for (int i = 0; i <= (keys.length - 2); i++) {
            String thisKey = keys[i];
            Object tmpObject = _get(thisKey, map);
            if (tmpObject == null) {
                return null;
            } else {
                if (tmpObject instanceof Map) {
                    map = (Map<String, Object>) tmpObject;
                } else {
                    throw new IllegalArgumentException("Property '" + thisKey + "' is not a map");
                }
            }
        }
        return map;
    }

}
