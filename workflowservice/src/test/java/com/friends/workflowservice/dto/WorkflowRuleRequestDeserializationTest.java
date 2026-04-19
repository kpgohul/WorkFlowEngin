package com.friends.workflowservice.dto;

import com.friends.workflowservice.dto.ruleconfig.DecisionRuleConfig;
import com.friends.workflowservice.dto.workflow.CreateWorkflowRequest;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class WorkflowRuleRequestDeserializationTest {

    private final JsonMapper objectMapper = new JsonMapper();

    @Test
    void shouldDeserializeNestedStepRuleWithExternalRuleType() throws JsonMappingException, JsonProcessingException {
        String payload = """
                {
                  "workflowTypeId": 1,
                  "name": "Leave Workflow with Decision",
                  "description": "Auto-route high leave days to HR review",
                  "status": "ACTIVE",
                  "version": 1,
                  "isActive": true,
                  "stepRule": [
                    {
                      "step": {
                        "stepCode": "CHECK_DAYS",
                        "name": "Check Leave Days",
                        "stepLine": 1,
                        "isLast": false,
                        "stepTimeoutInMillis": 0
                      },
                      "rule": {
                        "ruleType": "DECISION",
                        "ruleConfig": {
                          "name": "Route based on leaveDays",
                          "conditionExpression": {
                            "type": "RULE",
                            "field": "leaveDays",
                            "operator": "GT",
                            "value": 3
                          },
                          "onSuccessStepCode": "HR_REVIEW",
                          "onFailureStepCode": "MANAGER_REVIEW"
                        }
                      }
                    }
                  ]
                }
                """;

        CreateWorkflowRequest request = objectMapper.readValue(payload, CreateWorkflowRequest.class);

        assertEquals(1, request.getStepRule().size());
        assertInstanceOf(DecisionRuleConfig.class, request.getStepRule().get(0).getRule().getRuleConfig());
        DecisionRuleConfig config = (DecisionRuleConfig) request.getStepRule().get(0).getRule().getRuleConfig();
        assertEquals("HR_REVIEW", config.getOnSuccessStepCode());
        assertEquals("MANAGER_REVIEW", config.getOnFailureStepCode());
    }
}

