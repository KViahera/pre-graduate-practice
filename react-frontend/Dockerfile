FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci 
COPY . .
RUN npm run build
FROM node:22-alpine
RUN mkdir -p /app && chown -R node:node /app
WORKDIR /app
RUN npm install -g serve
USER node
COPY --from=builder --chown=node:node --chmod=0555 /app/dist ./dist
EXPOSE 5173
CMD ["serve", "-s", "dist", "-l", "5173"]