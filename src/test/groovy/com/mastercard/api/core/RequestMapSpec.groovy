package com.mastercard.api.core

import com.mastercard.api.core.model.RequestMap
import groovy.transform.TypeChecked
import spock.lang.Specification

/**
 * Created by eamondoyle on 16/02/2016.
 */
class RequestMapSpec extends Specification {

    def 'test RequestMap as map' () {
        given:
        Map<String, Object> map = ["a": 1, "b": "2", c: true, d: ["aa": 11, "bb": "22"], e:[ [ "one": 1, "two": 2, "three": 3 ]]]
        RequestMap requestMap = new RequestMap(map)

        expect:
        requestMap.get("a") == 1
        requestMap.containsKey("a") == true
        requestMap.remove("a") == 1

        requestMap.get("b") == "2"
        requestMap.get("c") == true
        requestMap.get("d") == ["aa": 11, "bb": "22"]
        requestMap.get("d.aa") == 11
        requestMap.containsKey("d.aa") == true
        requestMap.remove("d.aa") == 11

        requestMap.get("d.bb") == "22"
    }

    def 'test RequestMap as list' () {
        given:
        Map<String, Object> map = ["a": 1, "b": "2", c: true, d: ["aa": 11, "bb": "22"], e: [["one": 1, "two": 2, "three": 3]]]
        RequestMap requestMap = new RequestMap(map)

        expect:
        requestMap.get("e[0].one") == 1
        requestMap.containsKey("e[0].one") == true
        requestMap.remove("e[0].one") == 1
        requestMap.get("e[0].two") == 2
        requestMap.containsKey("e[0].two") == true
        requestMap.remove("e[0].two") == 2
        requestMap.get("e[0].three") == 3
        requestMap.get("e[].three") == 3
        requestMap.get("e[0]") == [three:3]
        requestMap.containsKey("e[0]") == true
        requestMap.get("e[]") == [three:3]
        requestMap.containsKey("e[]") == true
        requestMap.remove("e[]") == [three:3]
    }

    def 'test RequestMap and list of string'() {
        given:
        RequestMap requestMap = new RequestMap("{\"Categories\":{\"Category\":[\"1Apparel\",\"2Automotive\",\"3Beauty\",\"4Book Stores\" ]}}");

        expect:
        requestMap.get("Categories.Category[0]") == "1Apparel"
    }

    def 'test RequestMap as list throw exception' () {
        given:
        Map<String, Object> map = ["a": 1, "b": "2", c: true, d: ["aa": 11, "bb": "22"], e:[ [ "one": 1, "two": 2, "three": 3 ]]]
        RequestMap requestMap = new RequestMap(map)

        when:
        requestMap.get("e.one")

        then:
        thrown IllegalArgumentException

        when:
        requestMap.containsKey("e.one")

        then:
        thrown IllegalArgumentException

        when:
        requestMap.remove("e.one")

        then:
        thrown IllegalArgumentException
    }

    def 'test RequestMap(String jsonMapString)' () {
        given:
        String jsonMapString = "{\"AccountInquiry\":{\"AccountNumber\":\"5343434343434343\"},\"Account\":{\"Status\":\"true\",\"Listed\":\"true\",\"ReasonCode\":\"S\",\"Reason\":\"STOLEN\"}}"
        RequestMap requestMap = new RequestMap(jsonMapString)

        expect:
        requestMap.get("AccountInquiry")
        requestMap.get("AccountInquiry.AccountNumber") == "5343434343434343"
        requestMap.get("Account.Status") == "true"
    }

    def 'test where value does not exists' () {
        given:
        String jsonMapString = "{\"AccountInquiry\":{\"AccountNumber\":\"5343434343434343\"},\"Account\":{\"Status\":\"true\",\"Listed\":\"true\",\"ReasonCode\":\"S\",\"Reason\":\"STOLEN\"}}"
        RequestMap requestMap = new RequestMap(jsonMapString)

        when:
        def result = requestMap.get("AccountInquiry.AccountNumber.Test")

        then:
        thrown IllegalArgumentException

        when:
        result = requestMap.get("AccountInquiry.Test")

        then:
        result == null
    }

    def 'test RequestMap(String keyPath, Object value)' () {
        given:
        RequestMap requestMap = new RequestMap("mockKey", "mockValue")

        expect:
        requestMap.get("mockKey") == "mockValue"
        requestMap.size() == 1
    }

    def 'test put' () {
        given:
        RequestMap requestMap = new RequestMap()
        requestMap.put("int", new Integer("1"))
        requestMap.put("double", new Double("2.5"))
        requestMap.put("boolean", true)
        requestMap.put("null", null)
        requestMap.put("list", ["a", "b", "c"])
        requestMap.put("d.null", null)
        requestMap.put("d.aa", 11)

        expect:
        requestMap.get("int") == 1
        requestMap.get("double") == 2.5
        requestMap.get("boolean") == true
        requestMap.get("null") == null
        requestMap.get("list") == ["a", "b", "c"]
        requestMap.get("d.null") == null
        requestMap.get("d.aa") == 11
    }

