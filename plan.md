
# Halleyx Full Stack Engineer Challenge I – 2026
## Workflow Engine Design + Implementation Plan

---

# 1. Overview

Create a system that allows users to:

- Design workflows
- Define rules
- Execute processes
- Track every step

The system should support:

- Automation
- Notifications
- Approvals
- Dynamic decision-making based on input data

---

# 2. Core Concepts

## 2.1 Workflow

A **workflow** is a process composed of multiple steps executed sequentially or conditionally.

### Example Workflow

```
Expense Approval
    ↓
Manager Approval
    ↓
Finance Notification
    ↓
CEO Approval
    ↓
Task Completion
```

### Workflow Attributes

| Field | Type | Description |
|------|------|-------------|
| id | UUID | Unique identifier |
| name | string | Workflow name |
| version | integer | Workflow version |
| is_active | boolean | Whether this version is active |
| input_schema | JSON | Defines input data structure |
| start_step_id | UUID | First step to execute |
| created_at | timestamp | Creation timestamp |
| updated_at | timestamp | Update timestamp |

### Example Input Schema

```json
{
  "amount": { "type": "number", "required": true },
  "country": { "type": "string", "required": true },
  "department": { "type": "string", "required": false },
  "priority": {
    "type": "string",
    "required": true,
    "allowed_values": ["High", "Medium", "Low"]
  }
}
```

This schema defines what data is required when executing a workflow.

---

## 2.2 Step

A **step** is a single action within a workflow.

### Step Types

#### Task
Automated or manual action.

Examples:
- Update database
- Generate report

#### Approval
Requires user approval.

Example:
- Manager approves expense

#### Notification
Sends alerts/messages.

Examples:
- Email
- Slack
- UI message

### Step Attributes

| Field | Type | Description |
|------|------|-------------|
| id | UUID | Unique identifier |
| workflow_id | UUID | Associated workflow |
| name | string | Step name |
| step_type | enum | task, approval, notification |
| order | integer | Default step sequence |
| metadata | JSON | Additional information |
| created_at | timestamp | Creation time |
| updated_at | timestamp | Update time |

### Example Step

```json
{
  "id": "step-001",
  "workflow_id": "workflow-001",
  "name": "Manager Approval",
  "step_type": "approval",
  "order": 1,
  "metadata": {
    "assignee_email": "manager@example.com"
  }
}
```

---

## 2.3 Rule

Rules determine **which step executes next**.

Rules are evaluated in **priority order**.

Lower number = higher priority.

### Rule Attributes

| Field | Type | Description |
|------|------|-------------|
| id | UUID | Unique identifier |
| step_id | UUID | Step this rule belongs to |
| condition | string | Logical condition |
| next_step_id | UUID | Next step |
| priority | integer | Rule priority |
| created_at | timestamp | Creation time |
| updated_at | timestamp | Update time |

### Example Rule Condition

```
amount > 100 && country == "US"
```

### Supported Operators

#### Comparison

```
==
!=
<
>
<=
>=
```

#### Logical

```
&&   (AND)
||   (OR)
```

#### String Functions

```
contains(field, "value")
startsWith(field, "prefix")
endsWith(field, "suffix")
```

### Example Rules for Manager Approval

| Priority | Condition | Next Step |
|---------|----------|-----------|
| 1 | amount > 100 && country == 'US' && priority == 'High' | Finance Notification |
| 2 | amount <= 100 | CEO Approval |
| 3 | priority == 'Low' && country != 'US' | Task Rejection |
| 4 | DEFAULT | Task Rejection |

⚠️ **DEFAULT rule is required**

---

## 2.4 Execution

When a workflow runs, it creates an **execution instance**.

### Execution Attributes

| Field | Type | Description |
|------|------|-------------|
| id | UUID | Execution ID |
| workflow_id | UUID | Workflow reference |
| workflow_version | integer | Version executed |
| status | enum | pending, in_progress, completed, failed, canceled |
| data | JSON | Input values |
| logs | array | Step execution logs |
| current_step_id | UUID | Current executing step |
| retries | integer | Retry count |
| triggered_by | UUID | User who started execution |
| started_at | timestamp | Start time |
| ended_at | timestamp | End time |

### Example Execution Log

```json
{
  "step_name": "Manager Approval",
  "step_type": "approval",
  "evaluated_rules": [
    {
      "rule": "amount > 100 && country == 'US' && priority == 'High'",
      "result": true
    },
    {
      "rule": "amount <= 100 || department == 'HR'",
      "result": false
    }
  ],
  "selected_next_step": "Finance Notification",
  "status": "completed",
  "approver_id": "user-001",
  "error_message": null,
  "started_at": "2026-02-18T10:00:00Z",
  "ended_at": "2026-02-18T10:00:03Z"
}
```

---

# 3. Backend / API Requirements

## Workflows

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /workflows | Create workflow |
| GET | /workflows | List workflows |
| GET | /workflows/:id | Get workflow details |
| PUT | /workflows/:id | Update workflow |
| DELETE | /workflows/:id | Delete workflow |

