package omar.avro

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Ignore
import spock.lang.Specification
import grails.testing.spring.AutowiredTest
import grails.testing.gorm.DataTest

class AvroServiceSpec extends Specification implements AutowiredTest, DataTest {

    Closure doWithSpring() {
        { ->
            avroService AvroService
        }
    }

    AvroService avroService

    void setup() {
        assert avroService != null
    }

    void cleanup() {
    }

    @Ignore
    void 'Test addMessage'() {
        setup:
        def jsonSlurper = new JsonSlurper()

        def json = jsonSlurper.parse(new File(
                this.getClass().getResource("/test-addMessage.json").toURI()
        ))
        def cmd = new IndexMessageCommand()
        cmd.message = JsonOutput.toJson(json)
        cmd.messageId = json.messageId
        def result = null

        result = avroService.addMessage(cmd)

        expect:
        assert avroService != null

        and:
        result != null
    }
}
