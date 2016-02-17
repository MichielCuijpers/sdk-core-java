package com.mastercard.api.core

import spock.lang.Specification

/**
 * Created by eamondoyle on 16/02/2016.
 */
class RequestMapSpec extends Specification {

    def 'test RequestMap(Map<String, Object> map)' () {
        given:
        Map<String, Object> map = ["a": 1, "b": "2", c: true, d: ["aa": 11, "bb": "22"]]
        RequestMap requestMap = new RequestMap(map)

        expect:
        requestMap.get("a") == 1
        requestMap.get("b") == "2"
        requestMap.get("c") == true
        requestMap.get("d") == ["aa": 11, "bb": "22"]
        requestMap.get("d.aa") == 11
        requestMap.get("d.bb") == "22"
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

    def 'test put' () {
        given:
        RequestMap requestMap = new RequestMap()
        requestMap.put("int", new Integer("1"))
        requestMap.put("double", new Double("2.5"))
        requestMap.put("boolean", true)
        requestMap.put("list", ["a", "b", "c"])
        requestMap.put("d.aa", 11)

        expect:
        requestMap.get("int") == 1
        requestMap.get("double") == 2.5
        requestMap.get("boolean") == true
        requestMap.get("list") == ["a", "b", "c"]
        requestMap.get("d.aa") == 11
    }

    def 'test set and get' () {
        given:
        RequestMap requestMap = new RequestMap()
                .set("int", new Integer("1"))
                .set("double", new Double("2.5"))
                .set("boolean", true)
                .set("list", ["a", "b", "c"])
                .set("d.aa", 11)

        expect:
        requestMap.get("int") == 1
        requestMap.get("double") == 2.5
        requestMap.get("boolean") == true
        requestMap.get("list") == ["a", "b", "c"]
        requestMap.get("d.aa") == 11
    }

    def 'test remove' () {
        given:
        RequestMap requestMap = new RequestMap()
                .set("int", new Integer("1"))
                .set("double", new Double("2.5"))
                .set("boolean", true)
                .set("list", ["a", "b", "c"])
                .set("d.aa", 1)
                .set("d.bb", 2)
                .set("d.cc", 3)

        when:
        requestMap.remove("int")
        requestMap.remove("d.aa")

        then:
        !requestMap.containsKey("int")
        !requestMap.containsKey("d.aa")
        requestMap.get("double") == 2.5
        requestMap.get("boolean") == true
        requestMap.get("list") == ["a", "b", "c"]

        when:
        requestMap.remove("d")

        then:
        !requestMap.containsKey("d")
        !requestMap.containsKey("d.bb")
    }

}
