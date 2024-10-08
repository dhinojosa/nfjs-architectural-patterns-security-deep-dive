#!/bin/zsh

# Generate the Certificate

openssl genrsa -out ca_keys/ca.key 2048

# Create a self-signed certificate for the Certificate Authority

openssl req -x509 -new -nodes -key ca_keys/ca.key -sha256 -days 365 -out ca_keys/ca.crt -subj "/CN=localhost"

# Generate the Server Certificate

openssl genrsa -out server_keys/server.key 2048

# Create a Certificate Signing Request (CSR)

openssl req -new -key server_keys/server.key -out server_keys/server.csr -subj "/CN=localhost"

# Sign the server certificate with the CA

openssl x509 -req -in server_keys/server.csr -CA ca_keys/ca.crt -CAkey ca_keys/ca.key -CAcreateserial -out server_keys/server.crt -days 365 -sha256

# Generate the Client Private Key for mTLS

openssl genrsa -out client_keys/client.key 2048

# Generate a CSR for the Client

openssl req -new -key client_keys/client.key -out client_keys/client.csr -subj "/CN=client"

# Sign the Client Certificate with the CA

openssl x509 -req -in client_keys/client.csr -CA ca_keys/ca.crt -CAkey ca_keys/ca.key -CAcreateserial -out client_keys/client.crt -days 365 -sha256

# Start up the server and perform build of the web

docker-compose up --build -d

# Test mTLS by providing the certificate and the key

curl https://localhost --cacert ca_keys/ca.crt --cert client_keys/client.crt --key client_keys/client.key
