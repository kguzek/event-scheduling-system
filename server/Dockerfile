FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY application.properties .
COPY src ./src
RUN mvn clean install
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/target/server-0.0.1.jar"]
