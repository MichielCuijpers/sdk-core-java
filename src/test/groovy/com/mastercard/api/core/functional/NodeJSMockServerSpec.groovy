package com.mastercard.api.core.functional
import com.mastercard.api.core.ApiConfig
import com.mastercard.api.core.ApiController
import com.mastercard.api.core.exception.ApiCommunicationException
import com.mastercard.api.core.functional.model.Post
import com.mastercard.api.core.functional.model.UserPostHeader
import com.mastercard.api.core.functional.model.UserPostPath
import com.mastercard.api.core.mocks.MockAuthentication
import com.mastercard.api.core.mocks.MockBaseObject
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.model.ResourceList
import spock.lang.Specification
/**
 * Created by andrearizzini on 12/04/2016.
 */


//@IgnoreIf({ System.getProperty("RUN_NODEJS") == null })
class NodeJSMockServerSpec extends Specification {


    def setupSpec() {
        ApiConfig.setSandbox(true)
        ApiConfig.authentication = new MockAuthentication()
        ApiController.API_BASE_SANDBOX_URL = "http://localhost:8081";
        ApiController.API_BASE_LIVE_URL = "http://localhost:8081";
        MockBaseObject.setApiController(new ApiController())


    }

    def cleanupSpec() {
        ApiConfig.authentication = null;
        ApiController.API_BASE_SANDBOX_URL = "https://sandbox.api.mastercard.com";
        ApiController.API_BASE_LIVE_URL = "https://api.mastercard.com";
        MockBaseObject.setApiController(new ApiController())
    }


    def 'test Action.read from Post --> 200' () {
        when:
        Post returned = Post.read(null, "1");

        then:
        returned.get("id") == 1
        returned.get("title") == "My Title"
        returned.get("body") == "some body text"
        returned.get("userId") == 1

    }

    def 'test Action.read from Post with criteria--> 200' () {
        when:
        Map criteria = [notUserId: 10]
        Post returned = Post.read(null, "1", criteria);

        then:
        returned.get("id") == 1
        returned.get("title") == "My Title"
        returned.get("body") == "some body text"
        returned.get("userId") == 1

    }

    def 'test Action.read from Post --> 500' () {
        when:
        Post returned = Post.read(null, "aaa");

        then:
        def ex = thrown(ApiCommunicationException)
        ex.message == "I/O error"

    }


    def 'test Action.list from Post with empty param --> 200' () {
        when:
        ResourceList<Post> list = Post.list()

        then:
        list.getList().size() == 1

        Post returned = list.getList().get(0);
        returned.get("id") == 1
        returned.get("title") == "My Title"
        returned.get("body") == "some body text"
        returned.get("userId") == 1

    }


    def 'test Action.list from Post with criteria --> 200' () {
        when:
        Map criteria = [max: 10]
        ResourceList<Post> list = Post.list(criteria);

        then:
        list.getList().size() == 1

        Post returned = list.getList().get(0);
        returned.get("id") == 1
        returned.get("title") == "My Title"
        returned.get("body") == "some body text"
        returned.get("userId") == 1

    }



    def 'test Action.create from Post --> 200' () {
        when:
        RequestMap request = new RequestMap().set("title", "My Title").set("body", "some body text");
        Post response = Post.create(request);

        then:
        response.get("id") == 1
        response.get("title") == "My Title"
        response.get("body") == "some body text"
        response
    }

    def 'test Action.update from Post --> 200' () {
        when:
        RequestMap request = new RequestMap().set("title", "My Title").set("body", "some body text");
        Post response = Post.create(request);

        then:
        response.get("id") == 1
        response.get("title") == "My Title"
        response.get("body") == "some body text"

        when:
        response.set("title", "Updated My Title")
        response.set("body",  "Updated some body text")
        response.update();

        then:
        response.get("title") ==  "Updated My Title"
        response.get("body") == "Updated some body text"
    }


    def 'test Action.delete from Post --> 200' () {
        when:
        RequestMap request = new RequestMap().set("title", "My Title").set("body", "some body text");
        Post response = Post.create(request);

        then:
        response.get("id") == 1
        response.get("title") == "My Title"
        response.get("body") == "some body text"
        response.get("userId") == 1

        when:
        Post delete = response.delete()

        then:
        delete.isEmpty() == true

    }

    def 'test Action.delete from Post with id --> 200' () {
        when:
        Post response = Post.delete("1");

        then:
        response.isEmpty() == true

    }


    def 'test Action.list from UserPostPath --> 200' () {
        when:
        Map map = [user_id: 1]
        ResourceList<UserPostPath> list = UserPostPath.list(map)

        then:
        list.getList().size() == 1
        Post returned = list.getList().get(0);
        returned.get("id") == 1
        returned.get("title") == "My Title"
        returned.get("body") == "some body text"
        returned.get("userId") == 1

    }



    def 'test Action.list from UserPostHeader --> 200' () {
        when:
        Map map = [user_id: "1"]
        ResourceList<UserPostHeader> list = UserPostHeader.list(map)

        then:
        list.getList().size() == 1
        Post returned = list.getList().get(0);
        returned.get("id") == 1
        returned.get("title") == "My Title"
        returned.get("body") == "some body text"
        returned.get("userId") == 1

    }




}
