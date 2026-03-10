package com.urbanbloom.user.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command for user logout.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutCommand {
    private String refreshToken;
    private String realm;
}
