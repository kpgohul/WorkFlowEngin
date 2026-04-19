#!/bin/bash
# Add Bitnami chart repo
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Install Kafka chart
helm install kafka bitnami/kafka -f values.yaml

# Note: This will create a Kafka broker accessible internally via kafka:9092
