#!/bin/bash

# Add the Bitnami Helm repository
helm repo add bitnami https://charts.bitnami.com/bitnami &&\

# Install PostgreSQL
helm install --set primary.persistence.enabled=false my-postgres bitnami/postgresql