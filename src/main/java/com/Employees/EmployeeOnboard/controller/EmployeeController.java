package com.Employees.EmployeeOnboard.controller;

import com.Employees.EmployeeOnboard.exception.EmployeeNotFoundException;
import com.Employees.EmployeeOnboard.model.Employee;
import com.Employees.EmployeeOnboard.model.JWTAuthResponse;
import com.Employees.EmployeeOnboard.model.Login;
import com.Employees.EmployeeOnboard.model.Register;
import com.Employees.EmployeeOnboard.model.Role;
import com.Employees.EmployeeOnboard.model.UserEntity;
import com.Employees.EmployeeOnboard.repository.RoleRepository;
import com.Employees.EmployeeOnboard.repository.UserRepository;
import com.Employees.EmployeeOnboard.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import security.JwtGenerator;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/employees")
@CrossOrigin("*")
public class EmployeeController {

    private EmployeeService employeeService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;

    public EmployeeController(EmployeeService employeeService, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {

        this.employeeService = employeeService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<Employee> createEmployee(@RequestBody @Validated Employee employee) {
        log.info("Create Employee Method started with request {}", employee);
        Employee savedEmployee = employeeService.addEmployee(employee);
        log.info("Employee Record Inserted {}", employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(
            @RequestParam(required = false) String ageRange,
            @RequestParam(required = false) String birthYear,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastName
    ) throws EmployeeNotFoundException {
        log.info("Search Employee Method started with request age range {}, birth years{}, first name {}, last name {}", ageRange, birthYear, firstname, lastName);
        List<Employee> searchedList = employeeService.searchEmp(ageRange, birthYear, firstname, lastName);
        log.info("Employee Record Fetched by criteria {}", searchedList);
        if (searchedList.size() > 0) {
            return new ResponseEntity<>(searchedList, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @PutMapping("/updateEmployee/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") String id, @RequestBody @Validated Employee employee) throws EmployeeNotFoundException {
        log.info("Update Employee Method started with request {}", employee);
        employee.setEmployeeId(id);
        Employee updateEmployee = employeeService.updateEmployee(employee);
        log.info("Employee Record Updated {}", employee);
        if (!ObjectUtils.isEmpty(updateEmployee)) {
            return new ResponseEntity<>(updateEmployee, HttpStatus.ACCEPTED);
        }
        log.warn("Employee with id {} not found", id);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> authenticate(@RequestBody Login login){
        log.info("Login api to generate token is invoked with request {}", login);
        String token = employeeService.login(login);
        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Register registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }
}
