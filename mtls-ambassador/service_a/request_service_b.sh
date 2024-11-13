#!/bin/bash

# Send request to service_b via Envoy proxy for service_b (envoy_b)
curl -v --cacert /etc/envoy/certs/ca.crt --cert /etc/envoy/certs/client.crt --key /etc/envoy/certs/client.key https://envoy_b:8444