# Multi-stage Dockerfile for JobFit Resume Backend
# Stage 1: Build JAR using Maven & OpenJDK 21
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal hardened JRE runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-privileged service user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy compiled artifact from build stage
COPY --from=build --chown=spring:spring /app/target/backend-1.0.0.jar app.jar

ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/skills/dictionary || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
