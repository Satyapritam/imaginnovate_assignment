package org.example.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TaxDeductionResponseDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private Double yearlySalary;
    private Double taxAmount;
    private Double cessAmount;
}
