# Notification Service API Specification

This document defines the **API endpoints for the Notification Service**.

The Notification Service is responsible for:

* Receiving workflow events from Kafka
* Sending notifications (Email / Slack / UI)
* Storing notification logs
* Providing APIs to retrieve notification history

---

# Base URL

```text
/notifications
```

Example through Gateway:

```text
http://gateway-service/api/notifications
```

---

# 1. Get All Notifications

Retrieves all notification logs stored in the system.

## Endpoint

```http
GET /notifications
```

---

## Query Parameters (Optional)

| Parameter   | Type    | Description                          |
| ----------- | ------- | ------------------------------------ |
| page        | integer | Page number for pagination           |
| size        | integer | Number of records per page           |
| executionId | UUID    | Filter notifications by execution ID |

---

## Example Request

```http
GET /notifications?page=0&size=10
```

---

## Success Response

Status Code

```text
200 OK
```

Response Body

```json
{
  "notifications": [
    {
      "id": "c98a7b7e-9a3c-4b0a-a0de-abc123456789",
      "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
      "eventType": "STEP_COMPLETED",
      "recipient": "manager@example.com",
      "message": "Manager Approval step completed successfully",
      "status": "SENT",
      "createdAt": "2026-02-18T10:00:03Z"
    }
  ]
}
```

---

# 2. Get Notifications by Execution ID

Retrieves all notifications related to a specific workflow execution.

## Endpoint

```http
GET /notifications/execution/{executionId}
```

---

## Path Parameters

| Parameter   | Type | Description           |
| ----------- | ---- | --------------------- |
| executionId | UUID | Workflow execution ID |

---

## Example Request

```http
GET /notifications/execution/4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2
```

---

## Success Response

Status Code

```text
200 OK
```

Response Body

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "notifications": [
    {
      "id": "c98a7b7e-9a3c-4b0a-a0de-abc123456789",
      "eventType": "STEP_COMPLETED",
      "recipient": "manager@example.com",
      "message": "Manager Approval step completed successfully",
      "status": "SENT",
      "createdAt": "2026-02-18T10:00:03Z"
    }
  ]
}
```

---

# 3. Get Notification by ID

Retrieves a single notification record.

## Endpoint

```http
GET /notifications/{notificationId}
```

---

## Path Parameters

| Parameter      | Type | Description     |
| -------------- | ---- | --------------- |
| notificationId | UUID | Notification ID |

---

## Example Request

```http
GET /notifications/c98a7b7e-9a3c-4b0a-a0de-abc123456789
```

---

## Success Response

Status Code

```text
200 OK
```

Response Body

```json
{
  "id": "c98a7b7e-9a3c-4b0a-a0de-abc123456789",
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "eventType": "STEP_COMPLETED",
  "recipient": "manager@example.com",
  "message": "Manager Approval step completed successfully",
  "status": "SENT",
  "createdAt": "2026-02-18T10:00:03Z"
}
```

---

# 4. Trigger Notification Manually (Optional)

Allows manual triggering of a notification.

## Endpoint

```http
POST /notifications/send
```

---

## Request Body

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "recipient": "manager@example.com",
  "message": "Manual notification triggered",
  "type": "EMAIL"
}
```

---

## Success Response

Status Code

```text
200 OK
```

Response Body

```json
{
  "status": "SENT",
  "message": "Notification delivered successfully"
}
```

---

# Notification Types

| Type  | Description                                   |
| ----- | --------------------------------------------- |
| EMAIL | Sends an email notification                   |
| SLACK | Sends a Slack message                         |
| UI    | Displays a notification in the user interface |

---

# Notification Status Values

| Status  | Description                          |
| ------- | ------------------------------------ |
| SENT    | Notification successfully delivered  |
| FAILED  | Notification delivery failed         |
| PENDING | Notification queued but not yet sent |

---

# Kafka Events Consumed

The Notification Service listens to the following Kafka topics:

```text
notification-trigger
workflow-started
step-completed
workflow-completed
```

---

# Example Kafka Event

```json
{
  "executionId": "4c0a0d18-3e1a-4f48-9f8a-51fd0fd6c8b2",
  "workflowId": "c8c9c2c3-0f2f-48a7-a1f7-5c3f02b1d7d9",
  "step": "Manager Approval",
  "status": "completed"
}
```

The Notification Service processes this event and sends notifications accordingly.

---

# Example Notification Flow

```text
Execution Service
        │
        │ Kafka Event
        ▼
notification-trigger topic
        │
        ▼
Notification Service
        │
        ├── Send Email
        ├── Send Slack
        └── Store Notification Log
```

---

# Summary

The Notification Service APIs allow clients to:

* Retrieve notification logs
* Filter notifications by execution
* Fetch individual notification details
* Trigger notifications manually

These APIs support monitoring and debugging of workflow-related notifications.
