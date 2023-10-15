package com.mk.todolist.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMostSpecificCause().getMessage()));
    }
}