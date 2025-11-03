# === Stage 1: Frontend build ===
FROM node:20-slim AS frontend-builder
WORKDIR /frontend

# Copy npm metadata & install dependencies
COPY poll-frontend/package*.json ./
RUN npm install

# Copy full frontend source and build
COPY poll-frontend .
RUN npm run build


# === Stage 2: Backend build ===
FROM gradle:8.10.2-jdk21 AS backend-builder
WORKDIR /app

# Copy gradle metadata first (cache)
COPY pollapp/settings.gradle pollapp/build.gradle /app/pollapp/
COPY pollapp/gradle /app/pollapp/gradle

WORKDIR /app/pollapp
RUN gradle build -x test || true

# Copy full backend
COPY pollapp /app/pollapp

# Ensure static resources folder exists
RUN mkdir -p /app/pollapp/src/main/resources/static

# Copy vite public/ (vite.svg etc) into static BEFORE dist
COPY poll-frontend/public/ /app/pollapp/src/main/resources/static/

# Copy dist contents into static (not the folder itself)
COPY --from=frontend-builder /frontend/dist/ /app/pollapp/src/main/resources/static/

# Build the jar
RUN gradle bootJar -x test


# === Stage 3: Runtime ===
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

COPY --from=backend-builder /app/pollapp/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
