package omar.avro

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetFileCommand implements Validateable
{
   Integer offset
   Integer limit
   static constraints = {
      offset nullable: true, min: 0
      limit nullable: true, min: 0
   }
}
