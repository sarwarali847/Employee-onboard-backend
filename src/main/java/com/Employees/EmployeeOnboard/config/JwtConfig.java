package com.Employees.EmployeeOnboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import security.JwtGenerator;

@Configuration
public class JwtConfig {
    @Bean
    public JwtGenerator jwtGenerator() {
        return new JwtGenerator();
    }
}
