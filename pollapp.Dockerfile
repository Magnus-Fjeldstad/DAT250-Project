# Build backend
FROM gradle:8.10.2-jdk21 AS backend-builder
WORKDIR /app

# Copy Gradle config first for caching
COPY pollapp/settings.gradle pollapp/build.gradle /app/
COPY pollapp/gradle /app/gradle

# Download deps
RUN gradle build -x test || true

# Copy source and build jar
COPY pollapp /app
RUN gradle bootJar -x test

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
