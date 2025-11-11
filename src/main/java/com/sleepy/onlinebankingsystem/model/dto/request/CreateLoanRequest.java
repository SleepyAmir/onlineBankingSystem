package com.sleepy.onlinebankingsystem.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateLoanRequest {
    @NotBlank private String accountNumber;
    @Positive private BigDecimal principal;
    @Positive private BigDecimal annualInterestRate;
    @Min(1) @Max(360)
    private Integer durationMonths;
}