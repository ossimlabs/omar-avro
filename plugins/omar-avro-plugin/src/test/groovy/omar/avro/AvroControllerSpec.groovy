package omar.avro

import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.testing.spring.AutowiredTest
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import org.springframework.http.HttpStatus
import spock.lang.Ignore
import spock.lang.Specification

class AvroControllerSpec extends Specification implements AutowiredTest, DataTest, ControllerUnitTest<AvroController> {

    Closure doWithSpring() {
        { ->
            avroService AvroService
        }
    }

    AvroService avroService
    JsonSlurper jsonSlurper

    void setup() {
        jsonSlurper = new JsonSlurper()
    }

    @Ignore
    void "test addMessage"() {
        given:
        def msg = jsonSlurper.parse(new File(
                this.getClass().getResource("/controller-addMessage.json").toURI()
        ))
        request.JSON = JsonOutput.toJson(msg)
        request.method = 'POST'

        when:
        controller.addMessage()

        then:
        status == HttpStatus.OK.value()
    }

    void "listMessages should succeed"() {
        given:
        GetMessageCommand cmd = new GetMessageCommand()

        when:
        cmd.limit = 1
        cmd.offset = 1
        controller.listMessages(cmd)

        then:
        status == HttpStatus.OK.value()
    }

    void "listMessages bad limit"() {
        given:
        GetMessageCommand cmd = new GetMessageCommand()

        when:
        cmd.limit = -5
        cmd.offset = 1
        controller.listMessages(cmd)

        then:
        status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }

    void "listMessages bad offset"() {
        given:
        GetMessageCommand cmd = new GetMessageCommand()

        when:
        cmd.offset = -5
        cmd.limit = 1
        controller.listMessages(cmd)

        then:
        status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }
    void "listFiles should succeed"() {
        given:
        GetMessageCommand cmd = new GetMessageCommand()

        when:
        cmd.limit = 1
        cmd.offset = 1
        controller.listMessages(cmd)

        then:
        status == HttpStatus.OK.value()
    }

    void "listFiles bad limit"() {
        given:
        GetMessageCommand cmd = new GetMessageCommand()

        when:
        cmd.limit = -5
        cmd.offset = 1
        controller.listMessages(cmd)

        then:
        status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }

    void "listFiles bad offset"() {
        given:
        GetMessageCommand cmd = new GetMessageCommand()

        when:
        cmd.offset = -5
        cmd.limit = 1
        controller.listMessages(cmd)

        then:
        status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }
}
