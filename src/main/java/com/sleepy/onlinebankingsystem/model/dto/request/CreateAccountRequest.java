package com.sleepy.onlinebankingsystem.model.dto.request;

import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotNull
    private Long userId;

    @NotNull
    private AccountType type;

    @PositiveOrZero(message = "موجودی اولیه نمی‌تواند منفی باشد")
    private BigDecimal initialBalance;

}