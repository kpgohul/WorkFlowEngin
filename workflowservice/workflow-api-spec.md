# Workflow Service API Usage Guide

This document describes the current APIs in `workflowservice` for:

1. Creating reusable workflow types
2. Creating workflows from those types
3. Adding steps and rules

---

## 1) Create Workflow Type (Reusable Template)

Use workflow types so users can define their own custom workflow input schema once, then reuse it across many workflows.

### API

```http
POST /workflow-types
```

### Request Body

- `name` (string, required)
- `description` (string, optional)
- `fields` (array, required)

Each field:

- `fieldName` (string, required)
- `fieldType` (enum, required): `STRING | NUMBER | BOOLEAN | ENUM | DATE | CUSTOM`
- `required` (boolean, required)
- `allowedValues` (string array, required only for `ENUM`)
- `customFieldType` (string, required only for `CUSTOM`)

### Example Request

```json
{
  "name": "Expense Approval Type",
  "description": "Input schema for expense approvals",
  "fields": [
    {
      "fieldName": "amount",
      "fieldType": "NUMBER",
      "required": true
    },
    {
      "fieldName": "priority",
      "fieldType": "ENUM",
      "required": true,
      "allowedValues": ["LOW", "MEDIUM", "HIGH"]
    },
    {
      "fieldName": "departmentCode",
      "fieldType": "CUSTOM",
      "customFieldType": "ORG_DEPT_CODE",
      "required": false
    }
  ]
}
```

### Example Response

```json
{
  "id": "db1d4296-9e57-43ec-9f7e-f4f29a300f0d",
  "name": "Expense Approval Type",
  "description": "Input schema for expense approvals",
  "fields": [
    {
      "fieldName": "amount",
      "fieldType": "NUMBER",
      "required": true,
      "allowedValues": null,
      "customFieldType": null
    }
  ]
}
```

### Notes

- Duplicate `fieldName` values are rejected.
- `ENUM` without `allowedValues` is rejected.
- `CUSTOM` without `customFieldType` is rejected.

---

## 2) Read Workflow Types

### APIs

```http
GET /workflow-types
GET /workflow-types/{id}
GET /workflow-types/{id}/schema
```

Use `/schema` when UI needs only the field definition list.

---

## 3) Create Workflow From Workflow Type

After creating a workflow type, create a workflow that references it.

### API

```http
POST /workflows
```

### Request Body

- `workFlowTypeId` (UUID, required)
- `name` (string, required)
- `isActive` (boolean, required)

### Example Request

```json
{
  "workFlowTypeId": "db1d4296-9e57-43ec-9f7e-f4f29a300f0d",
  "name": "Expense Approval - India",
  "isActive": true
}
```

### Example Response

```json
{
  "id": "7ea06be8-a355-4892-a8f9-b351ed40d3d4",
  "workFlowTypeId": "db1d4296-9e57-43ec-9f7e-f4f29a300f0d",
  "name": "Expense Approval - India",
  "version": 1,
  "isActive": true,
  "startStepId": null
}
```

### Additional Read APIs

```http
GET /workflows
GET /workflows/{id}
GET /workflows/{id}/details
```

`GET /workflows/{id}/details` returns workflow type, workflow, steps (ordered by `stepOrder`), and each step's rules (ordered by `priority`).

---

## 3.5) Set Start Step for Workflow

After creating steps, set which step should execute first.

### API

```http
PUT /workflows/{workflowId}
```

### Request Body

- `startStepId` (UUID, required)

### Example Request

```json
{
  "startStepId": "f7fa543f-b8d1-4c35-bf1a-0f2ec2d8ef4d"
}
```

### Example Response

```json
{
  "id": "7ea06be8-a355-4892-a8f9-b351ed40d3d4",
  "workFlowTypeId": "db1d4296-9e57-43ec-9f7e-f4f29a300f0d",
  "name": "Expense Approval - India",
  "version": 1,
  "isActive": true,
  "startStepId": "f7fa543f-b8d1-4c35-bf1a-0f2ec2d8ef4d"
}
```

### Validation

- Workflow with given ID must exist
- Step with given ID must exist and belong to the workflow
- If step doesn't exist, error `404 NOT_FOUND` is returned

---

## 4) Add Steps to Workflow

### APIs

```http
POST /workflows/{workflowId}/steps
GET /workflows/{workflowId}/steps
```

### Example Step Create Request (with optional nested rules)

```json
{
  "name": "Manager Approval",
  "stepType": "APPROVAL",
  "stepOrder": 1,
  "metadata": "{\"assignee\":\"manager@company.com\"}",
  "rules": [
    {
      "condition": "amount > 1000 && priority == 'HIGH'",
      "nextStepId": "f7fa543f-b8d1-4c35-bf1a-0f2ec2d8ef4d",
      "priority": 1
    }
  ]
}
```

### Notes

- `rules` is optional while creating a step.
- If `rules` is provided, each `nextStepId` must belong to the same workflow.

---

## 5) Add Rules to Step

### APIs

```http
POST /steps/{stepId}/rules
GET /steps/{stepId}/rules
```

### Example Rule Create Request

```json
{
  "condition": "amount > 1000 && priority == 'HIGH'",
  "nextStepId": "f7fa543f-b8d1-4c35-bf1a-0f2ec2d8ef4d",
  "priority": 1
}
```

### Notes

- `stepId` comes from path, not request body.
- `nextStepId` must belong to the same workflow as the source step.

---

## 6) Error Response Shape

All handled errors follow this structure:

```json
{
  "apiPath": "/workflow-types",
  "errorCode": "BAD_REQUEST",
  "errorMessage": "allowedValues are required when fieldType is ENUM",
  "errorTime": "2026-03-17T20:00:00"
}
```

Common status codes:

- `400 BAD_REQUEST` validation/argument issues
- `404 NOT_FOUND` missing resource
- `409 CONFLICT` duplicate resource
- `500 INTERNAL_SERVER_ERROR` unhandled server error

---

## 7) Typical UI Sequence

```text
POST /workflow-types
        -> get workflowTypeId
POST /workflows
        -> get workflowId
POST /workflows/{workflowId}/steps
        -> optionally include rules in the same request
POST /steps/{stepId}/rules
        -> add more rules later if needed
GET /workflows/{workflowId}/details
        -> fetch workflow type + workflow + all steps + all rules
```

---

## 8) Quick cURL Samples

```bash
curl -X POST http://localhost:9091/workflow-types \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Leave Request Type",
    "description":"Leave workflow input",
    "fields":[
      {"fieldName":"days","fieldType":"NUMBER","required":true},
      {"fieldName":"reason","fieldType":"STRING","required":false}
    ]
  }'

curl -X POST http://localhost:9091/workflows \
  -H "Content-Type: application/json" \
  -d '{
    "workFlowTypeId":"<WORKFLOW_TYPE_ID>",
    "name":"Leave Request - Default",
    "isActive":true
  }'
```
