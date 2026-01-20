# Build stage
ARG BUILD_MODE=production
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci

# Copy source code and build
COPY . .
ARG BUILD_MODE=production
RUN npm run build -- --mode ${BUILD_MODE}

# Run stage
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Custom nginx config template for SPA routing and configurable port
ENV PORT=80
RUN printf 'server {\n\
    listen ${PORT};\n\
    location / {\n\
    root /usr/share/nginx/html;\n\
    index index.html index.htm;\n\
    try_files $uri $uri/ /index.html;\n\
    }\n\
    }' > /etc/nginx/conf.d/default.conf.template

# Use envsubst to replace the port in the template at runtime
EXPOSE 80
CMD /bin/sh -c "envsubst '\$PORT' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"
