# How to Add Start Step ID to WorkFlow

## Overview

When you create a workflow, it initially has no `startStepId`. After you've created all the steps for the workflow, you need to explicitly set which step should execute first.

---

## Complete Flow

### 1. Create Workflow Type (Optional - can reuse existing)

```bash
curl -X POST http://localhost:9091/workflow-types \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Expense Approval Type",
    "description": "Template for expense workflows",
    "fields": [
      {
        "fieldName": "amount",
        "fieldType": "NUMBER",
        "required": true
      }
    ]
  }'
```

**Response:**
```json
{
  "id": "db1d4296-...",
  "name": "Expense Approval Type",
  ...
}
```

**Save:** `WORKFLOW_TYPE_ID = "db1d4296-..."`

---

### 2. Create Workflow (startStepId will be null initially)

```bash
curl -X POST http://localhost:9091/workflows \
  -H "Content-Type: application/json" \
  -d '{
    "workFlowTypeId": "db1d4296-...",
    "name": "Expense Approval - India",
    "isActive": true
  }'
```

**Response:**
```json
{
  "id": "7ea06be8-...",
  "workFlowTypeId": "db1d4296-...",
  "name": "Expense Approval - India",
  "version": 1,
  "isActive": true,
  "startStepId": null
}
```

**Save:** `WORKFLOW_ID = "7ea06be8-..."`

---

### 3. Create Steps

```bash
# Create Step 1
curl -X POST http://localhost:9091/workflows/7ea06be8-.../steps \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Manager Approval",
    "stepType": "APPROVAL",
    "stepOrder": 1,
    "metadata": "{\"assignee\":\"manager@company.com\"}"
  }'
```

**Response:**
```json
{
  "id": "step-001-...",
  "workflowId": "7ea06be8-...",
  "name": "Manager Approval",
  "stepType": "APPROVAL",
  "stepOrder": 1
}
```

**Save:** `STEP_1_ID = "step-001-..."`

```bash
# Create Step 2
curl -X POST http://localhost:9091/workflows/7ea06be8-.../steps \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Finance Approval",
    "stepType": "APPROVAL",
    "stepOrder": 2,
    "metadata": "{\"assignee\":\"finance@company.com\"}"
  }'
```

**Save:** `STEP_2_ID = "step-002-..."`

---

### 4. **SET START STEP ID** ← This is the key step!

Use `PUT /workflows/{workflowId}` with the `startStepId`:

```bash
curl -X PUT http://localhost:9091/workflows/7ea06be8-... \
  -H "Content-Type: application/json" \
  -d '{
    "startStepId": "step-001-..."
  }'
```

**Response:**
```json
{
  "id": "7ea06be8-...",
  "workFlowTypeId": "db1d4296-...",
  "name": "Expense Approval - India",
  "version": 1,
  "isActive": true,
  "startStepId": "step-001-..."
}
```

✅ **Now `startStepId` is set!**

---

### 5. Add Rules (Optional)

```bash
curl -X POST http://localhost:9091/steps/step-001-.../rules \
  -H "Content-Type: application/json" \
  -d '{
    "condition": "amount > 1000",
    "nextStepId": "step-002-...",
    "priority": 1
  }'
```

---

## Implementation Details

### What happens when you PUT /workflows/{id}

```java
// WorkFlowServiceImpl.updateStartStep()
public Mono<WorkFlowResponse> updateStartStep(UUID id, UpdateWorkFlowRequest request) {
    return workFlowRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException(...)))
            .flatMap(workflow -> 
                stepRepository.findById(request.getStartStepId())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException(...)))
                    .flatMap(step -> {
                        workflow.setStartStepId(request.getStartStepId());
                        return workFlowRepository.save(workflow);
                    })
            )
            .map(WorkFlowMapper::toResponse);
}
```

**Flow:**
1. Find workflow by ID (throws 404 if not found)
2. Find step by ID (throws 404 if not found)
3. Set workflow's `startStepId` field
4. Save workflow to database
5. Return updated response

---

## Files Changed

- ✅ `WorkFlowServiceImpl.java` - Added `updateStartStep()` method
- ✅ `WorkFlowService.java` - Added interface method
- ✅ `WorkFlowController.java` - Added `PUT /workflows/{id}` endpoint
- ✅ `UpdateWorkFlowRequest.java` - New DTO
- ✅ `WorkFlowResponse.java` - Added `startStepId` field
- ✅ `workflow-api-spec.md` - Documented the endpoint

---

## Database

The `workflow_type` table already has:
```sql
CREATE TABLE IF NOT EXISTS workflow (
    id UUID PRIMARY KEY,
    workflow_type_id UUID,
    name VARCHAR(255) NOT NULL,
    version INT,
    is_active VARCHAR(50),
    start_step_id UUID,              ← This column is used!
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

No migration needed—the column exists!

---

## Validation

When you call `PUT /workflows/{id}`:

✅ Workflow exists → 404 if not  
✅ Step exists → 404 if not  
✅ startStepId is not null → 400 BAD_REQUEST if null  

---

## Example Complete Workflow Lifecycle

```
1. POST /workflow-types → get WORKFLOW_TYPE_ID
2. POST /workflows → get WORKFLOW_ID (startStepId = null)
3. POST /workflows/{id}/steps → get STEP_IDS
4. PUT /workflows/{id} → set startStepId ← YOU ARE HERE
5. POST /steps/{id}/rules → add rules
6. POST /workflows/{id}/execute → execution service starts from startStepId
```

---

## Quick Summary

**To add `startStepId` to a WorkFlow:**

```bash
PUT /workflows/{workflowId}
{
  "startStepId": "<UUID of starting step>"
}
```

That's it! The workflow is now configured and ready for execution.

