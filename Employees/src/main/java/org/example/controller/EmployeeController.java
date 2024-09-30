package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.EmployeeRequestDTO;
import org.example.dto.TaxDeductionResponseDTO;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/save")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequest) {
        Employee savedEmployee = employeeService.saveEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @GetMapping("/{employeeId}/tax-deductions")
    public ResponseEntity<TaxDeductionResponseDTO> getTaxDeductions(@PathVariable String employeeId) throws EmployeeNotFoundException {
        TaxDeductionResponseDTO taxResponse = employeeService.calculateTax(employeeId);
        return ResponseEntity.ok(taxResponse);
    }
}