    @TypeChecked
    def 'test set and get' () {
        given:
        RequestMap requestMap = new RequestMap()
                .set("int", new Integer("1"))
                .set("double", new Double("2.5"))
                .set("boolean", true)
                .set("null", null)
                .set("list", ["a", "b", "c"])
                .set("d.null", null)
                .set("d.aa", 11)

        expect:
        requestMap.get("int") == 1
        requestMap.get("double") == 2.5
        requestMap.get("boolean") == true
        requestMap.get("null") == null
        requestMap.get("list") == ["a", "b", "c"]
        requestMap.get("d.null") == null
        requestMap.get("d.aa") == 11
    }

    def 'test remove' () {
        given:
        RequestMap requestMap = new RequestMap()
        requestMap.set("int", new Integer("1"))
        requestMap.set("double", new Double("2.5"))
        requestMap.set("boolean", true)
        requestMap.set("null", null)
        requestMap.set("list", ["a", "b", "c"])
        requestMap.set("d.null", null)
        requestMap.set("d.aa", 1)
        requestMap.set("d.bb", 2)
        requestMap.set("d.cc", 3)

        when:
        requestMap.remove("int")
        requestMap.remove("d.aa")
        requestMap.remove("d.null")

        then:
        requestMap.containsKey("int") == false
        requestMap.containsKey("d.null") == false
        requestMap.containsKey("d.aa") == false
        requestMap.get("double") == 2.5
        requestMap.get("boolean") == true
        requestMap.get("list") == ["a", "b", "c"]

        when:
        requestMap.remove("d")

        then:
        !requestMap.containsKey("d")
        !requestMap.containsKey("d.bb")
    }

    def 'test set arrays' () {
        given:
        RequestMap rm1 = new RequestMap()
        def cards = [[pan: "pan1"],[pan: "pan2"]]

        when: "directly set an array of cards"
        rm1.set("user.cards", cards)

        then:
        rm1.get("user.cards") instanceof List
        rm1.get("user.cards").size() == 2
        rm1.get("user.cards[0].pan") == "pan1"
        rm1.get("user.cards[1].pan") == "pan2"

        when: "setting an index"
        RequestMap rm2 = new RequestMap()   // reset requestMap
        RequestMap card1 = new RequestMap()
                .set("pan", "initialize1")
        RequestMap card2 = new RequestMap()
                .set("pan", "initialize2")
        rm2.set("user.cards[0]", card1)
        rm2.set("user.cards[1]", card2)

        then:
        rm2.get("user.cards") instanceof List
        rm2.get("user.cards").size() == 2
        rm2.get("user.cards[0].pan") == "initialize1"
        rm2.get("user.cards[1].pan") == "initialize2"

        when: "overwrite existing using index"
        rm2.set("user.cards[0].pan", "pan1")
        rm2.set("user.cards[1].pan", "pan2")

        then:
        rm2.get("user.cards") instanceof List
        rm2.get("user.cards").size() == 2
        rm2.get("user.cards[0].pan") == "pan1"
        rm2.get("user.cards[1].pan") == "pan2"

        when: "set a value using the index"
        RequestMap rm3 = new RequestMap();
        rm3.set("user.cards[0].pan", "pan1")
        rm3.set("user.cards[1].pan", "pan2")

        then:
        rm3.get("user.cards") instanceof List
        rm3.get("user.cards").size() == 2
        rm3.get("user.cards[0].pan") == "pan1"
        rm3.get("user.cards[1].pan") == "pan2"

        when: "set a value using the index"
        RequestMap rm4 = new RequestMap();
        rm4.set("user.cards[0].pan", null)
        rm4.set("user.cards[1].pan", "pan2")

        then:
        rm4.get("user.cards") instanceof List
        rm4.get("user.cards").size() == 2
        rm4.get("user.cards[0].pan") == null
        rm4.get("user.cards[1].pan") == "pan2"
    }

    def 'test set multiple array' () {
        given:
        RequestMap requestMap = new RequestMap()

        when:
        requestMap.set("user.cards[0].addresses[0].line1", null)
        requestMap.set("user.cards[1].addresses[0].line1", "1")

        then:
        requestMap.get("user.cards") instanceof List
        requestMap.get("user.cards[0].addresses") instanceof List
        requestMap.get("user.cards[0].addresses[0].line1") == null
        requestMap.get("user.cards[1].addresses[0].line1") == "1"
    }

}
