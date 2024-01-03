package com.Employees.EmployeeOnboard.serviceImpl;

import com.Employees.EmployeeOnboard.exception.EmployeeNotFoundException;
import com.Employees.EmployeeOnboard.model.Employee;
import com.Employees.EmployeeOnboard.model.Login;
import com.Employees.EmployeeOnboard.repository.EmployeeRepository;
import com.Employees.EmployeeOnboard.repository.UserRepository;
import com.Employees.EmployeeOnboard.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import security.JwtGenerator;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtTokenProvider;


    public EmployeeServiceImpl(
            JwtGenerator jwtTokenProvider,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee addEmployee(Employee emp) {
        Long latestEmployeeId = employeeRepository.findMaxEmployeeId();
        String empId = String.valueOf(10000000 + latestEmployeeId + 1);
        emp.setEmployeeId("P" + empId);
        LocalDate dob = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(emp.getBirthYear()));
        LocalDate curDate = LocalDate.now();
        emp.setAge(Period.between(dob, curDate).getYears());
        log.info("Unique Employee Id generated having value {}", emp.getEmployeeId());
        return employeeRepository.save(emp);
    }

    @Override
    public String login(Login login) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public Employee updateEmployee(Employee emp) throws EmployeeNotFoundException {
        Employee existingEmployee = employeeRepository.findEmployeeByEmployeeId(emp.getEmployeeId());
        if (!ObjectUtils.isEmpty(existingEmployee)) {
            if (emp.getGender().equalsIgnoreCase("male")) {
                existingEmployee.setAddress(emp.getAddress());
            } else {
                existingEmployee.setFirstName(emp.getFirstName());
                existingEmployee.setLastName(emp.getLastName());
            }
            existingEmployee.setDepartment(emp.getDepartment());
            existingEmployee.setDesignation(emp.getDesignation());
            existingEmployee.setMaritalStatus(emp.getMaritalStatus());
            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            return updatedEmployee;
        }
         throw new EmployeeNotFoundException("Employee is not found in record");
    }

    @Override
    public List<Employee> searchEmp(String ageRange, String birthYear, String firstname, String lastName) throws EmployeeNotFoundException {
        List<Employee> empList = employeeRepository.findAll();
        List<Employee> filteredList = new ArrayList<>();
        if (!empList.isEmpty()) {
            if (ageRange != null && !ageRange.isEmpty()) {
                String[] ageBounds = ageRange.split("-");
                int minAge = Integer.parseInt(ageBounds[0]);
                int maxAge = Integer.parseInt(ageBounds[1]);
                filteredList = empList.stream()
                        .filter(emp -> (emp.getAge() >= minAge && emp.getAge() <= maxAge))
                        .collect(Collectors.toList());
            }

            if (birthYear != null && !birthYear.isEmpty()) {
                int targetYear = Integer.parseInt(birthYear);
                filteredList = empList.stream()
                        .filter(emp -> {
                            LocalDate empBirthDate = LocalDate.parse(String.valueOf(emp.getBirthYear()));
                            return empBirthDate.getYear() == targetYear;
                        })
                        .collect(Collectors.toList());
            }

            if (firstname != null && !firstname.isEmpty()) {
                filteredList = empList.stream()
                        .filter(emp -> emp.getFirstName().equalsIgnoreCase(firstname))
                        .collect(Collectors.toList());
            }
            if (lastName != null && !lastName.isEmpty()) {
                filteredList = empList.stream()
                        .filter(emp -> emp.getLastName().equalsIgnoreCase(lastName))
                        .collect(Collectors.toList());
            }
        } else {
            throw new EmployeeNotFoundException("Employee Not Found!!");
        }
        if(filteredList.isEmpty()) {
            throw new EmployeeNotFoundException("Employee Not Found!!");
        }
        return filteredList;
    }

}
