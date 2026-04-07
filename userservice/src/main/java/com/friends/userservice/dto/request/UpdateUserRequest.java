package com.friends.userservice.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @Size(max = 16, message = "Gender must not exceed 16 characters")
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 8, message = "Blood group must not exceed 8 characters")
    private String bloodGroup;

    private Integer age;

    @Size(max = 8, message = "Country code must not exceed 8 characters")
    private String countryCode;

    @Pattern(regexp = "^[0-9]{7,15}$", message = "Mobile must be 7-15 digits")
    private String mobile;

    @Size(max = 64, message = "Country must not exceed 64 characters")
    private String country;

    @Size(max = 64, message = "State must not exceed 64 characters")
    private String state;

    @Size(max = 256, message = "Address must not exceed 256 characters")
    private String address;

    @Size(max = 16, message = "Pincode must not exceed 16 characters")
    private String pincode;
}

