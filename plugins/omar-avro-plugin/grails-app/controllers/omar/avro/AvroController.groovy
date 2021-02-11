package omar.avro
import omar.core.BindUtil
import grails.converters.JSON

import io.swagger.annotations.*
@Api(value = "Avro",
     description = "API operations for AVRO Payload manipulation",
     produces = 'application/json',
     consumes = 'application/json'
)
class AvroController {
   def avroService
   static allowedMethods = [index:["GET"],
                            addFile:["POST", "GET"],
                            listFile:["GET"],
                            resetFileProcessingStatus:["POST","GET"],
                            addMessage:["POST"],
                            listMessage:["GET"]
                            ]

   def index() { render "" }

   @ApiOperation(value = "Add a file",
                 consumes= 'application/json',
                 produces='application/json',
                 httpMethod="POST",
                            notes = """
Calling this URL endpoint **addFile** allows one to add the file to the background job for posting to the staging or indexing service. If the file has previously **FAILED** it will be updated back to a ready state and tried again.  

## Parameter List

*   **filename**

    This is the local filename under the directory tree that is the result of the Avro Message downloaded from a reference URI.    """)
   @ApiImplicitParams([
           @ApiImplicitParam(name =
            'filename', value = 'File to have posted and indexed', required=true, paramType = 'query', dataType = 'string'),
   ])
   def addFile()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new IndexFileCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( IndexFileCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.addFile(cmd)

      response.status = result.statusCode
      render contentType: "application/json", text: result as JSON

   }
   @ApiOperation(value = "List files",
                 consumes= 'application/json',
                 produces= 'application/json',
                 httpMethod="GET",
                 notes = """
The service api **listFiles** supports pagination and will list the current local files being processed. It will return the processing status of the file if its in the READY, RUNNING, PAUSED, CANCELED, FINISHED, FAILED state.  

## Parameter List

*   **offset**

    This field is used in pagination and allows one to page the requests. The offset is the record offset for the next **limit** number of items

*   **limit**

    This parameter is used in pagination to define a limit on the number of items returned    """)
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'offset', value = 'Process Id', required=false, paramType = 'query', dataType = 'integer'),
           @ApiImplicitParam(name = 'limit', value = 'Process status', defaultValue = '', paramType = 'query', dataType = 'integer'),
   ])
   def listFiles()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new GetFileCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( GetFileCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.listFiles(cmd)

      render contentType: "application/json", text: result as JSON
   }
   @ApiOperation(value = "Reset File Processing Status", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'status', value = 'Set process status', required=true, allowableValues="READY,PAUSED,CANCELED,FINISHED,FAILED",  defaultValue = 'READY', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'processId', value = 'Process Id', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'whereStatusEquals', value = 'Where status equals', allowableValues="READY,PAUSED,CANCELED,FINISHED,FAILED,RUNNING",  defaultValue = '', paramType = 'query', dataType = 'string'),
   ])
   def resetFileProcessingStatus()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new ResetFileProcessingCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( ResetFileProcessingCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.resetFileProcessingCommand(cmd)

      response.status = result.statusCode
      render contentType: "application/json", text: result as JSON

   }
   @ApiOperation(value = "Add message", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'body',
                   value = "General Message for a single record Avro Payload",
                   paramType = 'body',
                   dataType = 'string')
   ])
   def addMessage()
   {
      //println "REQUEST ========================= ${params}"

      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new IndexMessageCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( IndexMessageCommand, requestParams )
      bindData( cmd, requestParams )
      cmd.message = request.JSON
      HashMap result = avroService.addMessage(cmd)

      response.status = result.statusCode
      render contentType: "application/json", text: result as JSON
   }

   @ApiOperation(value = "List Messages",
                 consumes= 'application/json',
                 produces='application/json',
                 httpMethod="GET",
                 notes = """
The service api **listMessages** supports pagination and will list the current messages being processed. It returns the messageID, payload and the date the message was created  

## Parameter List

*   **offset**

    This field is used in pagination and allows one to page the requests. The offset is the record offset for the next **limit** number of items

*   **limit**

    This parameter is used in pagination to define a limit on the number of items returned    """)
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'offset', value = 'Process Id', required=false, paramType = 'query', dataType = 'integer'),
           @ApiImplicitParam(name = 'limit', value = 'Process status', defaultValue = '', paramType = 'query', dataType = 'integer'),
   ])
   def listMessages()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new GetMessageCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( GetMessageCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.listMessages(cmd)

      render contentType: "application/json", text: result as JSON
   }
}
