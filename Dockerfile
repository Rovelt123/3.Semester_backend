
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src
RUN mvn clean package -DskipTests



FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=builder /app/target/SP2-1.0-shaded.jar /app/app.jar


EXPOSE 9191

ENTRYPOINT ["java", "-jar", "/app/app.jar"]