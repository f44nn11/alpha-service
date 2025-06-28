FROM openjdk:17-slim
LABEL authors="fkusu"
WORKDIR /app

RUN useradd -u 1000 -m -s /bin/bash userapp
RUN mkdir -p /app/document/support && chown -R userapp:userapp /app/document/support

COPY target/alpha-service-0.0.1-SNAPSHOT.jar /app/alpha-service.jar
COPY src/main/resources/log/log4j2.xml /app/log/log4j2.xml
RUN chmod -R 755 /app && chown -R userapp:userapp /app

USER root
EXPOSE 9131
ENTRYPOINT ["java", "-cp", "/app:/app/log","-jar","/app/alpha-service.jar"]