package com.sleepy.onlinebankingsystem.model.dto.request;

import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotNull private AccountType type;
}