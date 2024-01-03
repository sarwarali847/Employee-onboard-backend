package com.Employees.EmployeeOnboard.repository;

import com.Employees.EmployeeOnboard.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e.id FROM Employee e WHERE e.id = (SELECT MAX(e2.id) FROM Employee e2)")
    Long findMaxEmployeeId();

    Employee findEmployeeByEmployeeId(String employeeId);

}
