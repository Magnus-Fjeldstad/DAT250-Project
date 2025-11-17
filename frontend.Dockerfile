# Build Vue bundle
FROM node:20-slim AS build
WORKDIR /app

# Install dependencies
COPY poll-frontend/package*.json ./
RUN npm ci

# Build source
COPY poll-frontend/ .
RUN npm run build

# Serve with nginx
FROM nginx:stable-alpine

# Copy generated dist to nginx root
COPY --from=build /app/dist /usr/share/nginx/html

# Support SPA routing
COPY poll-frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
