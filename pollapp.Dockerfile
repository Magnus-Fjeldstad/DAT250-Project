# Build backend
FROM gradle:8.10.2-jdk21 AS backend-builder
WORKDIR /app

# Copy Gradle config first (cache optimization)
COPY pollapp/settings.gradle pollapp/build.gradle /app/
COPY pollapp/gradle /app/gradle

# Download dependencies (ignore tests for caching)
RUN gradle build -x test || true

# Copy source and build jar
COPY pollapp /app
RUN gradle bootJar -x test

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create writable directory for H2 database
RUN mkdir -p /data && chmod 777 /data

# Copy application jar from builder
COPY --from=backend-builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
