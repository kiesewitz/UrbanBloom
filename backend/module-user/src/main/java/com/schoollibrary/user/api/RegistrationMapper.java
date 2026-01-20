package com.schoollibrary.user.api;

import com.schoollibrary.user.application.RegisterUserCommand;
import com.schoollibrary.user.application.RegistrationResult;
import org.springframework.stereotype.Component;

/**
 * Mapper between API DTOs and application layer commands/results.
 */
@Component
public class RegistrationMapper {

    public RegisterUserCommand toCommand(RegistrationRequestDto dto) {
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail(dto.getEmail());
        command.setPassword(dto.getPassword());
        command.setFirstName(dto.getFirstName());
        command.setLastName(dto.getLastName());
        command.setStudentId(dto.getStudentId());
        command.setSchoolClass(dto.getSchoolClass());
        return command;
    }

    public RegistrationResponseDto toDto(RegistrationResult result) {
        return new RegistrationResponseDto(
                result.getUserId(),
                result.getEmail(),
                result.getMessage(),
                result.isVerificationRequired()
        );
    }
}
