3# Execution Service API Specification

This document defines the **API request and response specifications for the Execution Service**.

The Execution Service is responsible for:

* Starting workflow executions
* Tracking execution status
* Cancelling running workflows
* Retrying failed steps
* Retrieving execution logs

---

# Base URL

```
/executions
```

All APIs are typically accessed through the **Gateway Service**.

Example:

```
http://gateway-service/api/executions
```

---

# 1. Start Workflow Execution

Starts a new workflow execution instance.

## Endpoint

```
POST /workflows/{workflowId}/execute
```

## Path Parameters

| Parameter  | Type | Description                   |
| ---------- | ---- | ----------------------------- |
| workflowId | UUID | ID of the workflow to execute |

---

## Request Body

The request body contains the **input data required by the workflow**.

### Example Request

```json
{
  "amount": 250,
  "country": "US",
  "department": "Finance",
  "priority": "High"
}
```

---

## Response

### Success Response

Status Code:

```
201 CREATED
```

Response Body:

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "workflowId": "c8c9c2c3-0f2f-48a7-a1f7-5c3f02b1d7d9",
  "workflowVersion": 1,
  "status": "IN_PROGRESS",
  "currentStepId": "b3c2d0e0-9817-4f71-b04e-18f25db60a8b",
  "startedAt": "2026-02-18T10:00:00Z"
}
```

---

## Error Response

### Invalid Workflow

```
404 NOT FOUND
```

```json
{
  "error": "Workflow not found"
}
```

---

# 2. Get Execution Status

Retrieves the current state of a workflow execution.

## Endpoint

```
GET /executions/{executionId}
```

---

## Path Parameters

| Parameter   | Type | Description           |
| ----------- | ---- | --------------------- |
| executionId | UUID | Execution instance ID |

---

## Response

### Success Response

Status Code:

```
200 OK
```

Response Body:

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "workflowId": "c8c9c2c3-0f2f-48a7-a1f7-5c3f02b1d7d9",
  "workflowVersion": 1,
  "status": "IN_PROGRESS",
  "currentStepId": "b3c2d0e0-9817-4f71-b04e-18f25db60a8b",
  "retries": 0,
  "startedAt": "2026-02-18T10:00:00Z",
  "endedAt": null
}
```

---

# 3. Get Execution Logs

Retrieves logs for each step executed within a workflow.

## Endpoint

```
GET /executions/{executionId}/logs
```

---

## Path Parameters

| Parameter   | Type | Description           |
| ----------- | ---- | --------------------- |
| executionId | UUID | Execution instance ID |

---

## Response

### Success Response

Status Code:

```
200 OK
```

Response Body:

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "logs": [
    {
      "stepName": "Manager Approval",
      "stepType": "approval",
      "status": "completed",
      "evaluatedRules": [
        {
          "rule": "amount > 100 && country == 'US' && priority == 'High'",
          "result": true
        }
      ],
      "selectedNextStep": "Finance Notification",
      "startedAt": "2026-02-18T10:00:00Z",
      "endedAt": "2026-02-18T10:00:03Z"
    }
  ]
}
```

---

# 4. Cancel Execution

Cancels a running workflow execution.

## Endpoint

```
POST /executions/{executionId}/cancel
```

---

## Path Parameters

| Parameter   | Type | Description           |
| ----------- | ---- | --------------------- |
| executionId | UUID | Execution instance ID |

---

## Response

### Success Response

Status Code:

```
200 OK
```

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "status": "CANCELED",
  "message": "Workflow execution canceled successfully"
}
```

---

# 5. Retry Failed Step

Retries the failed step in a workflow execution.

⚠️ This does **not restart the entire workflow**.

---

## Endpoint

```
POST /executions/{executionId}/retry
```

---

## Path Parameters

| Parameter   | Type | Description           |
| ----------- | ---- | --------------------- |
| executionId | UUID | Execution instance ID |

---

## Response

### Success Response

Status Code:

```
200 OK
```

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "status": "IN_PROGRESS",
  "message": "Failed step retried successfully"
}
```

---

# Execution Status Values

| Status      | Description                       |
| ----------- | --------------------------------- |
| PENDING     | Execution created but not started |
| IN_PROGRESS | Workflow is currently executing   |
| COMPLETED   | Workflow finished successfully    |
| FAILED      | Workflow failed                   |
| CANCELED    | Workflow canceled by user         |

---

# Example Workflow Execution Flow

```
User starts workflow
        ↓
Execution Service creates execution instance
        ↓
Workflow Service provides workflow definition
        ↓
Rule Engine evaluates step rules
        ↓
Next step determined
        ↓
Step executed
        ↓
Logs stored
        ↓
Kafka event emitted
        ↓
Workflow continues until completion
```

---

# Kafka Events Emitted

Execution Service publishes the following events:

```
workflow-started
step-completed
workflow-completed
notification-trigger
```

Example Event:

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "workflowId": "c8c9c2c3-0f2f-48a7-a1f7-5c3f02b1d7d9",
  "step": "Manager Approval",
  "status": "completed"
}
```

---

# Summary

The Execution Service provides APIs for:

* Starting workflow execution
* Monitoring workflow status
* Viewing execution logs
* Cancelling workflows
* Retrying failed steps

These APIs enable the system to **execute dynamic workflows defined by the Workflow Service**.
