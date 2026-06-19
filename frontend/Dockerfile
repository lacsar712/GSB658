FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm config set registry https://registry.npmmirror.com
# 遵循用户规范: 必须在有 lock 文件的情况下使用 ci。 
# 如果本地没有生成 package-lock.json，这里 fallback 到 install
RUN npm ci || npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
# 覆盖 nginx 默认配置以支持 Vue Router 的 History 模式
RUN echo 'server { listen 80; client_max_body_size 10M; location / { root /usr/share/nginx/html; index index.html index.htm; try_files $uri $uri/ /index.html; } location /api/ { proxy_pass http://backend:8080/api/; } }' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
