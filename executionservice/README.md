# Execution Service

Reactive execution engine for workflow runs.

## Implemented

- Workflow execution create/update/get/list APIs.
- First-step auto-initiation after execution creation.
- Step action endpoints for:
  - TASK
  - APPROVAL
  - DECISION
  - AUTO_APPROVAL
  - NOTIFICATION
  - DELAY
  - WEBHOOK
- Kafka command/result event publishing through `ActionService`.
- Kafka result listener to update step/execution status and trigger next step.

## Key Endpoints

- `POST /executions`
- `PUT /executions`
- `GET /executions/{id}`
- `GET /executions?page=1&size=10`
- `POST /executions/{workflowExecutionId}/steps/task`
- `POST /executions/{workflowExecutionId}/steps/approval`
- `POST /executions/{workflowExecutionId}/steps/decision`
- `POST /executions/{workflowExecutionId}/steps/auto-approval`
- `POST /executions/{workflowExecutionId}/steps/notification`
- `POST /executions/{workflowExecutionId}/steps/delay`
- `POST /executions/{workflowExecutionId}/steps/webhook`

## Local Run

```bash
cd executionservice
./mvnw test
./mvnw spring-boot:run
```

## Notes

- Workflow metadata is pulled from workflow service via `WebClient`.
- Kafka topic is configured by `kafka.execution.topic`.

