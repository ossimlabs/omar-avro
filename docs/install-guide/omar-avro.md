# OMAR AVRO

## Dockerfile

Here is an example Dockerfile.  omar-base is no more than a CentOS 7 with the latest java installed

```
FROM omar-base
MAINTAINER DigitalGlobe-RadiantBlue
expose 8080
RUN useradd -u 1001 -r -g 0 -d $HOME -s /sbin/nologin -c 'Default Application User' omar
RUN mkdir /usr/share/omar
COPY omar-avro-app-1.0.0-SNAPSHOT.jar /usr/share/omar
RUN chown -R 1001:0 /usr/share/omar
RUN chown 1001:0 /usr/share/omar
RUN chmod -R g+rw /usr/share/omar
RUN find $HOME -type d -exec chmod g+x {} +
USER 1001
WORKDIR /usr/share/omar
CMD ["java", "-server", "-Xms256m", "-Xmx1024m", "-Djava.awt.headless=true", "-XX:+CMSClassUnloadingEnabled", "-XX:+UseGCOverheadLimit", "-Djava.security.egd=file:/dev/./urandom", "-jar", "omar-avro-app-1.0.0-SNAPSHOT.jar"]
```

Ref: [omar-ossim-base](../../../omar-ossim-base/docs/install-guide/omar-ossim-base/)

## JAR
`http://artifacts.radiantbluecloud.com/artifactory/webapp/#/artifacts/browse/tree/General/omar-local/io/ossim/omar/apps/omar-avro-app`

##Configuration

```
serverName: "localhost"
serverProtocol: "http"

omarDb:
  host: 
  port: 5432
  name: omardb-prod
  url: jdbc:postgresql://${omarDb.host}:${omarDb.port}/${omarDb.name}
  driver: org.postgresql.Driver
  dialect: 'org.hibernate.spatial.dialect.postgis.PostgisDialect'
  username: 
  password:

server:
  contextPath:
  port: 8080


environments:
  production:
    dataSource:
      pooled: true
      jmxExport: true
      driverClassName: ${omarDb.driver}
      dialect:  ${omarDb.dialect}
      url:      ${omarDb.url}
      username: ${omarDb.username}
      password: ${omarDb.password}

omar:
  avro:
    sourceUriField: "uRL"
    dateField: "observationDateTime"
    dateFieldFormat: ""
    imageIdField: "imageId"
    jsonSubFieldPath: "Message"
    download:
      directory: "/data/s3"
      command: "wget --no-check-certificate -O <destination> <source>"
    destination:
      type: "post"
      post:
        addRasterEndPoint: http://omar-stager-app:8080/omar-stager/dataManager/addRaster
        addRasterEndPointField: "filename"
        addRasterEndPointParams:
          background: "true"
          buildHistograms: "true"
          buildOverviews: "true"
          overviewCompressionType: "NONE"
          overviewType: "ossim_tiff_box"
          filename: ""
    stagingDelay: 1000
    nAttempts: 3
    attemptDelay: 5000

---
grails:
  serverURL:${serverProtocol}://${serverName}${server.contextPath}

```

* **sourceUriField** Is the source URI field name in the JSON Avro record.
* **dateField (optional)** Is the date field in the JSON Avro Record.  This field is optional and is used as a way to encode the **directory** for storing the image.  If this is not given then the directory suffix will be the path of the **sourceUriField**
* **dateFieldFormat** Is the format of the date field.  If you leave this blank "" then it will default to parsing an ISO8601 date.  Typically left blank.
* **imageIdField** Is the image Id field used to identify the image
* **jsonSubFieldPath** Allows one to specify a path separated by "." to the submessage to where all the image information resides.  For example, if you pass a Message wrapped within the SNS notification it will be a subfield of the SNS message.  This allows one to specify a path to the message to be handled.
* **omar.avro.download** This is the download specifications
 * **directory** This is the directory prefix where the file will be downloaded.  For example,   if we have the **sourceUriField** given as http://\<IP>/\<path>/\<to>/\<image>/foo.tif and the date field content has for a value of 20090215011010  with a dateField format the directory structure will be \<yyyy>/\<mm>/\<dd>/\<hh> where **yyyy** is a 4 character year and the **mm** is the two character month and the **dd** is the two character day and the **hh** is a two character hour.  If the datefield is not specified then we use the path in the URI as a suffix to the local directory defined in the **directory** field above: /data/s3/\<path>/\<to>/\<image>/foo.tif
 * **command** If you do not want the standard HTTP connect to be used in java then you can pass a shell command: ex. `wget -O <destination> <source>` we use where the **source** and **destination** are replaced internally with the proper values.
* **omar.avro.destination**
 * **type** Referes to the type we wish to specify and use.  The values can be "stdout" or "post".  If the value 'stdout' is used it will just do a println of the message. If the type is "post" then it will post the message to the service definition for the endPoint and the Field.
 * **post.addRasterEndPoint** If the destination type is **"post"** then this field needs to be specified to identify the location of the addRaster endpoint.  Typically you will be connecting this to a stager-app endpoint which will have a relative path of dataManager/addRaster.  The example URL was taken from the ossim-vagrant repo definitions.  This will need to be modified for your environment.
 * **post.addRasterEndPointParams** This is used as the post parameters to the URL given by the value **post.addRasterEndPoint** We support modifying the default action being passed and you can specify **background**, **buildHistograms**, **buildOverviews** flags.  The **background** tells the stager to perform the staging as a background process.  If this flag is false it will do the staging inline to the endpoint call.  You can also specify the parameters **overviewCompressionType** which can be of values "NONE","JPEG","PACKBITS", or "DEFLATE" and also the paramter **overviewType** where the value can be "ossim_tiff_box", "ossim_tiff_nearest", or "ossim_kakadu_nitf_j2k".
 * **post.addRasterEndPointField** If the destination type is **"post"** then this field is needed to define the post variable used for the filename.   By default this field should be left as *"filename"*.  It will add the filename value to the addRasterEndPointParams.