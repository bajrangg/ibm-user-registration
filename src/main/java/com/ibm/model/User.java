package com.ibm.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class User {
    @NotBlank
    private String userName;

    // Password need to be greater than 8 characters, containing at least 1 number, 1 Captialized letter,
    // 1 special character in this set "_ # $ % ." Return error messages if not valid
    @NotBlank
    @Pattern(regexp="(?=.*[0-9])(?=.*[A-Z])(?=.*[_#$%.]).{8,}", message="Length must be > 8 and contain at " +
            "least 1 number, 1 capital letter, 1 special character from '_ # $ % .' ")
    private String password;

    @NotBlank
    private String ipAddress;
}
