# 8. Workflow Examples

## 8.1 Expense Approval Workflow

### Description
Used when an employee submits an expense claim.

### Steps
1. Manager Approval (approval)
2. Finance Notification (notification)
3. CEO Approval (approval)
4. Task Rejection (task)

### Example Input
```json
{
  "amount": 250,
  "country": "US",
  "department": "Finance",
  "priority": "High"
}
```

### Rules for Manager Approval

| Priority | Condition | Next Step |
|--------|----------|-----------|
| 1 | amount > 100 && country == 'US' && priority == 'High' | Finance Notification |
| 2 | amount <= 100 | CEO Approval |
| 3 | priority == 'Low' && country != 'US' | Task Rejection |
| 4 | DEFAULT | Task Rejection |

---

# 8.2 Leave Request Workflow

### Description
Used when employees apply for leave.

### Steps
1. Manager Approval (approval)
2. HR Notification (notification)
3. Leave Recorded (task)
4. Leave Rejected (task)

### Example Input
```json
{
  "days": 5,
  "employee_department": "Engineering"
}
```

### Rules

| Priority | Condition | Next Step |
|--------|----------|-----------|
| 1 | days > 3 | HR Notification |
| 2 | days <= 3 | Leave Recorded |
| 3 | DEFAULT | Leave Rejected |

---

# 8.3 Purchase Request Workflow

### Description
Used when employees request equipment or purchases.

### Steps
1. Manager Approval (approval)
2. Finance Approval (approval)
3. Procurement Task (task)
4. Request Rejected (task)

### Example Input
```json
{
  "amount": 1200,
  "item": "Laptop"
}
```

### Rules

| Priority | Condition | Next Step |
|--------|----------|-----------|
| 1 | amount > 500 | Finance Approval |
| 2 | amount <= 500 | Procurement Task |
| 3 | DEFAULT | Request Rejected |

---

# 8.4 Employee Onboarding Workflow

### Description
Used when a new employee joins the organization.

### Steps
1. HR Verification (approval)
2. IT Account Creation (task)
3. Laptop Allocation (task)
4. Manager Notification (notification)

### Example Input
```json
{
  "employee_role": "Software Engineer",
  "location": "India"
}
```

### Rules

| Priority | Condition | Next Step |
|--------|----------|-----------|
| 1 | employee_role contains "Engineer" | IT Account Creation |
| 2 | DEFAULT | Manager Notification |

---

# 8.5 Bug Release Approval Workflow

### Description
Used in software teams before deploying code.

### Steps
1. QA Approval (approval)
2. Engineering Approval (approval)
3. Deployment Task (task)
4. Release Notification (notification)

### Example Input
```json
{
  "severity": "High",
  "environment": "production"
}
```

### Rules

| Priority | Condition | Next Step |
|--------|----------|-----------|
| 1 | severity == "High" && environment == "production" | Engineering Approval |
| 2 | DEFAULT | Deployment Task |

---

# 9. How These Workflows Demonstrate Engine Capabilities

These workflows demonstrate the engine's ability to handle:

## Multiple Workflow Types

- Expense Approval
- Leave Request
- Purchase Request
- Employee Onboarding
- Bug Release Approval

## Different Step Types

- approval
- task
- notification

## Dynamic Rule Evaluation

Rules include:

- numeric comparisons
- string functions
- logical conditions
- DEFAULT fallbacks

## Different Execution Paths

Example:

```
Expense Approval
        ↓
Manager Approval
        ↓
Finance Notification OR CEO Approval
```

---

# 10. Demo Plan Using These Workflows

Your demo video should show:

### Step 1
Create Expense Approval Workflow

### Step 2
Add steps and rules

### Step 3
Start execution

### Step 4
Show rule evaluation

### Step 5
Show logs

### Step 6
Run another workflow (Leave Request)

This proves the engine works generically.