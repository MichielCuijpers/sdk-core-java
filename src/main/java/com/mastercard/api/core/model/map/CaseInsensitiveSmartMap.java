package com.mastercard.api.core.model.map;

import org.json.simple.JSONValue;

import java.util.*;

/**
 * Created by andrearizzini on 23/01/2017.
 */
public class CaseInsensitiveSmartMap extends AbstractSmartMap {

    /**
     * Constructs an empty map with the default capacity and load factor.
     */
    public CaseInsensitiveSmartMap() {
        store = createMap();
    }

    /**
     * Constructs a map with the same mappings as in the specifed map.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public CaseInsensitiveSmartMap(Map<String, Object> map) {

        store = parseMap(new HashMap(map));
    }

    /**
     * Consturcts a map based of the speficied JSON string.
     *
     * @param jsonMapString the JSON string used to construct the map
     */
    public CaseInsensitiveSmartMap(String jsonMapString) {
        store = parseMap(new HashMap<String,Object>((Map<String,Object>) JSONValue.parse(jsonMapString)));
    }

    @Override
    protected Map<String, Object> createMap() {
        return new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
    }


    @Override
    protected Map<String, Object> createMap(Map<String,Object> map) {
        Map<String,Object> tmpMap = new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
        tmpMap.putAll(map);
        return tmpMap;
    }


}
