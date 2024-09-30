package org.example.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;

import org.example.dto.EmployeeRequestDTO;
import org.example.dto.TaxDeductionResponseDTO;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {EmployeeController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class EmployeeControllerTest {
    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setDoj(LocalDate.of(1970, 1, 1));
        employeeRequestDTO.setEmail("jane.doe@example.org");
        employeeRequestDTO.setEmployeeId("42");
        employeeRequestDTO.setFirstName("Jane");
        employeeRequestDTO.setLastName("Doe");
        employeeRequestDTO.setPhoneNumbers(new ArrayList<>());
        employeeRequestDTO.setSalary(10.0d);
        String content = (new ObjectMapper()).writeValueAsString(employeeRequestDTO);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/employees/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(employeeController).build().perform(requestBuilder);
    }

    @Test
    void testGetTaxDeductions() throws Exception {
        TaxDeductionResponseDTO buildResult = TaxDeductionResponseDTO.builder()
                .cessAmount(10.0d)
                .employeeId("42")
                .firstName("Jane")
                .lastName("Doe")
                .taxAmount(10.0d)
                .yearlySalary(10.0d)
                .build();
        when(employeeService.calculateTax(Mockito.<String>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/employees/{employeeId}/tax-deductions", "42");

        MockMvcBuilders.standaloneSetup(employeeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"employeeId\":\"42\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"yearlySalary\":10.0,\"taxAmount\":10.0,\"cessAmount"
                                        + "\":10.0}"));
    }
}
