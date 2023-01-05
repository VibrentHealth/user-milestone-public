package com.vibrent.milestone.exceptions;

import com.vibrent.milestone.dto.ErrorCodeDTO;
import com.vibrent.milestone.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class MilestoneExceptionHandler {

    @ExceptionHandler({BusinessProcessingException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorCodeDTO processBusinessProcessingException(BusinessProcessingException e) {
        log.warn("UserMilestone: Exception received. message :{}", e.getMessage(), e);
        return new ErrorCodeDTO(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorCodeDTO processMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("UserMilestone: Method Argument Not Valid Exception received. message :{}", e.getMessage(), e);
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

 
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorCodeDTO processHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("UserMilestone: HttpMessage Not Readable Exception received. message :{}", e.getMessage(), e);
        String message = e.getMessage() != null ? e.getMessage().split(";")[0] : "Parse Error";
        return new ErrorCodeDTO(message);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorCodeDTO processConstraintViolationException(ConstraintViolationException e) {
        String errorMsg = e.getConstraintViolations() == null ? e.getMessage() :
                e.getConstraintViolations().stream().map(cv -> String.format("%s: '%s", cv.getPropertyPath(), cv.getMessage())).collect(Collectors.joining(", "));
        log.warn("UserMilestone: Constraint error Msg - {}", errorMsg, e);
        return new ErrorCodeDTO(errorMsg);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorCodeDTO processAccessDeniedException(AccessDeniedException e) {
        log.warn("UserMilestone: Access denied :{}", e.getMessage(), e);
        return new ErrorCodeDTO(e.getMessage());
    }

    private ErrorCodeDTO processFieldErrors(List<FieldError> fieldErrors) {
        ErrorDTO dto = new ErrorDTO("BAD REQUEST");
        for (FieldError fieldError : fieldErrors) {
            String message = StringUtils.isEmpty(fieldError.getDefaultMessage()) ? fieldError.getCode() : fieldError.getDefaultMessage();
            dto.add(fieldError.getObjectName(), fieldError.getField(), message);
        }
        return dto;
    }
}
