# Deploying Business Analysts API as a Microservice to Kubernetes using AWS

## Project Overview

This project deploys a Business Analysts API as a microservice to Kubernetes using AWS services.

## Project Steps

### 1. Repository and Docker Image Setup

-  Set up a repository on AWS Elastic Container Registry (ECR) for storing Docker images.
-  Configured AWS CodeBuild to build and push Docker images triggered by GitHub "push" events.

### 2. AWS CodeBuild Pipeline

-  Established a CodeBuild pipeline for continuous integration with the necessary ECR roles.

### 3. Amazon EKS Cluster Setup

-  Created an Amazon EKS cluster with a node group, using suitable hardware and software components.
-  Using `config-eks.sh` script to update config in local.

### 4. AWS EKS and VS Studio Integration

-  Initialized communication between AWS EKS service and VS Studio terminal.

### 5. Helm Charts for Database Configuration

-  Configured the database using Helm Charts with simple commands in script folder and seed sample data from `seed_data.sh`.

### 6. Kubernetes Deployment Configuration

-  Created deployment configurations (`deployment.yaml`, `service.yaml`, `configmap.yaml`, `secrets.yaml`) for running the microservice as a pod in Kubernetes using deploy.sh in script folder.

### 7. Database Service Configuration

-  Ensured the database service is running and accessible by the deployed microservice.
-  Set up to send directed logs to AWS CloudWatch for troubleshooting using `cloudwatch.sh` script.

## Conclusion

Following these steps ensures the automatic deployment and scalability of the microservice on Kubernetes using AWS services.
