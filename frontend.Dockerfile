# Build Vue
FROM node:20-slim AS builder
WORKDIR /app
COPY poll-frontend/package*.json ./
RUN npm ci
COPY poll-frontend/ .
RUN npm run build

# Serve with nginx
FROM nginx:stable-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
# Provide nginx config supporting SPA routing
COPY poll-frontend/nginx.conf /etc/nginx/conf.d/default.conf
