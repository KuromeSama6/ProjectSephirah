FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar /app/app.jar
EXPOSE 6023

ENTRYPOINT ["java", "-jar", "app.jar"]