package com.Employees.EmployeeOnboard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer ";

    public JWTAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
