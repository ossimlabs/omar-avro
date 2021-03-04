package omar.avro

import spock.lang.Specification

class GetFileCommandSpec extends Specification {

    GetFileCommand cmd = new GetFileCommand()

    void "negative values"() {
        when:
        cmd.limit = -5
        cmd.offset = -5
        cmd.validate()

        then:
        cmd.errors.allErrors.size() == 2
    }

    void "zero values"() {
        when:
        cmd.limit = 0
        cmd.offset = 0
        cmd.validate()

        then:
        !cmd.hasErrors()
    }

    void "positive values"() {
        when:
        cmd.limit = 1
        cmd.offset = 1
        cmd.validate()

        then:
        !cmd.hasErrors()
    }
}
