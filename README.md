# omar-avro
## Welcome to the AVRO Service

[![Build Status](https://jenkins.radiantbluecloud.com/buildStatus/icon?job=omar-avro-dev)](https://jenkins.radiantbluecloud.com/view/OMAR-DEV-Builds/job/omar-avro-dev/)

This service takes an AVRO JSON payload or JSON record from an AVRO file as input and will process the file by looking for the reference URI field and downloading the File.  The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields

* **S3\_URI\_Nitf** This is a JSON field defining the source URI location of the image we wish to download and process.
* **Observation_Date** This is acquisition date of the image and we use the date field as a way to create a local destination directory for the field
* **Image_Id** This is the Image Id and is used for the destination filename
