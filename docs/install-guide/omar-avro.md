# OMAR AVRO

## Source Location
[https://github.com/ossimlabs/omar-avro](https://github.com/ossimlabs/omar-avro)

## Purpose
The AVRO service takes an AVRO JSON payload or JSON record from an AVRO file as input and will process the file by looking for the reference URI field and downloading the File.

## Dockerfile
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
[https://artifactory.ossim.io/artifactory/webapp/#/artifacts/browse/tree/General/omar-local/io/ossim/omar/apps/omar-avro-app](https://artifactory.ossim.io/artifactory/webapp/#/artifacts/browse/tree/General/omar-local/io/ossim/omar/apps/omar-avro-app)

## Installation in Openshift

**Assumption:** The omar-avro-app docker image is pushed into the OpenShift server's internal docker registry and available to the project.

### Persistent Volumes

Avro requires shared access to OSSIM imagery data. This data is assumed to be accessible from the local filesystem of the pod. Therefore, a volume mount must be mapped into the container. A PersistentVolumeClaim should be mounted to a configured location (see environment variables) in the service, but is typically */data*

### Environment variables

|Variable|Value|
|------|------|
|SPRING_PROFILES_ACTIVE|Comma separated profile tags (*e.g. production, dev*)|
|SPRING_CLOUD_CONFIG_LABEL|The Git branch from which to pull config files (*e.g. master*)|

### An Example DeploymentConfig
```yaml
apiVersion: v1
kind: DeploymentConfig
metadata:
  annotations:
    openshift.io/generated-by: OpenShiftNewApp
  creationTimestamp: null
  generation: 1
  labels:
    app: omar-openshift
  name: omar-avro-app
spec:
  replicas: 1
  selector:
    app: omar-openshift
    deploymentconfig: omar-avro-app
  strategy:
    resources: {}
    rollingParams:
      intervalSeconds: 1
      maxSurge: 25%
      maxUnavailable: 25%
      timeoutSeconds: 600
      updatePeriodSeconds: 1
    type: Rolling
  template:
    metadata:
      annotations:
        openshift.io/generated-by: OpenShiftNewApp
      creationTimestamp: null
      labels:
        app: omar-openshift
        deploymentconfig: omar-avro-app
    spec:
      containers:
      - env:
        - name: SPRING_PROFILES_ACTIVE
          value: dev
        - name: SPRING_CLOUD_CONFIG_LABEL
          value: master
        image: 172.30.181.173:5000/o2/omar-avro-app@sha256:937ef7677960d1f31bf33b7a790c224b255a26aa880d548233556eca107a381c
        imagePullPolicy: Always
        livenessProbe:
          failureThreshold: 3
          initialDelaySeconds: 60
          periodSeconds: 10
          successThreshold: 1
          tcpSocket:
            port: 8080
          timeoutSeconds: 1
        name: omar-avro-app
        ports:
        - containerPort: 8080
          protocol: TCP
        readinessProbe:
          failureThreshold: 3
          initialDelaySeconds: 30
          periodSeconds: 10
          successThreshold: 1
          tcpSocket:
            port: 8080
          timeoutSeconds: 1
        resources:
          limits:
            memory: 4Gi
        terminationMessagePath: /dev/termination-log
        volumeMounts:
        - mountPath: /data
          name: volume-avro
      dnsPolicy: ClusterFirst
      restartPolicy: Always
      securityContext: {}
      terminationGracePeriodSeconds: 30
      volumes:
      - name: volume-avro
        persistentVolumeClaim:
          claimName: ossim-data-dev-pvc
  test: false
  triggers:
  - type: ConfigChange
  - imageChangeParams:
      automatic: true
      containerNames:
      - omar-avro-app
      from:
        kind: ImageStreamTag
        name: omar-avro-app:latest
        namespace: o2
    type: ImageChange
```

## Configuration

|Variable|Description|
|------|------|
|sourceUriField|Is the source URI field name in the JSON Avro record.|
|dateField (optional)|Is the date field in the JSON Avro Record.  This field is optional and is used as a way to encode the **directory** for storing the image.  If this is not given then the directory suffix will be the path of the **sourceUriField**|
|dateFieldFormat|Is the format of the date field.  If you leave this blank "" then it will default to parsing an ISO8601 date.  Typically left blank.|
|imageIdField|Is the image Id field used to identify the image|
|jsonSubFieldPath|Allows one to specify a path separated by "." to the submessage to where all the image information resides.  For example, if you pass a Message wrapped within the SNS notification it will be a subfield of the SNS message.  This allows one to specify a path to the message to be handled.|
|omar.avro.download|This is the download specifications|
||**directory** This is the directory prefix where the file will be downloaded.  For example,   if we have the **sourceUriField** given as http://[path]/[to]/[image]/foo.tif and the date field content has for a value of 20090215011010  with a dateField format the directory structure will be \<yyyy>/\<mm>/\<dd>/\<hh> where **yyyy** is a 4 character year and the **mm** is the two character month and the **dd** is the two character day and the **hh** is a two character hour.  If the datefield is not specified then we use the path in the URI as a suffix to the local directory defined in the **directory** field above: /data/s3/[path]/[to]/[image]/foo.tif|
||**command** If you do not want the standard HTTP connect to be used in java then you can pass a shell command: ex. `wget -O <destination> <source>` we use where the **source** and **destination** are replaced internally with the proper values.|
|omar.avro.destination||
 ||**type** Referes to the type we wish to specify and use.  The values can be "stdout" or "post".  If the value 'stdout' is used it will just do a println of the message. If the type is "post" then it will post the message to the service definition for the endPoint and the Field.|
||**post.addRasterEndPoint** If the destination type is **"post"** then this field needs to be specified to identify the location of the addRaster endpoint.  Typically you will be connecting this to a stager-app endpoint which will have a relative path of dataManager/addRaster.  The example URL was taken from the ossim-vagrant repo definitions.  This will need to be modified for your environment.|
||**post.addRasterEndPointParams** This is used as the post parameters to the URL given by the value **post.addRasterEndPoint** We support modifying the default action being passed and you can specify **background**, **buildHistograms**, **buildOverviews** flags.  The **background** tells the stager to perform the staging as a background process.  If this flag is false it will do the staging inline to the endpoint call.  You can also specify the parameters **overviewCompressionType** which can be of values "NONE","JPEG","PACKBITS", or "DEFLATE" and also the paramter **overviewType** where the value can be "ossim_tiff_box", "ossim_tiff_nearest", or "ossim_kakadu_nitf_j2k".|
||**post.addRasterEndPointField** If the destination type is **"post"** then this field is needed to define the post variable used for the filename.   By default this field should be left as *"filename"*.  It will add the filename value to the addRasterEndPointParams.|


 ## Application Configuration YAML
