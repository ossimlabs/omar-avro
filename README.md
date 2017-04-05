# omar-avro
### Welcome to the AVRO Service

[![Build Status](https://jenkins.radiantbluecloud.com/buildStatus/icon?job=omar-avro-dev)]()

This service takes an AVRO JSON payload or JSON record from an AVRO file as input and will process the file by looking for the reference URI field and downloading the File.  The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields

* **S3\_URI\_Nitf** This is a JSON field defining the source URI location of the image we wish to download and process.
* **Observation_Date** This is acquisition date of the image and we use the date field as a way to create a local destination directory for the field
* **Image_Id** This is the Image Id and is used for the destination filename

Git clone or git pull the omar-common repo.
```
  git clone https://github.com/ossimlabs/omar-common.git
```

### Required environment variable
- OMAR_COMMON_PROPERTIES

### Optional environment variables
#### required by Jenkins or a local Artifactory or a local Openshift

- OPENSHIFT_USERNAME
- OPENSHIFT_PASSWORD
- ARTIFACTORY_USER
- ARTIFACTORY_PASSWORD

Example:
```
  export OMAR_COMMON_PROPERTIES=~/omar-common/omar-common-properties.gradle
```

Git clone or git pull the following repos.
1. omar-core
2. omar-hibernate
3. oomar-ingest-metrics

### Install the following plugins in order before installing omar-avro-plugin

1. omar-core-plugin
2. omar-hibernate-spatial-plugin
3. oomar-ingest-metrics-plugin

Example:
```
 cd omar-core/plugins/omar-core-plugin
 ./gradlew clean install
```

### Install omar-avro-plugin before you build/install omar-avro-app
```
 cd omar-avro/plugins/omar-avro-plugin
 ./gradlew clean install
```
