package com.sleepy.onlinebankingsystem.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min = 4, max = 50)
    private String username;

    @NotBlank @Size(min = 6)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Pattern(regexp = "^09[0-9]{9}$")
    private String phone;

    @Size(min = 10, max = 10)
    private String nationalCode;
}