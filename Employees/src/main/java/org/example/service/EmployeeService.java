package org.example.service;

import org.example.dto.EmployeeRequestDTO;
import org.example.dto.TaxDeductionResponseDTO;
import org.example.exception.EmployeeAlreadyExistsException;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Employee;
import org.example.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepo employeeRepo;

    public Employee saveEmployee(EmployeeRequestDTO employeeRequest) {
        if (employeeRepo.existsByEmployeeId(employeeRequest.getEmployeeId())) {
            throw new EmployeeAlreadyExistsException("Employee ID must be unique");
        }
        Employee employee = Employee.builder()
                .employeeId(employeeRequest.getEmployeeId())
                .firstName(employeeRequest.getFirstName())
                .lastName(employeeRequest.getLastName())
                .email(employeeRequest.getEmail())
                .phoneNumbers(employeeRequest.getPhoneNumbers())
                .doj(employeeRequest.getDoj())
                .salary(employeeRequest.getSalary())
                .build();
        return employeeRepo.save(employee);
    }

    public TaxDeductionResponseDTO calculateTax(String employeeId) throws EmployeeNotFoundException {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        double yearlySalary = employee.getSalary() * 12;
        double taxAmount = calculateTaxAmount(yearlySalary);
        double cessAmount = calculateCessAmount(yearlySalary);

        return TaxDeductionResponseDTO.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .yearlySalary(yearlySalary)
                .taxAmount(taxAmount)
                .cessAmount(cessAmount)
                .build();
    }

    private double calculateTaxAmount(double yearlySalary) {
        if (yearlySalary <= 250000) {
            return 0;
        } else if (yearlySalary <= 500000) {
            return (yearlySalary - 250000) * 0.05;
        } else if (yearlySalary <= 1000000) {
            return 250000 * 0.05 + (yearlySalary - 500000) * 0.10;
        } else {
            return 250000 * 0.05 + 500000 * 0.10 + (yearlySalary - 1000000) * 0.20;
        }
    }

    private double calculateCessAmount(double yearlySalary) {
        if (yearlySalary > 2500000) {
            return (yearlySalary - 2500000) * 0.02;
        }
        return 0;
    }
}

