package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.dto.EmployeeRequestDTO;
import org.example.dto.TaxDeductionResponseDTO;
import org.example.exception.EmployeeAlreadyExistsException;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Employee;
import org.example.repository.EmployeeRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {EmployeeService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class EmployeeServiceTest {
    @MockBean
    private EmployeeRepo employeeRepo;

    @Autowired
    private EmployeeService employeeService;

    @Test
    void testSaveEmployee() {
        when(employeeRepo.existsByEmployeeId(Mockito.<String>any())).thenReturn(true);

        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.saveEmployee(new EmployeeRequestDTO()));
        verify(employeeRepo).existsByEmployeeId(isNull());
    }

    @Test
    void testSaveEmployee2() {
        Employee employee = new Employee();
        employee.setDoj(LocalDate.of(1970, 1, 1));
        employee.setEmail("jane.doe@example.org");
        employee.setEmployeeId("42");
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setPhoneNumbers(new ArrayList<>());
        employee.setSalary(10.0d);
        when(employeeRepo.existsByEmployeeId(Mockito.<String>any())).thenReturn(false);
        when(employeeRepo.save(Mockito.<Employee>any())).thenReturn(employee);

        Employee actualSaveEmployeeResult = employeeService.saveEmployee(new EmployeeRequestDTO());

        verify(employeeRepo).existsByEmployeeId(isNull());
        verify(employeeRepo).save(isA(Employee.class));
        assertSame(employee, actualSaveEmployeeResult);
    }

    @Test
    void testSaveEmployee3() {
        when(employeeRepo.existsByEmployeeId(Mockito.<String>any()))
                .thenThrow(new EmployeeAlreadyExistsException("An error occurred"));

        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.saveEmployee(new EmployeeRequestDTO()));
        verify(employeeRepo).existsByEmployeeId(isNull());
    }

    @Test
    void testCalculateTax() throws EmployeeNotFoundException {
        Employee employee = new Employee();
        employee.setDoj(LocalDate.of(1970, 1, 1));
        employee.setEmail("jane.doe@example.org");
        employee.setEmployeeId("42");
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setPhoneNumbers(new ArrayList<>());
        employee.setSalary(10.0d);
        Optional<Employee> ofResult = Optional.of(employee);
        when(employeeRepo.findById(Mockito.<String>any())).thenReturn(ofResult);

        TaxDeductionResponseDTO actualCalculateTaxResult = employeeService.calculateTax("42");

        verify(employeeRepo).findById(eq("42"));
        assertEquals("42", actualCalculateTaxResult.getEmployeeId());
        assertEquals("Doe", actualCalculateTaxResult.getLastName());
        assertEquals("Jane", actualCalculateTaxResult.getFirstName());
        assertEquals(0.0d, actualCalculateTaxResult.getCessAmount().doubleValue());
        assertEquals(0.0d, actualCalculateTaxResult.getTaxAmount().doubleValue());
        assertEquals(120.0d, actualCalculateTaxResult.getYearlySalary().doubleValue());
    }

    @Test
    void testCalculateTax2() throws EmployeeNotFoundException {
        Employee employee = mock(Employee.class);
        when(employee.getSalary()).thenReturn(10.0d);
        when(employee.getEmployeeId()).thenReturn("42");
        when(employee.getFirstName()).thenReturn("Jane");
        when(employee.getLastName()).thenReturn("Doe");
        doNothing().when(employee).setDoj(Mockito.<LocalDate>any());
        doNothing().when(employee).setEmail(Mockito.<String>any());
        doNothing().when(employee).setEmployeeId(Mockito.<String>any());
        doNothing().when(employee).setFirstName(Mockito.<String>any());
        doNothing().when(employee).setLastName(Mockito.<String>any());
        doNothing().when(employee).setPhoneNumbers(Mockito.<List<String>>any());
        doNothing().when(employee).setSalary(Mockito.<Double>any());
        employee.setDoj(LocalDate.of(1970, 1, 1));
        employee.setEmail("jane.doe@example.org");
        employee.setEmployeeId("42");
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setPhoneNumbers(new ArrayList<>());
        employee.setSalary(10.0d);
        Optional<Employee> ofResult = Optional.of(employee);
        when(employeeRepo.findById(Mockito.<String>any())).thenReturn(ofResult);

        TaxDeductionResponseDTO actualCalculateTaxResult = employeeService.calculateTax("42");

        verify(employee).getEmployeeId();
        verify(employee).getFirstName();
        verify(employee).getLastName();
        verify(employee).getSalary();
        verify(employee).setDoj(isA(LocalDate.class));
        verify(employee).setEmail(eq("jane.doe@example.org"));
        verify(employee).setEmployeeId(eq("42"));
        verify(employee).setFirstName(eq("Jane"));
        verify(employee).setLastName(eq("Doe"));
        verify(employee).setPhoneNumbers(isA(List.class));
        verify(employee).setSalary(eq(10.0d));
        verify(employeeRepo).findById(eq("42"));
        assertEquals("42", actualCalculateTaxResult.getEmployeeId());
        assertEquals("Doe", actualCalculateTaxResult.getLastName());
        assertEquals("Jane", actualCalculateTaxResult.getFirstName());
        assertEquals(0.0d, actualCalculateTaxResult.getCessAmount().doubleValue());
        assertEquals(0.0d, actualCalculateTaxResult.getTaxAmount().doubleValue());
        assertEquals(120.0d, actualCalculateTaxResult.getYearlySalary().doubleValue());
    }

    @Test
    void testCalculateTax3() throws EmployeeNotFoundException {
        Employee employee = mock(Employee.class);
        when(employee.getSalary()).thenReturn(250000.0d);
        when(employee.getEmployeeId()).thenReturn("42");
        when(employee.getFirstName()).thenReturn("Jane");
        when(employee.getLastName()).thenReturn("Doe");
        doNothing().when(employee).setDoj(Mockito.<LocalDate>any());
        doNothing().when(employee).setEmail(Mockito.<String>any());
        doNothing().when(employee).setEmployeeId(Mockito.<String>any());
        doNothing().when(employee).setFirstName(Mockito.<String>any());
        doNothing().when(employee).setLastName(Mockito.<String>any());
        doNothing().when(employee).setPhoneNumbers(Mockito.<List<String>>any());
        doNothing().when(employee).setSalary(Mockito.<Double>any());
        employee.setDoj(LocalDate.of(1970, 1, 1));
        employee.setEmail("jane.doe@example.org");
        employee.setEmployeeId("42");
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setPhoneNumbers(new ArrayList<>());
        employee.setSalary(10.0d);
        Optional<Employee> ofResult = Optional.of(employee);
        when(employeeRepo.findById(Mockito.<String>any())).thenReturn(ofResult);

        TaxDeductionResponseDTO actualCalculateTaxResult = employeeService.calculateTax("42");
        verify(employee).getEmployeeId();
        verify(employee).getFirstName();
        verify(employee).getLastName();
        verify(employee).getSalary();
        verify(employee).setDoj(isA(LocalDate.class));
        verify(employee).setEmail(eq("jane.doe@example.org"));
        verify(employee).setEmployeeId(eq("42"));
        verify(employee).setFirstName(eq("Jane"));
        verify(employee).setLastName(eq("Doe"));
        verify(employee).setPhoneNumbers(isA(List.class));
        verify(employee).setSalary(eq(10.0d));
        verify(employeeRepo).findById(eq("42"));
        assertEquals("42", actualCalculateTaxResult.getEmployeeId());
        assertEquals("Doe", actualCalculateTaxResult.getLastName());
        assertEquals("Jane", actualCalculateTaxResult.getFirstName());
        assertEquals(10000.0d, actualCalculateTaxResult.getCessAmount().doubleValue());
        assertEquals(3000000.0d, actualCalculateTaxResult.getYearlySalary().doubleValue());
        assertEquals(462500.0d, actualCalculateTaxResult.getTaxAmount().doubleValue());
    }

    @Test
    void testCalculateTax4() throws EmployeeNotFoundException {
        Optional<Employee> emptyResult = Optional.empty();
        when(employeeRepo.findById(Mockito.<String>any())).thenReturn(emptyResult);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.calculateTax("42"));
        verify(employeeRepo).findById(eq("42"));
    }
}
