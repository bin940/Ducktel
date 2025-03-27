FROM node:20 AS builder
WORKDIR /app
COPY . .
RUN npm install && npm run build

# nginx를 통한 정적 파일 제공
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
