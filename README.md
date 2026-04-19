# FlowForge Application Overview

FlowForge is a robust, microservices-based Workflow Engine designed to automate and manage complex business processes. It leverages Spring Boot, Spring Cloud, Kafka, and Kubernetes to provide a scalable and fault-tolerant environment.

## 🏗 Architecture Overview

The system is composed of several specialized microservices communicating via REST APIs and Kafka events.

### Core Services

| Service | Internal Port | Description |
| :--- | :--- | :--- |
| **Config Server** | 9091 | Centralized configuration management using Spring Cloud Config. |
| **Eureka Server** | 9092 | Service discovery (used for local development; K8s DNS used in production). |
| **Gateway Server** | 9093 | API Gateway for routing and cross-cutting concerns (External NodePort: 30080). |
| **Auth Server** | 9099 | OAuth2/OIDC identity provider. |
| **User Service** | 9097 | Manages users, teams, and role assignments. |
| **Workflow Service** | 9094 | Defines and manages workflow templates. |
| **Execution Service**| 9095 | Orchestrates the execution of workflows. |
| **Action Service**   | 9096 | Executes atomic actions like approvals, notifications, and webhooks. |

---

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Configuration**: Spring Cloud Config
- **Service Discovery**: Eureka / Kubernetes DNS
- **Messaging**: Apache Kafka
- **Database**: PostgreSQL (Multi-database setup: `auth_db`, `user_db`, `workflow_db`, `execution_db`, `action_db`)
- **Security**: Spring Security OAuth2 / OIDC
- **Deployment**: Docker, Kubernetes (Minikube)

---

## 📊 Messaging & Events (Kafka)

FlowForge uses asynchronous messaging for workflow orchestration and user registration.

- **`user.registered`**: Triggered when a new user signs up; consumed by `userservice`.
- **`action.event`**: Emitted by `executionservice` to trigger actions in `actionservice`.
- **`action.result`**: Emitted by `actionservice` back to `executionservice` with the result of an operation.

---

## 🚀 Key Flows

### 1. Unified Configuration Management
The project utilizes a tiered configuration strategy:
1. **Kubernetes ConfigMap (`app-config`)**: Stores infrastructure-level settings like service URLs (e.g., `AUTH_SERVICE_URL`, `USER_SERVICE_URL`) and Kafka topics.
2. **Kubernetes Secrets (`app-secrets`)**: Stores sensitive data like DB credentials (`DB_USER`, `DB_PASSWORD`) and Mail settings.
3. **Internal Isolation**: Only the **Config Server** has full access to the ConfigMap and Secrets. Other microservices are restricted and only receive the `CONFIG_BASE_URL`. They fetch their complete functional configuration from the Config Server during the bootstrap phase.

### 2. Workflow Action Execution
1. `executionservice` determines that the next step in a process is an action.
2. It sends a payload to the `action.event` topic.
3. `actionservice` consumes the event, persists the `ExecutionAction`, and performs the requested operation (e.g., sending an approval email via `MailSenderUtil`).
4. On completion or failure, `actionservice` sends an `ActionResponse` back via the `action.result` topic.

### 3. Team-Based Approvals
The `userservice` provides a specialized endpoint `/api/v1/teams/users` that allows the `actionservice` to fetch all users belonging to a specific team or role. This enables dynamic assignment of approvers based on organizational structure.

---

## ☸️ Kubernetes Deployment

The deployment manifests are located in the `k8s/` directory.

### Initialization Order:
1. **Infrastructure**: PostgreSQL (`postgres/`) and Kafka (`kafka/`).
2. **Config**: `app-config` ConfigMap and `app-secrets` Secret.
3. **Central Services**: `config-service` (must be healthy first) and `eureka-service`.
4. **App Services**: `auth-service`, `user-service`, `workflow-service`, `execution-service`, `action-service`.
5. **Gateway**: `gateway-service` (Entry point).
