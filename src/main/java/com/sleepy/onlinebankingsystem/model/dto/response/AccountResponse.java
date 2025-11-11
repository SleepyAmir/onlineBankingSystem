package com.sleepy.onlinebankingsystem.model.dto.response;

import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class AccountResponse {
    private String accountNumber;
    private AccountType type;
    private BigDecimal balance;
    private AccountStatus status;
}