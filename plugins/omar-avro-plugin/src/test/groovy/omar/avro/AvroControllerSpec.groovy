package omar.avro

import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.testing.spring.AutowiredTest
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
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
        status == 200
    }
}
