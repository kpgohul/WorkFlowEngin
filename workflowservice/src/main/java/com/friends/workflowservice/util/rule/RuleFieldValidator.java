package com.friends.workflowservice.util.rule;

import com.friends.workflowservice.appconstant.Operator;
import com.friends.workflowservice.appconstant.WorkflowFieldType;
import com.friends.workflowservice.entity.WorkflowTypeField;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Validates RuleExpression fields against WorkflowTypeField definitions.
 * Ensures field existence, data type compatibility, operator support, and value constraints.
 */
public class RuleFieldValidator {

    // Map of operators supported by each field type
    private static final Map<WorkflowFieldType, Set<Operator>> OPERATOR_BY_TYPE = Map.of(
            WorkflowFieldType.STRING, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.IN, Operator.NOT_IN,
                    Operator.CONTAINS, Operator.STARTS_WITH, Operator.ENDS_WITH,
                    Operator.IS_NULL, Operator.IS_NOT_NULL
            ),
            WorkflowFieldType.NUMBER, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.GT, Operator.GTE,
                    Operator.LT, Operator.LTE, Operator.IN, Operator.NOT_IN,
                    Operator.IS_NULL, Operator.IS_NOT_NULL
            ),
            WorkflowFieldType.BOOLEAN, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.IS_NULL, Operator.IS_NOT_NULL
            ),
            WorkflowFieldType.DATE, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.GT, Operator.GTE,
                    Operator.LT, Operator.LTE, Operator.IN, Operator.NOT_IN,
                    Operator.IS_NULL, Operator.IS_NOT_NULL
            ),
            WorkflowFieldType.DATETIME, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.GT, Operator.GTE,
                    Operator.LT, Operator.LTE, Operator.IN, Operator.NOT_IN,
                    Operator.IS_NULL, Operator.IS_NOT_NULL
            ),
            WorkflowFieldType.JSON, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.IS_NULL, Operator.IS_NOT_NULL
            ),
            WorkflowFieldType.ENUM, EnumSet.of(
                    Operator.EQ, Operator.NE, Operator.IN, Operator.NOT_IN,
                    Operator.IS_NULL, Operator.IS_NOT_NULL
            )
    );

    /**
     * Validates that the field exists in the workflow type fields.
     *
     * @param fieldKey the field key to validate
     * @param fields the list of available workflow type fields
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if field does not exist
     */
    public static void validateFieldExists(String fieldKey, List<WorkflowTypeField> fields, String path) {
        boolean fieldExists = fields.stream()
                .anyMatch(field -> field.getFieldKey().equals(fieldKey));

        if (!fieldExists) {
            throw new IllegalArgumentException(
                    "Field '" + fieldKey + "' not found in workflow type fields at " + path);
        }
    }

    /**
     * Validates that the operator is supported for the given field type.
     *
     * @param operator the operator to validate
     * @param fieldType the field type
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if operator is not supported for the field type
     */
    public static void validateOperatorSupport(Operator operator, WorkflowFieldType fieldType, String path) {
        Set<Operator> supportedOperators = OPERATOR_BY_TYPE.get(fieldType);

        if (supportedOperators == null || !supportedOperators.contains(operator)) {
            throw new IllegalArgumentException(
                    "Operator '" + operator + "' is not supported for field type '" + fieldType + "' at " + path);
        }
    }

    /**
     * Validates that the value matches the expected data type.
     *
     * @param value the value to validate
     * @param fieldType the expected field type
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if value type doesn't match the field type
     */
    public static void validateDataTypeMatch(Object value, WorkflowFieldType fieldType, String path) {
        if (value == null) {
            return; // null is handled separately by IS_NULL/IS_NOT_NULL operators
        }

        switch (fieldType) {
            case STRING:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            "Value must be of type String for STRING field at " + path);
                }
                break;
            case NUMBER:
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException(
                            "Value must be of type Number for NUMBER field at " + path);
                }
                break;
            case BOOLEAN:
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException(
                            "Value must be of type Boolean for BOOLEAN field at " + path);
                }
                break;
            case DATE:
                if (!(value instanceof String || value instanceof LocalDate)) {
                    throw new IllegalArgumentException(
                            "Value must be a date string or LocalDate for DATE field at " + path);
                }
                break;
            case DATETIME:
                if (!(value instanceof String || value instanceof LocalDateTime)) {
                    throw new IllegalArgumentException(
                            "Value must be a datetime string or LocalDateTime for DATETIME field at " + path);
                }
                break;
            case JSON:
                if (!(value instanceof String || value instanceof Map || value instanceof List)) {
                    throw new IllegalArgumentException(
                            "Value must be a JSON string, Map, or List for JSON field at " + path);
                }
                break;
            case ENUM:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            "Value must be of type String for ENUM field at " + path);
                }
                break;
        }
    }

    /**
     * Validates that the value matches the regex pattern if defined.
     *
     * @param value the value to validate
     * @param validationRegex the regex pattern
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if value doesn't match the regex pattern
     */
    public static void validateRegexPattern(Object value, String validationRegex, String path) {
        if (value == null || validationRegex == null || validationRegex.isBlank()) {
            return;
        }

        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException(
                    "Regex validation requires String value at " + path);
        }

        try {
            Pattern pattern = Pattern.compile(validationRegex);
            if (!pattern.matcher(stringValue).matches()) {
                throw new IllegalArgumentException(
                        "Value does not match the validation pattern at " + path);
            }
        } catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException(
                    "Invalid regex pattern defined for field at " + path + ": " + ex.getMessage());
        }
    }

    /**
     * Validates that the value is in the list of allowed ENUM values.
     *
     * @param value the value to validate
     * @param allowedValues the list of allowed values
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if value is not in the allowed values list
     */
    public static void validateEnumValues(Object value, List<String> allowedValues, String path) {
        if (value == null || allowedValues == null || allowedValues.isEmpty()) {
            return;
        }

        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException(
                    "ENUM value must be a String at " + path);
        }

        if (!allowedValues.contains(stringValue)) {
            throw new IllegalArgumentException(
                    "Value '" + stringValue + "' is not in allowed values " + allowedValues + " at " + path);
        }
    }

    /**
     * Validates IN/NOT_IN operator values are a list of correct data type.
     *
     * @param value the value to validate (should be a List)
     * @param fieldType the field type
     * @param operator the operator (IN or NOT_IN)
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if value is not a list or contains incompatible types
     */
    public static void validateInOperatorTypes(Object value, WorkflowFieldType fieldType, Operator operator, String path) {
        if (operator != Operator.IN && operator != Operator.NOT_IN) {
            return;
        }

        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException(
                    "Value must be a List for " + operator + " operator at " + path);
        }

        if (list.isEmpty()) {
            throw new IllegalArgumentException(
                    "Value list cannot be empty for " + operator + " operator at " + path);
        }

        // Validate each element in the list
        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            if (element != null) {
                try {
                    validateDataTypeMatch(element, fieldType, path + "[" + i + "]");
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException(
                            "Element at index " + i + " in " + operator + " list has invalid type: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Validates string-specific operators (CONTAINS, STARTS_WITH, ENDS_WITH) have string values.
     *
     * @param value the value to validate
     * @param operator the operator
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if value is not a String for string operators
     */
    public static void validateStringOperatorValue(Object value, Operator operator, String path) {
        if (operator == Operator.CONTAINS || operator == Operator.STARTS_WITH || operator == Operator.ENDS_WITH) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        "Operator '" + operator + "' requires String value at " + path);
            }
        }
    }

    /**
     * Gets the field metadata by field key from the list of workflow type fields.
     *
     * @param fieldKey the field key to find
     * @param fields the list of workflow type fields
     * @return the WorkflowTypeField if found
     * @throws IllegalArgumentException if field is not found
     */
    public static WorkflowTypeField getFieldMetadata(String fieldKey, List<WorkflowTypeField> fields) {
        return fields.stream()
                .filter(field -> field.getFieldKey().equals(fieldKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Field metadata for '" + fieldKey + "' not found"));
    }

    /**
     * Performs complete validation of a field value against its type definition.
     *
     * @param fieldKey the field key
     * @param value the value to validate
     * @param operator the operator
     * @param fields the list of available workflow type fields
     * @param path the JSON path for error messages
     * @throws IllegalArgumentException if any validation fails
     */
    public static void validateFieldValue(String fieldKey, Object value, Operator operator,
                                         List<WorkflowTypeField> fields, String path) {
        // Validate field exists
        validateFieldExists(fieldKey, fields, path);

        // Get field metadata
        WorkflowTypeField fieldMetadata = getFieldMetadata(fieldKey, fields);
        WorkflowFieldType fieldType = fieldMetadata.getFieldType();

        // Validate operator is supported for this field type
        validateOperatorSupport(operator, fieldType, path);

        // Handle null operators separately
        if (operator == Operator.IS_NULL || operator == Operator.IS_NOT_NULL) {
            return;
        }

        // Validate data type match
        if (operator == Operator.IN || operator == Operator.NOT_IN) {
            validateInOperatorTypes(value, fieldType, operator, path);
        } else {
            validateDataTypeMatch(value, fieldType, path);
        }

        // Validate string-specific operators
        validateStringOperatorValue(value, operator, path);

        // Validate regex pattern if defined
        if (fieldMetadata.getValidationRegex() != null && value instanceof String) {
            validateRegexPattern(value, fieldMetadata.getValidationRegex(), path);
        }

        // Validate ENUM values if field type is ENUM
        if (fieldType == WorkflowFieldType.ENUM && operator != Operator.IN && operator != Operator.NOT_IN) {
            validateEnumValues(value, fieldMetadata.getAllowedValues(), path);
        }
    }
}

