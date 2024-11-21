FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean install
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/target/server-0.0.1.jar"]
