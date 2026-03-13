FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY src ./src
COPY angular.json tsconfig.json tsconfig.app.json ./
RUN npm run build -- --configuration production


FROM nginxinc/nginx-unprivileged:alpine-slim
USER root
RUN echo 'server { \
    listen 8080; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

COPY --from=builder /app/dist/angular-frontend/browser /usr/share/nginx/html/
RUN chmod -R a-w /usr/share/nginx/html && chown -R 101:101 /usr/share/nginx/html
USER 101
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]