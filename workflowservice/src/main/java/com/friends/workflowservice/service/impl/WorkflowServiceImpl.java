package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.dto.common.PagedResponse;
import com.friends.workflowservice.dto.common.WorkflowStepRuleResponse;
import com.friends.workflowservice.dto.workflow.CreateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.UpdateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.WorkflowResponse;
import com.friends.workflowservice.entity.Workflow;
import com.friends.workflowservice.exception.ResourceAlreadyExistException;
import com.friends.workflowservice.exception.ResourceNotFoundException;
import com.friends.workflowservice.mapper.WorkflowMapper;
import com.friends.workflowservice.repo.WorkflowRepository;
import com.friends.workflowservice.repo.WorkflowStepRepository;
import com.friends.workflowservice.repo.WorkflowTypeRepository;
import com.friends.workflowservice.service.WorkflowService;
import com.friends.workflowservice.service.WorkflowStepService;
import com.friends.workflowservice.service.WorkflowTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowTypeRepository workflowTypeRepository;
    private final WorkflowTypeService workflowTypeService;
    private final WorkflowStepService stepService;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<WorkflowResponse> createWorkflow(Mono<CreateWorkflowRequest> requestMono) {
        return requestMono.flatMap(request -> {
            if (request.getStepRule() == null || request.getStepRule().isEmpty()) {
                return Mono.error(new IllegalArgumentException("At least one step-rule is required"));
            }
            if (request.getWorkflowTypeId() == null) {
                return Mono.error(new IllegalArgumentException("Workflow type ID is required"));
            }

            return workflowTypeRepository.existsById(request.getWorkflowTypeId())
                    .flatMap(typeExists -> {
                        if (!typeExists) {
                            return Mono.error(new ResourceNotFoundException("WorkflowType", "id", request.getWorkflowTypeId().toString()));
                        }

                        return workflowRepository.existsByName(request.getName())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new ResourceAlreadyExistException("Workflow", "name", request.getName()));
                                    }

                                    Workflow entity = WorkflowMapper.toEntityWithDefaults(request);

                                    return workflowRepository.save(entity)
                                            .flatMap(saved -> {
                                                var stepRules = WorkflowMapper.attachWorkflowId(saved.getId(), request.getStepRule());

                                                return stepService.createStepRules(Flux.fromIterable(stepRules))
                                                        .collectList()
                                                        .map(stepRuleResponses -> WorkflowMapper.toResponse(saved, stepRuleResponses));
                                            });
                                });
                    })
                    .as(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<WorkflowResponse> updateWorkflow(Mono<UpdateWorkflowRequest> requestMono) {
        return requestMono.flatMap(request -> {
            Mono<WorkflowResponse> flow = workflowRepository.findById(request.getId())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", request.getId().toString())))
                    .flatMap(existing -> {
                        String name = request.getName() == null ? existing.getName() : request.getName();

                        return workflowRepository.existsByNameAndIdNot(name, existing.getId())
                                .flatMap(nameExists -> {
                                    if (nameExists) {
                                        return Mono.error(new ResourceAlreadyExistException("Workflow", "name", name));
                                    }

                                    Mono<Void> typeCheck = Mono.empty();
                                    if (request.getWorkflowTypeId() != null && !request.getWorkflowTypeId().equals(existing.getWorkflowTypeId())) {
                                        typeCheck = workflowTypeRepository.existsById(request.getWorkflowTypeId())
                                                .flatMap(typeExists -> {
                                                    if (!typeExists) {
                                                        return Mono.error(new ResourceNotFoundException("WorkflowType", "id", request.getWorkflowTypeId().toString()));
                                                    }
                                                    return Mono.empty();
                                                });
                                    }

                                    return typeCheck.then(
                                            workflowRepository.save(WorkflowMapper.mergeForUpdate(request, existing))
                                                    .flatMap(saved -> {
                                                        // Step 2a: No step-rules in request → return workflow with existing steps/rules
                                                        if (request.getStepRule() == null || request.getStepRule().isEmpty()) {
                                                            return fetchStepRulesByWorkflowId(saved.getId())
                                                                    .map(stepRuleResponses -> WorkflowMapper.toResponse(saved, stepRuleResponses));
                                                        }

                                                        // Step 2b: Delete all existing steps+rules, then create fresh ones from the request DTOs
                                                // workflowId is attached programmatically — clients do NOT need to send stepId or rule id
                                                        var freshStepRules = WorkflowMapper.attachWorkflowId(saved.getId(), request.getStepRule());

                                                        return stepRepository.findAllByWorkflowId(saved.getId())
                                                                .concatMap(existingStep -> stepService.deleteStepRulesById(existingStep.getId()))
                                                                .then(stepService.createStepRules(Flux.fromIterable(freshStepRules)).collectList())
                                                                .map(stepRuleResponses -> WorkflowMapper.toResponse(saved, stepRuleResponses));
                                                    })
                                    );
                                });
                    });

            return flow.as(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<WorkflowResponse> getWorkflowById(Long id, boolean includeWorkflowType) {
        return workflowRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", id.toString())))
                .flatMap(workflow -> fetchStepRulesByWorkflowId(workflow.getId())
                        .map(stepRuleResponses -> WorkflowMapper.toResponse(workflow, stepRuleResponses)))
                .flatMap(response -> includeWorkflowType
                        ? workflowTypeService.getWorkflowTypeById(response.getWorkflowTypeId())
                                .map(type -> {
                                    response.setWorkflowType(type);
                                    return response;
                                })
                        : Mono.just(response));
    }

    @Override
    public Mono<PagedResponse<WorkflowResponse>> getAllWorkflows(int page, int size, boolean includeWorkflowType) {
        long offset = (long) page * size;

        Mono<List<WorkflowResponse>> contentMono = workflowRepository.findAllPaged(size, offset)
                .flatMap(workflow -> getWorkflowById(workflow.getId(), includeWorkflowType))
                .collectList();

        Mono<Long> countMono = workflowRepository.countAll();

        return Mono.zip(contentMono, countMono)
                .map(tuple -> PagedResponse.<WorkflowResponse>builder()
                        .content(tuple.getT1())
                        .page(page + 1)
                        .size(size)
                        .totalElements(tuple.getT2())
                        .totalPages((int) Math.ceil((double) tuple.getT2() / size))
                        .build());
    }

    @Override
    public Mono<Void> deleteWorkflowById(Long id) {
        Mono<Void> flow = workflowRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", id.toString())))
                .flatMap(existing -> stepRepository.findAllByWorkflowId(existing.getId())
                        .flatMap(step -> stepService.deleteStepRulesById(step.getId()))
                        .then(workflowRepository.deleteById(existing.getId())));

        return flow.as(transactionalOperator::transactional);
    }

    private Mono<List<WorkflowStepRuleResponse>> fetchStepRulesByWorkflowId(Long workflowId) {
        return stepRepository.findAllByWorkflowId(workflowId)
                .concatMap(step -> stepService.getStepRulesById(step.getId()).singleOrEmpty()
                        .onErrorResume(ResourceNotFoundException.class, e -> Mono.empty()))
                .collectList();
    }
}
