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
