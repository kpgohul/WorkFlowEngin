package com.friends.executionservice.exception;

public class ResourceAlreadyExistException extends RuntimeException{
    public ResourceAlreadyExistException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s is already exist with the given input data %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
