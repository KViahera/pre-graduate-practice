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
COPY --from=builder /app/dist ./dist
RUN chmod -R a-w ./dist && chown -R node:node ./dist
USER node
EXPOSE 5173
CMD ["serve", "-s", "dist", "-l", "5173"]