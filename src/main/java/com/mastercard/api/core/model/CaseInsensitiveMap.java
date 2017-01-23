package com.mastercard.api.core.model;

import java.util.*;

/**
 * Created by andrearizzini on 23/01/2017.
 */
public class CaseInsensitiveMap extends LinkedHashMap<String,Object> {

    /**
     * Constructs an empty map with the default capacity and load factor.
     */
    public CaseInsensitiveMap() {
        super();
    }

    /**
     * Constructs a map with the same mappings as in the specifed map.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public CaseInsensitiveMap(Map<String, Object> map) {
        super();
        putAll(parseMap(map));
    }



    private Map<String,Object> parseMap(Map<String, Object> map) {
        Map<String,Object> result = new LinkedHashMap();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                //recursive add map
                result.put(entry.getKey().toLowerCase(), parseMap((Map<String,Object>) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                //recursive add list
                result.put(entry.getKey().toLowerCase(), parseList((List<Object>) entry.getValue()));
            } else {
                // add normal value
                result.put(entry.getKey().toLowerCase(), entry.getValue());
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
}