## Steps

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /workflows/:workflow_id/steps | Add step |
| GET | /workflows/:workflow_id/steps | List steps |
| PUT | /steps/:id | Update step |
| DELETE | /steps/:id | Delete step |

## Rules

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /steps/:step_id/rules | Add rule |
| GET | /steps/:step_id/rules | List rules |
| PUT | /rules/:id | Update rule |
| DELETE | /rules/:id | Delete rule |

## Workflow Execution

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /workflows/:workflow_id/execute | Start execution |
| GET | /executions/:id | Get execution status |
| POST | /executions/:id/cancel | Cancel execution |
| POST | /executions/:id/retry | Retry failed step |

⚠️ Retry should **only re-execute the failed step**, not the whole workflow.

---

# 4. Rule Engine Behavior

The rule engine must:

- Evaluate rules dynamically at runtime
- Select first matching rule based on priority
- Continue to DEFAULT rule if none match
- Log every evaluation
- Handle rule errors

### Bonus

Support:

- Branching workflows
- Looping workflows
- Maximum iteration limits to prevent infinite loops

---

# 5. UI Requirements

## 5.1 Workflow List

Displays workflows.

Columns:

```
ID | Name | Steps | Version | Status | Actions
```

Features:

- Search
- Filter
- Pagination

Actions:

- Create
- Edit
- Execute

---

## 5.2 Workflow Editor

Allows users to configure:

- Workflow name
- Input schema
- Steps

Example:

```
Workflow: Expense Approval (Version 3)

Input Schema:
amount: number
country: string
department: string
priority: string

Steps:

Manager Approval
Finance Notification
CEO Approval
Task Rejection
```

---

## 5.3 Step Rule Editor

Allows rule configuration.

```
Priority | Condition | Next Step
```

Example:

```
1 amount > 100 && country == 'US' && priority == 'High' → Finance Notification
2 amount <= 100 || department == 'HR' → CEO Approval
3 priority == 'Low' && country != 'US' → Task Rejection
4 DEFAULT → Task Rejection
```

---

## 5.4 Workflow Execution UI

User provides input.

Example:

```
amount = 250
country = US
department = Finance
priority = High
```

Then:

```
Start Execution
```

Execution page shows:

- Current step
- Status
- Logs
- Approver details
- Execution duration

---

## 5.5 Audit Log

Displays workflow history.

| Execution ID | Workflow | Version | Status | Started By | Start Time | End Time |

Users can view logs for each execution.

---

# 6. Submission Requirements

Submit:

- Code
- Backend APIs
- Database models
- Frontend UI
- README

Include:

- Setup instructions
- Dependencies
- Workflow engine design

### Sample Workflows

Provide **1–2 example workflows**.

### Execution Example

Show:

- Input
- Rule evaluation
- Execution logs

### Demo Video

3–5 minutes showing:

- Workflow creation
- Rule definition
- Execution
- Logs

### Packaging

Provide:

- Git repository
- Runnable using README

---

# 7. Evaluation Matrix

| Criteria | Weight |
|--------|-------|
| Backend APIs | 20% |
| Rule Engine | 20% |
| Workflow Execution | 20% |
| Frontend UI | 15% |
| Demo Video | 10% |
| Code Quality | 5% |
| Documentation | 5% |
| Bonus Features | 5% |

---

# Implementation Plan (Our Architecture)

## Tech Stack

### Backend

- Spring Boot WebFlux
- Spring Security
- PostgreSQL
- Kafka
- Docker

### Frontend

- React
- TailwindCSS

### Infrastructure

- Docker
- Docker Compose
- Kafka
- PostgreSQL

---

# Microservice Architecture

Services:

```
gateway-service
auth-service
workflow-service
execution-service
notification-service
frontend
```

---

# Service Responsibilities

## Gateway Service

- Spring Cloud Gateway
- Authentication
- Routing

## Auth Service

- JWT authentication
- User management
- Role handling

## Workflow Service

Handles:

- workflow CRUD
- step CRUD
- rule CRUD

Tables:

```
workflow
step
rule
```

## Execution Service

Handles:

- start workflow
- execute steps
- evaluate rules
- move to next step
- log execution
- retry failed steps

Tables:

```
execution
execution_logs
```

## Notification Service

Handles:

- email alerts
- slack notifications
- ui notifications

Triggered via **Kafka events**.

---

# Event Driven Communication

Kafka topics:

```
workflow-started
step-completed
workflow-completed
notification-trigger
```

---

# Database

PostgreSQL

Schema:

```
workflow
step
rule
execution
execution_logs
```

---

# Containerization

Using **Docker Compose**.

Services:

```
postgres
kafka
zookeeper
workflow-service
execution-service
notification-service
gateway-service
frontend
```

---

# Development Phases

### Phase 1

- Workflow CRUD
- Step CRUD
- Rule CRUD
- Database design

### Phase 2

- Execution engine
- Rule engine
- Step processing
- Logging

### Phase 3

- Kafka integration
- Notifications
- Retry logic

### Phase 4

- React UI
- Workflow editor
- Execution monitor

---

# Deliverables

Final repository structure:

```
workflow-engine

gateway-service
auth-service
workflow-service
execution-service
notification-service
frontend

docker-compose.yml
README.md
```