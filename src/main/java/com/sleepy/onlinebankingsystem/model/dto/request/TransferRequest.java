package com.sleepy.onlinebankingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank
    private String fromCardNumber;
    @NotBlank
    private String toCardNumber;
    @Positive
    private BigDecimal amount;
    private String description;


    public String getFromCardNumber() { return fromCardNumber; }
    public void setFromCardNumber(String fromCardNumber) { this.fromCardNumber = fromCardNumber; }
    public String getToCardNumber() { return toCardNumber; }
    public void setToCardNumber(String toCardNumber) { this.toCardNumber = toCardNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}