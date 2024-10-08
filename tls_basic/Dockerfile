FROM nginx:alpine

COPY server_keys/server.crt /etc/nginx/server.crt
COPY server_keys/server.key /etc/nginx/server.key
COPY ca_keys/ca.crt /etc/nginx/ca.crt
COPY nginx.conf /etc/nginx/nginx.conf
