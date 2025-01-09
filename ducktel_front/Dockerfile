# Step 1: Node.js를 사용하여 Vue 앱 빌드
FROM node:18 AS build
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package*.json ./

# 의존성 설치
RUN npm install

# 애플리케이션 코드 복사
COPY . .

# Vue 프로젝트 빌드
RUN npm run build

# Step 2: Nginx를 사용하여 정적 파일 제공
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx 포트 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]