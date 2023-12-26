#FROM 172.19.25.163:5000/jre8:0.4
#FROM 172.31.56.145:80/openjdk:8-jre-alpine
FROM 172.31.56.145/xyy_image/openjdk:8-jdk-alpine
#ARG jarVersion
#COPY service-account-app/target/service-account-app-$jarVersion.jar /data/server/bin/sbc.jar
WORKDIR /data/server/bin/
#WORKDIR /data/server/conf/
COPY service-account-app/target/service-account-app-xyy-4.0.9-SNAPSHOT.jar /data/server/bin/sbc.jar
RUN ln -s /data/server/bin/sbc.jar /data/server/bin/server.jar && mkdir -p /data/server/conf/ && \
    echo "spring.cloud.nacos.config.server-addr=172.31.56.144:8848" > /data/server/conf/application.properties && \
    echo "spring.application.name=sbc-service-customer" >> /data/server/conf/application.properties && \
    echo "spring.cloud.nacos.config.prefix=sbc-service-customer" >> /data/server/conf/application.properties && \
    echo "spring.cloud.nacos.config.file-extension=properties" >> /data/server/conf/application.properties && \
    echo "spring.cloud.nacos.config.enabled=true" >> /data/server/conf/application.properties && \
    echo "spring.cloud.nacos.config.shared-dataids=common.properties,mysql-setting.properties" >> /data/server/conf/application.properties && \
    echo "spring.cloud.nacos.config.refreshable-dataids=common.properties" >> /data/server/conf/application.properties
#CMD /data/server/bin/startup.sh; sleep inf
#CMD ["/bin/sh","-c","java -Darms.appName=sbc-account -Dspring.profiles.active=dev -Dfile.encoding=utf-8  -Xms512M -Xmx512M -Dspring.config.location=/data/server/conf/application.properties -jar  /data/server/bin/sbc.jar"]