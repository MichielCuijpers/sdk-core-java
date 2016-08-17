package com.mastercard.api.core
import com.mastercard.api.core.mocks.MockAuthentication
import com.mastercard.api.core.mocks.MockBaseObject
import com.mastercard.api.core.model.ResourceList
import spock.lang.Specification
/**
 * Created by eamondoyle on 16/02/2016.
 */
class BaseObjectSpec extends Specification {

    def apiController

    def setup() {
        apiController = Mock(ApiController)
        MockBaseObject.setApiController(apiController)
    }

    def cleanup() {
        apiController = null;
        MockBaseObject.setApiController(new ApiController())
    }


    def 'test read object' () {

        when:
        MockBaseObject response = MockBaseObject.readObject(new MockAuthentication(), new MockBaseObject());

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test query object' () {

        when:
        MockBaseObject response = MockBaseObject.queryObject(new MockAuthentication(), new MockBaseObject());

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test list objects' () {


        when:
        ResourceList<MockBaseObject> response = MockBaseObject.listObjects(new MockAuthentication(), new MockBaseObject(), null);

        then:
        1 * apiController.execute(_,_,_)  >> {
            [
                list: [
                    [a: 1, b: "2", c: true],
                    [a: 11, b: "22", c: true],
                    [a: 111, b: "222", c: true],
                    "not a map"
                ]
            ]
        }
        response.getList().size() == 3
        response.getList().get(0).get("a") == 1
        response.getList().get(0).get("b") == "2"
        response.getList().get(0).get("c") == true
        response.getList().get(1).get("a") == 11
        response.getList().get(1).get("b") == "22"
        response.getList().get(1).get("c") == true
        response.getList().get(2).get("a") == 111
        response.getList().get(2).get("b") == "222"
        response.getList().get(2).get("c") == true
    }

    def 'test list objects with criteria' () {


        when:
        ResourceList<MockBaseObject> response = MockBaseObject.listObjects(new MockAuthentication(), new MockBaseObject(), [max: 2])

        then:
        1 * apiController.execute(_,_,_)  >> {
            [
                list: [
                    [a: 1, b: "2", c: true],
                    [a: 11, b: "22", c: true]
                ]
            ]
        }
        response.getList().size() == 2
        response.getList().get(0).get("a") == 1
        response.getList().get(0).get("b") == "2"
        response.getList().get(0).get("c") == true
        response.getList().get(1).get("a") == 11
        response.getList().get(1).get("b") == "22"
        response.getList().get(1).get("c") == true
    }

    def 'test create object' () {


        when:
        MockBaseObject response = MockBaseObject.createObject(new MockAuthentication(), new MockBaseObject())

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test create object with 204' () {


        when:
        MockBaseObject response = MockBaseObject.createObject(new MockAuthentication(), new MockBaseObject())

        then:
        1 * apiController.execute(_,_,_)  >> null
        response != null
        response.size() == 0
    }

    def 'test update object with auth' () {


        when:
        MockBaseObject response = new MockBaseObject().updateObject(new MockAuthentication(), new MockBaseObject())

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test update object' () {


        when:
        MockBaseObject response = new MockBaseObject().updateObject(new MockBaseObject())

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test delete object with auth' () {


        when:
        MockBaseObject response = new MockBaseObject().deleteObject(new MockAuthentication(), new MockBaseObject())

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test delete object' () {


        when:
        MockBaseObject response = new MockBaseObject().deleteObject(new MockBaseObject())

        then:
        1 * apiController.execute(_,_,_)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test create response base object' () {
        given:
        MockBaseObject value = new MockBaseObject()

        when:
        BaseObject response = MockBaseObject.createResponseBaseObject(value)

        then:
        response.getResourcePath() == "/mock/MockObject"
        response.size() == 0
    }

}
