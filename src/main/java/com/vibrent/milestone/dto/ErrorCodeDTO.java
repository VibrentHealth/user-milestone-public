package com.vibrent.milestone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

/**
 * DTO for transferring error code with description.
 */
@JsonInclude(Include.NON_NULL)
public class ErrorCodeDTO implements Serializable {
    private static final long serialVersionUID = -2762642570150751724L;

    protected String description;
    protected String errorCode;

    public ErrorCodeDTO() {

    }

    public ErrorCodeDTO(String description) {
        this.description = description;
    }

    public ErrorCodeDTO(String errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
