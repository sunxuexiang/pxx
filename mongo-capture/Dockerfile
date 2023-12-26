FROM 172.19.25.163:5000/jre8:0.4
ARG jarVersion
COPY target/mongo-capture$jarVersion.jar /data/server/bin/sbc.jar
RUN ln -s /data/server/bin/sbc.jar /data/server/bin/server.jar
CMD /data/server/bin/startup.sh; sleep inf