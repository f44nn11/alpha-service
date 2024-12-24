FROM openjdk:17-slim
LABEL authors="fkusu"
WORKDIR /app
COPY target/alpha-service-0.0.1-SNAPSHOT.jar /app/alpha-service.jar
COPY src/main/resources/log/log4j2.xml /app/log/log4j2.xml
EXPOSE 9131
ENTRYPOINT ["java", "-cp", "/app:/app/log","-jar","/app/alpha-service.jar"]