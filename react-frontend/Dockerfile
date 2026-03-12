FROM node:22-alpine
RUN mkdir -p /app && chown -R node:node /app
WORKDIR /app
RUN npm install -g serve
USER node
COPY --chown=node:node package*.json ./
RUN npm install
COPY --chown=node:node . .
RUN npm run build
EXPOSE 5173
CMD ["serve", "-s", "dist", "-l", "5173"]