package com.sleepy.onlinebankingsystem.model.dto.response;

import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class TransactionResponse {
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime date;
    private TransactionStatus status;
    private String referenceNumber;
}