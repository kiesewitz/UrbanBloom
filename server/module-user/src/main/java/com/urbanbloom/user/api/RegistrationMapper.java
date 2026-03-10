package com.urbanbloom.user.api;

import com.urbanbloom.user.application.RegisterUserCommand;
import com.urbanbloom.user.application.RegistrationResult;
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
        return command;
    }

    public RegistrationResponseDto toDto(RegistrationResult result) {
        return new RegistrationResponseDto(
                result.getUserId(),
                result.getExternalId(),
                result.getMessage(),
                result.isVerificationRequired()
        );
    }
}
