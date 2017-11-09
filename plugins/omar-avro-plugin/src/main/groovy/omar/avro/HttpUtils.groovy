package omar.avro

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.TEXT
import omar.core.HttpStatus
import java.net.URLConnection
import java.io.BufferedInputStream
import java.io.FileOutputStream

@Slf4j
class HttpUtils
{
   static void downloadURI(String destination, String sourceURI)
   {
      URL url = new URL(sourceURI)
      File file = new File(destination)
      if(file.exists())
      {
         file.delete()
      }

      URLConnection connection = url.openConnection();
      connection.setReadTimeout(5000);
      connection.connect();
      // this will be useful so that you can show a typical 0-100% progress bar
      //      int fileLength = connection.contentLength;
      // download the file
      def input = new BufferedInputStream(connection.inputStream);
      def output = new FileOutputStream(file);

      def buffer = new byte[4096];
      long total = 0;
      int count=0;
      while (((count = input.read(buffer)) != -1)) {
         total += count;
         //            publishProgress((int) (total * 100 / fileLength - 1));
         output.write(buffer, 0, count);
      }
      output?.flush();
      output?.close();
   }
   static void downloadURIShell(String shellCommand, String destination, String sourceURI)
   {
      String tempShellCommand = shellCommand

      tempShellCommand = tempShellCommand.replaceFirst("<source>", sourceURI)
      tempShellCommand = tempShellCommand.replaceFirst("<destination>", destination)

//      println "EXECUTING: ${tempShellCommand}"
      def shellProcess = tempShellCommand.execute()
      shellProcess.consumeProcessOutput(new NullOutputStream(), new NullOutputStream())
      shellProcess.waitFor()
//        println "DONE!!!!!!!!!!!!"
      // if we are non zero return then throw exception
      if(shellProcess.exitValue())
      {
         throw new Exception("Unable to execute command ${tempShellCommand}")
      }

   }

   static HashMap postMessage(String url, HashMap params)//String field, String message)
   {
      def result = [status:200,message:""]
      URL tempUrl = new URL(url)
      String host = getHostFromUrl(tempUrl)
      String path = tempUrl.path

      try{
         def http = new HTTPBuilder( host )
         def queryParams = params
         queryParams = queryParams + tempUrl.params
         http.handler.failure = {resp->
            result.status = resp.status
            result.message = resp.statusLine
         }
         http.post( path: path, query:queryParams,//body: postBody,
                 requestContentType: URLENC )
                 { resp ->
                    result.message = resp.statusLine
                    result.status = resp.statusLine.statusCode
                 }
      }
      catch(e)
      {
         result.status = HttpStatus.NOT_FOUND
         result.message = e.toString()

         log.error result.message
      }

      result
   }

    static String getHostFromUrl(URL url) {
        String host
        if(url.port> 0)
        {
            host = "${url.protocol}://${url.host}:${url.port}".toString()
        }
        else
        {
            host = "${url.protocol}://${url.host}".toString()
        }
        return host
    }

    static Map postToAvroMetadata(String url, String body)
    {
        def result = [status:200,message:""]
        URL tempUrl = new URL(url)
        String host = getHostFromUrl(tempUrl)
        String path = tempUrl.path

        log.debug "url: ${path}"
        log.debug "body: ${body}"
        try {
            def http = new HTTPBuilder(host)

            // Set http failure handler
            http.handler.failure = {resp->
                result.status = resp.status
                result.message = resp.statusLine
                log.error "${result.message}"
            }

            http.post(path: "/", body: body, requestContentType: JSON) { resp ->
                result.message = resp.statusLine
                result.status = resp.statusLine.statusCode
                log.info "${result.message}"
            }
        }
        catch (e)
        {
            result.status = HttpStatus.NOT_FOUND
            result.message = e.toString()

            log.error "${result.message}"
        }
        return result
    }

}