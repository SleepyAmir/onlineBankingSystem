package com.sleepy.onlinebankingsystem.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateLoanRequest {

    @NotBlank(message = "شماره حساب الزامی است")
    private String accountNumber;

    @NotNull(message = "مبلغ اصل وام الزامی است")
    @DecimalMin(value = "1000000", message = "حداقل مبلغ وام 1,000,000 ریال است")
    @DecimalMax(value = "1000000000", message = "حداکثر مبلغ وام 1,000,000,000 ریال است")
    private BigDecimal principal;

    @NotNull(message = "نرخ بهره الزامی است")
    @DecimalMin(value = "5.0", message = "حداقل نرخ بهره 5% است")
    @DecimalMax(value = "30.0", message = "حداکثر نرخ بهره 30% است")
    private BigDecimal annualInterestRate;

    @NotNull(message = "مدت زمان وام الزامی است")
    @Min(value = 6, message = "حداقل مدت وام 6 ماه است")
    @Max(value = 60, message = "حداکثر مدت وام 60 ماه است")
    private Integer durationMonths;
}
