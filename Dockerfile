# Multi-stage build for Spring Boot Backend
# Stage 1: Build the JAR file with Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy dependency files first (for Docker cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application (smaller image, only JRE)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/cinemas-app-1.0.0.jar app.jar

# Expose port (internal port, accessible via Nginx reverse proxy)
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]