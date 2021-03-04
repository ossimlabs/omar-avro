package omar.avro

import spock.lang.Specification

class GetMessageCommandSpec extends Specification {

    GetMessageCommand cmd = new GetMessageCommand()

    void "test negative value"() {
        when:
        cmd.limit = -5
        cmd.offset = -5
        cmd.validate()

        then:
        cmd.errors.allErrors.size() == 2
    }

    void "test zero values"() {
        when:
        cmd.limit = 0
        cmd.offset = 0
        cmd.validate()

        then:
        !cmd.hasErrors()
    }

    void "test positive values"() {
        when:
        cmd.limit = 1
        cmd.offset = 1
        cmd.validate()

        then:
        !cmd.hasErrors()
    }
}
