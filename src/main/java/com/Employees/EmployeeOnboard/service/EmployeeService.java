package com.Employees.EmployeeOnboard.service;

import com.Employees.EmployeeOnboard.exception.EmployeeNotFoundException;
import com.Employees.EmployeeOnboard.model.Employee;
import com.Employees.EmployeeOnboard.model.Login;

import java.util.List;

public interface EmployeeService {
     Employee addEmployee(Employee emp);
     String login(Login login);
     Employee updateEmployee(Employee emp) throws EmployeeNotFoundException;
     List<Employee> searchEmp(String ageRange, String birthYear, String firstname, String lastName) throws EmployeeNotFoundException;
}
