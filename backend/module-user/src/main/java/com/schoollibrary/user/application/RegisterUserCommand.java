package com.schoollibrary.user.application;

import lombok.Data;

/**
 * Command for user registration.
 * Represents the input for the registration use case.
 */
@Data
public class RegisterUserCommand {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String studentId;
    private String schoolClass;
}
