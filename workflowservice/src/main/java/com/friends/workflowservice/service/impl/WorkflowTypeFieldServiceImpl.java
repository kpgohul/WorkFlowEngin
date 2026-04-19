package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.appconstant.WorkflowFieldType;
import com.friends.workflowservice.dto.workflowtype.*;
import com.friends.workflowservice.entity.WorkflowTypeField;
import com.friends.workflowservice.exception.ResourceNotFoundException;
import com.friends.workflowservice.mapper.WorkflowTypeFieldMapper;
import com.friends.workflowservice.repo.WorkflowTypeFieldRepository;
import com.friends.workflowservice.service.WorkflowTypeFieldService;
import com.friends.workflowservice.util.common.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowTypeFieldServiceImpl implements WorkflowTypeFieldService {

    private final WorkflowTypeFieldRepository fieldRepository;

    @Override
    public Mono<List<WorkflowTypeFieldResponse>> createWorkflowTypeFields(Long workflowTypeId, Mono<List<WorkflowTypeFieldRequest>> requests) {
        return requests
                .map(req -> {
                    if (req == null || req.isEmpty()) {
                        throw new ResourceNotFoundException("WorkFlowType", "Fields", "Empty");
                    }

                    return req.stream()
                            .map(r -> WorkflowTypeFieldMapper.toEntity(r, workflowTypeId))
                            .collect(Collectors.toList());
                })
                .doOnNext(this::validateWorkflowTypeFieldRequest)
                .flatMapMany(fieldRepository::saveAll)
                .map(WorkflowTypeFieldMapper::toResponse)
                .collectList();
    }


    @Override
    public Mono<List<WorkflowTypeFieldResponse>> updateWorkflowTypeFields(Long workflowTypeId, Mono<List<WorkflowTypeFieldRequest>> requests) {
        return requests
                .map(req -> req.stream()
                        .map(r -> WorkflowTypeFieldMapper.toEntity(r, workflowTypeId))
                        .collect(Collectors.toList())
                )
                .doOnNext(this::validateWorkflowTypeFieldRequest)
                .flatMap(fields -> fieldRepository.deleteAllByWorkflowTypeId(workflowTypeId)
                        .thenMany(fieldRepository.saveAll(fields))
                        .map(WorkflowTypeFieldMapper::toResponse)
                        .collectList()
                );
    }

    @Override
    public Mono<List<WorkflowTypeFieldResponse>> getWorkflowTypeFieldById(Long id) {
        return fieldRepository.findAllByWorkflowTypeId(id)
                .map(WorkflowTypeFieldMapper::toResponse)
                .collectList();
    }

    @Override
    public Mono<Void> deleteWorkflowTypeFieldById(Long id) {
        return fieldRepository.deleteAllByWorkflowTypeId(id);
    }

    private void validateWorkflowTypeFieldRequest(List<WorkflowTypeField> req) {
        Set<String> fieldKeys = new HashSet<>();
        Set<String> fieldLabels = new HashSet<>();

        List<WorkflowTypeField> sorted = req.stream()
                .sorted(Comparator.comparing(WorkflowTypeField::getDisplayOrder))
                .toList();

        int expectedDisplayOrder = 1;

        for (WorkflowTypeField r : sorted) {
            if (!fieldKeys.add(r.getFieldKey())) {
                throw new IllegalArgumentException("Duplicate field key: " + r.getFieldKey());
            }
            if (!fieldLabels.add(r.getFieldLabel())) {
                throw new IllegalArgumentException("Duplicate field label: " + r.getFieldLabel());
            }
            if (!r.getDisplayOrder().equals(expectedDisplayOrder)) {
                throw new IllegalArgumentException("Display order should be sequential");
            }
            typeCheck(r);
            expectedDisplayOrder++;
        }
    }

    private void typeCheck(WorkflowTypeField field) {
        if (field.getDefaultValue() != null) {
            if(field.getFieldType() != WorkflowFieldType.ENUM)
                ensureAllowedValuesAbsent(field.getAllowedValues(), field.getFieldType());
            switch (field.getFieldType()) {
                case STRING -> {
                    // No specific validation for string type, but we can add length checks or other constraints if needed
                }
                case NUMBER -> {
                    try {
                        Double.parseDouble(field.getDefaultValue());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Default value must be a valid number for NUMBER field type");
                    }
                }
                case BOOLEAN -> {
                    if (!field.getDefaultValue().equalsIgnoreCase("true") && !field.getDefaultValue().equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Default value must be true or false for BOOLEAN field type");
                    }
                }

                case ENUM -> {
                    // This case is handled separately in validateWorkflowTypeFieldRequest method
                }

                case DATE -> {
                    LocalDate.parse(field.getDefaultValue());
                }

                case DATETIME -> {
                    LocalDateTime.parse(field.getDefaultValue());
                }

                case JSON -> {
                    try {
                        JsonUtils.fromJson(field.getDefaultValue(), new TypeReference<Object>() {});
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid JSON defaultValue for fieldKey: " + field.getFieldKey());
                    }
                }
                default -> {
                    throw new IllegalArgumentException("Unsupported field type: " + field.getFieldType());
                }
            }
        }
        if (field.getFieldType() == WorkflowFieldType.ENUM) {
            if (field.getAllowedValues() == null || field.getAllowedValues().isEmpty()) {
                throw new IllegalArgumentException("allowedValues must be provided for ENUM field type");
            }
            if (field.getDefaultValue() != null && !field.getAllowedValues().contains(field.getDefaultValue())) {
                throw new IllegalArgumentException("Default value must be one of the allowed values for ENUM field type");
            }
            Set<String> uniqueAllowedValues = new HashSet<>(field.getAllowedValues());
            if (uniqueAllowedValues.size() != field.getAllowedValues().size()) {
                throw new IllegalArgumentException("allowedValues must be unique for ENUM field type");
            }
        }

    }

    private static void ensureAllowedValuesAbsent(List<String> allowedValues, WorkflowFieldType fieldType) {
        if (allowedValues != null && !allowedValues.isEmpty()) {
            throw new IllegalArgumentException("allowedValues is only allowed for ENUM type, not " + fieldType);
        }
    }


}
