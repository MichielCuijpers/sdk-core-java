package com.mastercard.api.core

import com.mastercard.api.core.mocks.MockAuthentication
import com.mastercard.api.core.mocks.MockBaseObject
import com.mastercard.api.core.model.ResourceList
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by eamondoyle on 16/02/2016.
 */
class BaseObjectSpec extends Specification {

    ApiController mockApiController
    ApiControllerFactory mockApiControllerFactory

    def setup() {
        mockApiController = Mock(ApiController, constructorArgs: ['/BARNEY'])
        mockApiControllerFactory = Mock(ApiControllerFactory)

        Field field = BaseObject.getDeclaredField("apiControllerFactory")
        field.setAccessible(true)

        Field modifiersField = Field.class.getDeclaredField("modifiers")
        modifiersField.setAccessible(true)
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL)

        field.set(null, mockApiControllerFactory)
    }

    def 'test read object' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = MockBaseObject.readObject(auth, value.getObjectType(), value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test list objects' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        ResourceList<MockBaseObject> response = MockBaseObject.listObjects(auth, value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> {
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
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        ResourceList<MockBaseObject> response = MockBaseObject.listObjects(auth, value, [max: 2])

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> {
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
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = MockBaseObject.createObject(auth, value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test create object with 204' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = MockBaseObject.createObject(auth, value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> null
        response != null
        response.size() == 0
    }

    def 'test update object with auth' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = value.updateObject(auth, value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test update object' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = value.updateObject(value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test delete object with auth' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = value.deleteObject(auth, value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> { [a: 1, b: "2", c: true] }
        response.get("a") == 1
        response.get("b") == "2"
        response.get("c") == true
    }

    def 'test delete object' () {
        given:
        MockBaseObject value = new MockBaseObject()
        MockAuthentication auth = new MockAuthentication()

        when:
        MockBaseObject response = value.deleteObject(value)

        then:
        1 * mockApiControllerFactory.createApiController(_) >> mockApiController
        1 * mockApiController.execute(_, _, _, _, _)  >> { [a: 1, b: "2", c: true] }
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
        response.getBasePath() == "/mock"
        response.getObjectType() == "MockObject"
        response.size() == 0
    }

}
