# === Stage 1: Frontend build ===
FROM node:20-slim AS frontend-builder
WORKDIR /frontend

# Copy npm metadata
COPY poll-frontend/package*.json ./

# Install all dependencies cleanly (always fresh & deterministic)
RUN npm ci

# Copy full frontend source
COPY poll-frontend/ .

# Build frontend
RUN npm run build


# === Stage 2: Backend build ===
FROM gradle:8.10.2-jdk21 AS backend-builder
WORKDIR /app

# Copy gradle metadata first for caching
COPY pollapp/settings.gradle pollapp/build.gradle /app/pollapp/
COPY pollapp/gradle /app/pollapp/gradle

WORKDIR /app/pollapp

# Download gradle dependencies without source code
RUN gradle build -x test || true

# Now copy the actual backend source
COPY pollapp /app/pollapp

# Ensure Spring static folder exists
RUN mkdir -p /app/pollapp/src/main/resources/static

# Copy Vite public/ static files
COPY poll-frontend/public/ /app/pollapp/src/main/resources/static/

# Copy built frontend into static folder
COPY --from=frontend-builder /frontend/dist/ /app/pollapp/src/main/resources/static/

# Build the executable jar
RUN gradle bootJar -x test


# === Stage 3: Runtime ===
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

COPY --from=backend-builder /app/pollapp/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
