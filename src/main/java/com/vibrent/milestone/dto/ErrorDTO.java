package com.vibrent.milestone.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO extends ErrorCodeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<FieldErrorDTO> fieldErrors;

    public ErrorDTO(String message) {
        this(message, null);
    }

    public ErrorDTO(String errorCode, String description) {
        super(errorCode,description);
    }

    ErrorDTO(String errorCode, String description, List<FieldErrorDTO> fieldErrors) {
        super(errorCode,description);
        this.fieldErrors = fieldErrors;
    }

    public void add(String objectName, String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new ArrayList<>();
        }
        fieldErrors.add(new FieldErrorDTO(objectName, field, message));
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}
