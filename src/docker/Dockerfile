FROM openjdk:8-jre-alpine
ENV INGRID_USER=root
ENV RUN_DIRECTLY=true

ADD ingrid-iplug-*-installer.jar /

RUN unzip ingrid-iplug-*-installer.jar -d /tmp \
    && mkdir -p /opt/ingrid/ingrid-iplug-wfs-dsc \
    && cp -R /tmp/ingrid-iplug-wfs-dsc-*/* /opt/ingrid/ingrid-iplug-wfs-dsc/ \
    && rm -Rf /tmp/* \
    && sed -i -r 's/(<AppenderRef.*\/>)/\1<AppenderRef ref="Console" \/>/g' /opt/ingrid/ingrid-iplug-wfs-dsc/conf/log4j2.xml \
    && rm ingrid-*.jar

RUN echo jetty.port=80 > /opt/ingrid/ingrid-iplug-wfs-dsc/conf/config.override.properties
WORKDIR /opt/ingrid/ingrid-iplug-wfs-dsc
EXPOSE 80

CMD /bin/sh start.sh start
