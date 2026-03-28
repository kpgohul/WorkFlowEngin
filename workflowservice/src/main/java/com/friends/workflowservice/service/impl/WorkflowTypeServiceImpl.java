package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.dto.common.PagedResponse;
import com.friends.workflowservice.dto.workflowtype.*;
import com.friends.workflowservice.entity.WorkflowType;
import com.friends.workflowservice.exception.ResourceAlreadyExistException;
import com.friends.workflowservice.exception.ResourceNotFoundException;
import com.friends.workflowservice.mapper.WorkflowTypeMapper;
import com.friends.workflowservice.repo.WorkflowTypeRepository;
import com.friends.workflowservice.service.WorkflowTypeFieldService;
import com.friends.workflowservice.service.WorkflowTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowTypeServiceImpl implements WorkflowTypeService {

    private final WorkflowTypeFieldService fieldService;
    private final WorkflowTypeRepository typeRepository;
    private final TransactionalOperator transactionalOperator;

    // code and name should be unique against the existing one

    @Override
    public Mono<WorkflowTypeResponse> createWorkflowType(Mono<CreateWorkflowTypeRequest> reqMono) {
        return reqMono.flatMap(req -> {

            Mono<WorkflowTypeResponse> flow =
                    typeRepository.existsByCodeOrName(req.getCode(), req.getName())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new ResourceAlreadyExistException("WorkflowType", "NameOrCode", req.getName() + " or " + req.getCode()));
                                }

                                WorkflowType entity = WorkflowTypeMapper.toEntity(req);

                                return typeRepository.save(entity)
                                        .flatMap(saved ->
                                                fieldService.createWorkflowTypeFields(
                                                                saved.getId(),
                                                                Mono.just(req.getFields())
                                                        )
                                                        .map(fields -> {
                                                            WorkflowTypeResponse response =
                                                                    WorkflowTypeMapper.toResponse(saved);
                                                            response.setFields(fields);
                                                            return response;
                                                        })
                                        );
                            });

            return flow.as(transactionalOperator::transactional);
        });
    }

    @Override
    public Mono<WorkflowTypeResponse> updateWorkflowType(Mono<UpdateWorkflowTypeRequest> reqMono) {
        return reqMono
                .flatMap(req -> {

                    Mono<WorkflowTypeResponse> flow =
                            typeRepository.findById(req.getId())
                                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowType", "id", req.getId().toString())))
                                    .flatMap(existing ->

                                            typeRepository.existsByCodeOrNameExcludingIds(req.getCode(), req.getName(), List.of(req.getId()))
                                                    .flatMap(exists -> {
                                                        if (exists)
                                                            return Mono.error(new ResourceAlreadyExistException("WorkflowType", "NameOrCode", req.getName() + " or " + req.getCode()));

                                                        WorkflowType updated = WorkflowTypeMapper.toEntity(req, existing);

                                                        return typeRepository.save(updated)
                                                                .flatMap(saved -> {

                                                                    return fieldService.updateWorkflowTypeFields(req.getId(), Mono.just(req.getFields()))
                                                                            .map(fields -> {
                                                                                WorkflowTypeResponse response =
                                                                                        WorkflowTypeMapper.toResponse(saved);
                                                                                response.setFields(fields);
                                                                                return response;
                                                                            });
                                                                });
                                                    })
                                    );

                    return flow.as(transactionalOperator::transactional);
                });
    }

    @Override
    public Mono<WorkflowTypeResponse> getWorkflowTypeById(Long id) {
        return typeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceAlreadyExistException("WorkflowType", "id", id.toString())))
                .flatMap(workflowType ->
                        fieldService.getWorkflowTypeFieldById(workflowType.getId())
                                .map(fields -> {
                                    WorkflowTypeResponse response = WorkflowTypeMapper.toResponse(workflowType);
                                    response.setFields(fields);
                                    return response;
                                })
                );
    }

    @Override
    public Mono<PagedResponse<WorkflowTypeResponse>> getAllWorkflowTypes(int page, int size) {

        long offset = (long) page * size;

        Mono<List<WorkflowTypeResponse>> contentMono = typeRepository.findAllPaged(size, offset)
                .flatMap(workflowType ->
                        fieldService.getWorkflowTypeFieldById(workflowType.getId())
                                .map(fields -> {
                                    WorkflowTypeResponse response = WorkflowTypeMapper.toResponse(workflowType);
                                    response.setFields(fields);
                                    return response;
                                })
                )
                .collectList();

        Mono<Long> countMono = typeRepository.countAll();

        return Mono.zip(contentMono, countMono)
                .map(tuple -> {
                    List<WorkflowTypeResponse> content = tuple.getT1();
                    long totalElements = tuple.getT2();

                    return PagedResponse.<WorkflowTypeResponse>builder()
                            .content(content)
                            .page(page + 1)
                            .size(size)
                            .totalElements(totalElements)
                            .totalPages((int) Math.ceil((double) totalElements / size))
                            .build();
                });
    }

    @Override
    public Mono<Void> deleteWorkflowTypeById(Long id) {
        Mono<Void> flow = typeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowType", "id", id.toString())))
                .flatMap(existing ->
                        fieldService.deleteWorkflowTypeFieldById(id)
                                .then(typeRepository.deleteById(id))
                );

        return flow.as(transactionalOperator::transactional);
    }

//    private void validatePageAndSize(int page, int size) {
//        if (page < 0) {
//            throw new IllegalArgumentException("page must be greater than or equal to 0");
//        }
//        if (size <= 0 || size > 100) {
//            throw new IllegalArgumentException("size must be between 1 and 100");
//        }
//    }
}
