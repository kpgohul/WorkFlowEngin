# Workflow Service API Usage Guide

This document explains **how a user creates a workflow using the Workflow Service APIs**.

The workflow creation process happens in **three main steps**:

1. Create the **Workflow**
2. Add **Steps**
3. Add **Rules** to define the execution path

---

# 1. Create Workflow

First, create a workflow definition.

## API

```
POST /workflows
```

## Request Body

```json
{
  "name": "Expense Approval",
  "version": 1,
  "isActive": true,
  "inputSchema": {
    "amount": { "type": "number", "required": true },
    "country": { "type": "string", "required": true },
    "priority": { "type": "string", "required": true }
  }
}
```

## Example cURL

```bash
curl -X POST http://localhost:8081/workflows \
-H "Content-Type: application/json" \
-d '{
  "name":"Expense Approval",
  "version":1,
  "isActive":true,
  "inputSchema":"{\"amount\":{\"type\":\"number\"},\"country\":{\"type\":\"string\"}}"
}'
```

## Example Response

```json
{
  "id": "1c92c08e-1e0a-4f1f-a55c-b36e8b61d71b",
  "name": "Expense Approval",
  "version": 1,
  "isActive": true,
  "inputSchema": "...",
  "startStepId": null
}
```

Save the **workflow ID**, which will be used when creating steps.

---

# 2. Add Steps

Steps define the **actions in the workflow**.

## API

```
POST /workflows/{workflowId}/steps
```

---

## Example Step 1 — Manager Approval

### Request

```json
{
  "name": "Manager Approval",
  "stepType": "approval",
  "stepOrder": 1,
  "metadata": {
    "assignee": "manager@example.com"
  }
}
```

### Example Request

```
POST /workflows/1c92c08e-1e0a-4f1f-a55c-b36e8b61d71b/steps
```

### Example Response

```json
{
  "id": "step-101",
  "workflowId": "1c92c08e-1e0a-4f1f-a55c-b36e8b61d71b",
  "name": "Manager Approval",
  "stepType": "approval",
  "stepOrder": 1
}
```

---

## Example Step 2 — Finance Notification

```json
{
  "name": "Finance Notification",
  "stepType": "notification",
  "stepOrder": 2
}
```

---

## Example Step 3 — CEO Approval

```json
{
  "name": "CEO Approval",
  "stepType": "approval",
  "stepOrder": 3
}
```

---

## Example Step 4 — Task Rejection

```json
{
  "name": "Task Rejection",
  "stepType": "task",
  "stepOrder": 4
}
```

---

# 3. Add Rules

Rules determine **which step executes next**.

## API

```
POST /steps/{stepId}/rules
```

---

## Example Rules for Manager Approval Step

### Rule 1

```json
{
  "condition": "amount > 100 && country == 'US' && priority == 'High'",
  "nextStepId": "finance-step-id",
  "priority": 1
}
```

---

### Rule 2

```json
{
  "condition": "amount <= 100",
  "nextStepId": "ceo-step-id",
  "priority": 2
}
```

---

### Rule 3

```json
{
  "condition": "priority == 'Low' && country != 'US'",
  "nextStepId": "reject-step-id",
  "priority": 3
}
```

---

### Rule 4 — DEFAULT Rule

```json
{
  "condition": "DEFAULT",
  "nextStepId": "reject-step-id",
  "priority": 4
}
```

Every step should include a **DEFAULT rule** to ensure the workflow always continues.

---

# 4. Set the Start Step

After creating steps, update the workflow to define the **starting step**.

## API

```
PUT /workflows/{workflowId}
```

## Example Request

```json
{
  "startStepId": "manager-approval-step-id"
}
```

---

# 5. Final Workflow Structure

Example workflow:

```
Workflow: Expense Approval

Start Step
    ↓
Manager Approval
    ↓
Finance Notification
    ↓
CEO Approval
    ↓
Task Completion
```

Example rule flow:

```
Manager Approval
 ├─ amount > 100 && country == 'US' → Finance Notification
 ├─ amount <= 100 → CEO Approval
 ├─ priority == 'Low' && country != 'US' → Task Rejection
 └─ DEFAULT → Task Rejection
```

---

# 6. How the UI Uses These APIs

A frontend application (for example, a React workflow editor) will call APIs in the following order:

```
Create Workflow
        ↓
Add Steps
        ↓
Define Rules
        ↓
Save Workflow
```

The UI may visually represent the workflow like this:

```
Manager Approval
      ↓
Finance Notification
      ↓
CEO Approval
```

---

# 7. Complete Workflow Creation Flow

```
POST /workflows
        ↓
POST /workflows/{id}/steps
        ↓
POST /steps/{stepId}/rules
        ↓
Workflow Ready
```

After the workflow is defined, it can be executed by the execution service.

```
POST /workflows/{workflowId}/execute
```

---

# 8. Summary

The Workflow Service manages the **structure of workflows**, including:

* Workflow definitions
* Steps within workflows
* Rules that determine step transitions

The **Execution Service** is responsible for:

* Running workflows
* Evaluating rules
* Moving between steps
* Logging execution history
* Handling retries and failures
