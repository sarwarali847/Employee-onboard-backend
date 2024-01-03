package com.Employees.EmployeeOnboard.exception;

import com.Employees.EmployeeOnboard.config.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> employeeNotFoundException(EmployeeNotFoundException ex) {

        ExceptionResponse errorModel = new ExceptionResponse();
        errorModel.setStatus(HttpStatus.NOT_FOUND.value());
        errorModel.setMessage(ex.getMessage());
        errorModel.setDateTime(LocalDateTime.now());

        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globleExcpetionHandler(Exception ex) {
        ExceptionResponse errorModel = new ExceptionResponse();
        errorModel.setStatus(HttpStatus.NOT_FOUND.value());
        errorModel.setMessage(ex.getMessage());
        errorModel.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
