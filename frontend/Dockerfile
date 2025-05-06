# Стадия сборки
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install

# Копируем нужный .env файл в зависимости от ARG
ARG ENV_PROFILE=production
COPY .env.${ENV_PROFILE} .env.production
COPY . .

RUN npm run build

# Стадия запуска
FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package*.json ./
RUN npm install --omit=dev

EXPOSE 9090
CMD ["npm", "start"]