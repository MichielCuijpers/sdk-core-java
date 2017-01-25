package com.mastercard.api.core.model.map;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andrearizzini on 25/01/2017.
 */
abstract class AbstractSmartMap implements Map<String,Object>,Cloneable,Serializable {

    private static final Pattern arrayIndexPattern = Pattern.compile("(.*)\\[(.*)\\]");
    protected Map<String,Object> store = null;
    protected abstract Map<String,Object> createMap();
    protected abstract Map<String, Object> createMap(Map<String,Object> map);

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
                    Map<String,Object> tmpMap = createMap();
                    store.put(key, tmpMap);
                    return  tmpMap;
                }
                else {
                    store.put(key, value);
                    return value;
                }

            } else {
                if (!hasValue && createSubObject) {
                    Map<String,Object> tmpMap = createMap();
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
                tmpObject = store.get(keyName);  // get the list from the map
                if (tmpObject == null) {
                    tmpObject = new ArrayList<Object>();
                    store.put(keyName, tmpObject);

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
                Map<String,Object> tmpMap = createMap();
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
    private Object _get(String key, Map<String,Object> map) throws IllegalArgumentException {
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
    private Object _get(String key, Map<String,Object> map, boolean retrievalMode) throws IllegalArgumentException {
        Matcher m = arrayIndexPattern.matcher(key);
        if (!m.matches()) {
            //this could return any object
            if (map == null) {
                return store.get(key);
            } else {
                return map.get(key);
            }

        } else {
            // handle the keyPath: "x[]"
            String keyName = m.group(1);
            // gets the key to retrieve from the matcher

            Object tmpObject = null;
            if (map == null) {
                tmpObject = store.get(keyName);  // get the list from the map
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
                return store.remove(key);
            } else {
                return map.remove(key);
            }

        } else {
            // handle the keyPath: "x[]"
            String keyName = m.group(1);
            // gets the key to retrieve from the matcher
            Object tmpObject = null;
            if (map == null) {
                tmpObject = store.get(keyName);
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

    /**
     *
     * @param keyPath
     * @return
     * @throws IllegalArgumentException
     */
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


    protected Map<String,Object> parseMap(Map<String, Object> map) {
        Map<String,Object> result = createMap();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                //recursive add map
                result.put(entry.getKey(), parseMap((Map<String,Object>) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                //recursive add list
                result.put(entry.getKey(), parseList((List<Object>) entry.getValue()));
            } else {
                // add normal value
                result.put(entry.getKey(), entry.getValue());
            }

        }
        return result;

    }

    private List<Object> parseList(List<Object> list) {
        List<Object> result = new ArrayList();
        for (Object o : list) {
            if (o instanceof Map) {
                result.add(parseMap((Map) o));
            } else if (o instanceof List) {
                result.add(parseList( (List) o));
            } else {
                result.add(o);
            }
        }
        return result;
    }




    @Override
    public int size() {
        return store.size();
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(value);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        store.putAll(m);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public Set<String> keySet() {
        return store.keySet();
    }

    @Override
    public Collection<Object> values() {
        return store.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return store.entrySet();
    }
}
