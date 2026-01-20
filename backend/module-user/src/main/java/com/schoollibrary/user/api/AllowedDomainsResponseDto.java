package com.schoollibrary.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for allowed-domains endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowedDomainsResponseDto {

    private List<String> domains;
}
