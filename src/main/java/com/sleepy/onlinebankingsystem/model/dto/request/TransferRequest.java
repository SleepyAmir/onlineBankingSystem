package com.sleepy.onlinebankingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotBlank private String fromAccountNumber;
    @NotBlank private String toAccountNumber;
    @Positive private BigDecimal amount;
    private String description;
}