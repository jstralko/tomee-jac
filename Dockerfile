 
 FROM azul/zulu-openjdk-alpine:8u202

 RUN apk update
 RUN apk add --no-cache gnupg
 RUN apk add curl

 #Ref: https://github.com/tomitribe/docker-tomee/blob/345cfefbfe4ef0956f0ea2caf1af2595111f5a85/8-jre-1.7.5-plume/Dockerfile
 #--------Start of TomEE base install------------

 ENV PATH /usr/local/tomee/bin:$PATH
 RUN mkdir -p /usr/local/tomee

 WORKDIR /usr/local/tomee

 RUN curl -fSL https://dist.apache.org/repos/dist/release/tomee/tomee-8.0.0-M2/apache-tomee-8.0.0-M2-plume.tar.gz -o tomee.tar.gz \
 	&& tar -zxf tomee.tar.gz \
 	&& mv apache-tomee-plume-8.0.0-M2/* /usr/local/tomee \
 	&& rm -Rf apache-tomee-plume-8.0.0-M2 \
 	&& rm tomee.tar.gz*

 #-----end of TomEE Base install----------------

COPY tomee.xml /usr/local/tomee/conf
COPY system.properties /usr/local/tomee/conf
COPY wk-amsp.jar /usr/local/tomee/lib

# Should be generic-jms-ra (we have manually changes)
COPY rar-libs/*.jar /usr/local/tomee/lib/

#Dependences for the Generic JMS RA
ADD https://repo1.maven.org/maven2/org/jboss/logging/jboss-logging/3.3.1.Final/jboss-logging-3.3.1.Final.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar /urs/local/tomee/lib

#ARG PROTONJ_VERSION=0.27.3
ARG PROTONJ_VERSION=0.33.1
ADD https://repo1.maven.org/maven2/org/apache/qpid/proton-j/$PROTONJ_VERSION/proton-j-$PROTONJ_VERSION.jar /usr/local/tomee/lib

ARG QPID_JMS_CLIENT_VERSION=0.44.0
ADD https://repo1.maven.org/maven2/org/apache/qpid/qpid-jms-client/$QPID_JMS_CLIENT_VERSION/qpid-jms-client-$QPID_JMS_CLIENT_VERSION.jar /usr/local/tomee/lib

#ARG NETTY_VERSION=4.1.16.Final
ARG NETTY_VERSION=4.1.37.Final

#Pull in the Netty Deps
ADD https://repo1.maven.org/maven2/io/netty/netty-handler/$NETTY_VERSION/netty-handler-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-buffer/$NETTY_VERSION/netty-buffer-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-codec/$NETTY_VERSION/netty-codec-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-codec-http/$NETTY_VERSION/netty-codec-http-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-common/$NETTY_VERSION/netty-common-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-resolver/$NETTY_VERSION/netty-resolver-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-transport/$NETTY_VERSION/netty-transport-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-transport-native-epoll/$NETTY_VERSION/netty-transport-native-epoll-$NETTY_VERSION.jar /usr/local/tomee/lib
ADD https://repo1.maven.org/maven2/io/netty/netty-transport-native-kqueue/$NETTY_VERSION/netty-transport-native-kqueue-$NETTY_VERSION.jar /usr/local/tomee/lib

RUN mkdir /usr/local/tomee/apps

COPY ra.rar /usr/local/tomee/apps
COPY app/ear/target/*.ear /usr/local/tomee/apps

 #Debug
 ENV JPDA_ADDRESS="8000"
 ENV JPDA_TRANSPORT="dt_socket"

 ENTRYPOINT ["catalina.sh", "jpda", "run"]