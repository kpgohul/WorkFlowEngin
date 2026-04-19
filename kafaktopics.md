# Kafka Topics and Consumer Groups Documentation

## Topics

### 1. user.registered
- Purpose:  
  Used to pass user registration information from the Auth Service to the User Service.

- Flow:  
  Auth Service → publishes → `user.registered` → User Service consumes

---

### 2. action.event
- Purpose:  
  Used to send action messages from the Execution Service to the Action Service.

- Flow:  
  Execution Service → publishes → `action.event` → Action Service consumes

---

### 3. action.result
- Purpose:  
  Used to send action results from the Action Service back to the Execution Service.

- Flow:  
  Action Service → publishes → `action.result` → Execution Service consumes

---

## Consumer Groups

### 1. user-service-group
- Consumes from: `user.registered`
- Description:  
  Handles user registration data and processes it within the User Service.

---

### 2. execution-service-group
- Consumes from: `action.result`
- Description:  
  Receives results of actions and continues execution flow.

---

### 3. action-service-group
- Consumes from: `action.event`
- Description:  
  Processes incoming action events and performs required operations.

---

## Summary Flow

1. User registers → Auth Service publishes to `user.registered`
2. User Service consumes and processes user data

3. Execution Service sends action → publishes to `action.event`
4. Action Service consumes and processes the action

5. Action Service sends result → publishes to `action.result`
6. Execution Service consumes result and continues processing