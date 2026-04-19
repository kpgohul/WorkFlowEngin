# Deploying Microservices on Minikube

This guide contains step-by-step commands and explanations for each aspect of deploying your Spring Boot Microservices project onto a local Kubernetes instance using Minikube.

## Step 1: Start Minikube & Configure Docker

Start your Minikube cluster and switch your Docker environment to point to Minikube's internal Docker daemon. This guarantees the images you build locally are immediately accessible to Kubernetes.

```bash
# Start minikube
minikube start --memory=4096 --cpus=4

# Point your terminal to use minikube's docker daemon
eval $(minikube docker-env)
```

## Step 2: Build the Docker Images

Now build the images natively inside the Minikube Docker daemon. Remember they are using multi-stage builds so it will package the app using the Maven wrapper internally.

```bash
cd /Users/gohul-nts0352/Documents/SpringUsage/FlowForge

docker build -t action-service:latest ./actionservice
docker build -t auth-service:latest ./authserver
docker build -t execution-service:latest ./executionservice
docker build -t gateway-service:latest ./gatewayserver
docker build -t notification-service:latest ./notificationservice
docker build -t user-service:latest ./userservice
docker build -t workflow-service:latest ./workflowservice
```

## Step 3: Global Setup (Postgres, Secrets, ConfigMaps)

Before turning on the microservices, we need our databases, Kafka, and configs deployed.

```bash
# Create the Secrets
kubectl apply -f k8s/secrets/secrets.yaml

# Create the ConfigMaps
kubectl apply -f k8s/configmaps/configmaps.yaml

# Deploy PostgreSQL
kubectl apply -f k8s/postgres/postgres.yaml
```

## Step 4: Install Kafka Using Helm

Deploy Kafka using Bitnami's Helm chart. 

```bash
cd k8s/kafka
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install kafka bitnami/kafka -f values.yaml
cd ../..
```

Wait until the Kafka broker is running (it can take a few minutes):
```bash
kubectl get pods -w
```
*(Press Ctrl+C once `kafka-0` is ready)*

## Step 5: Deploy the Microservices

Next, apply all the Deployment and Service manifests to spin up your Spring Boot applications.

```bash
# Apply Deployments
kubectl apply -f k8s/deployments/

# Apply Services
kubectl apply -f k8s/services/
```

Check the status of all pods to make sure they're running successfully.
```bash
kubectl get pods
```

## Step 6: Accessing the Application

Because you're using Minikube, we mapped `gateway-service` as a `NodePort` service. You can ask Minikube to provide a direct tunnel to it:

```bash
minikube service gateway-service
```
This command will open a browser or output a local URL (e.g. `http://127.0.0.1:xyz`) perfectly routed.

---

## 📁 Explanations of What We Created

1. **`k8s/deployments/`**
   - Each microservice has its own Deployment YAML. We configured them to use `imagePullPolicy: Never` so they directly use the copies you built into Minikube. They mount the general configurations from `ConfigMap` and secrets from the `Secret`.

2. **`k8s/services/`**
   - Contains all the `ClusterIP` services required for Inter-Service communication (`http://action-service:8080`).
   - The Gateway service is setup as a `NodePort` specifically so you can externally reach it via Minikube.

3. **`k8s/configmaps/configmaps.yaml` & `k8s/secrets/secrets.yaml`**
   - Houses the configurations that used to be dynamically pulled tightly from `configserver`. We injected URLs acting as placeholders mapping directly to Kubernetes DNS names (like `r2dbc:postgresql://postgres-service:5432/user_account`).

4. **`k8s/postgres/postgres.yaml`**
   - Deploys PostgreSQL using a `StatefulSet` with an attached PersistentVolume for data persistence and sets up the primary user utilizing the Secret details from `db-secret`.

5. **`k8s/kafka/install_kafka.sh` and `values.yaml`**
   - Streamlined local cluster Kafka installation, exposing the service to internal microservices cleanly at `kafka:9092`.

6. **Changes in `application.yaml`:**
   Instead of deleting the previous code, we modified `application.yaml` for each microservice with pure comment blocks for Eureka/Config Server and set up fallback property injection (overridden completely by ConfigMaps during K8s startup):
   ```yaml
   #  config:
   #    import: configserver:http://localhost:9091/

   server:
     port: ${SERVER_PORT:8080}

   eureka:
     client:
       enabled: ${EUREKA_CLIENT_ENABLED:true}
   #    serviceUrl:
   #      defaultZone: http://localhost:9092/eureka/
   ```

   Gateway strictly routes path predicates (`/users/**`) directly to the Kubernetes `user-service`.
