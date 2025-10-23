# === Stage 1: Build stage ===
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

# Kopier Gradle config først for caching
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Pre-caching av dependencies
RUN gradle build -x test || true

# Kopier resten av prosjektet
COPY . .

# Full build (uten tester for raskere image build)
RUN gradle bootJar -x test

# === Stage 2: Runtime stage ===
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopier jar fra builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Kjør applikasjonen
ENTRYPOINT ["java", "-jar", "app.jar"]
