package com.Employees.EmployeeOnboard.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private Integer status;
    private String message;
    private LocalDateTime dateTime;

}
