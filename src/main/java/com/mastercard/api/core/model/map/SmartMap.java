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

package com.mastercard.api.core.model.map;

import org.json.simple.JSONValue;

import java.util.*;


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
public class SmartMap extends AbstractSmartMap {



    /**
     * Constructs an empty map with the default capacity and load factor.
     */
    public SmartMap() {
        store = createMap();
    }

    /**
     * Constructs a map with the same mappings as in the specifed map.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public SmartMap(Map<String, Object> map) {
        store = createMap(map);
    }

    /**
     * Consturcts a map based of the speficied JSON string.
     *
     * @param jsonMapString the JSON string used to construct the map
     */
    public SmartMap(String jsonMapString) {
        store = createMap((Map<String,Object>) JSONValue.parse(jsonMapString));
    }

    /**
     * Constructs a map with an initial mapping of keyPath to value.
     * @param keyPath key path with which the specified value is to be associated.
     * @param value value to be associated with the specified key path.
     */
    public SmartMap(String keyPath, Object value) {
        store = createMap();
        put(keyPath, value);
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


    @Override
    protected Map<String, Object> createMap() {
        return new LinkedHashMap<String,Object>();
    }

    @Override
    protected Map<String, Object> createMap(Map<String,Object> map) {
        return new LinkedHashMap<String,Object>(map);
    }



}
