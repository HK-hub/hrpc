FROM openjdk:8-alpine
VOLUME /tmp
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone
ADD demo-docker-provider.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.io.tmpdir=/tmp -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
