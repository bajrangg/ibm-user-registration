package com.ibm.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class User {
    @NotNull
    @NotEmpty
    private String userName;

    // Password need to be greater than 8 characters, containing at least 1 number, 1 Captialized letter,
    // 1 special character in this set "_ # $ % ." Return error messages if not valid
    @NotNull
    @NotEmpty
    @Pattern(regexp="(?=.*[0-9])(?=.*[A-Z])(?=.*[_#$%.]).{8,}", message="Length must be > 8 and contain at " +
            "least 1 number, 1 capital letter, 1 special character from '_ # $ % .' ")
    private String password;

    @NotNull
    @NotEmpty
    private String ipAddress;
}
