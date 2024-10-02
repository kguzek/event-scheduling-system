FROM eclipse-temurin:21-jdk-alpine AS build
COPY server .
RUN mvn clean package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/target/server-0.0.1.jar"]
