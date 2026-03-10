package com.urbanbloom.user.adapter.persistence;

import com.urbanbloom.user.domain.*;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain UserProfile and JPA entity.
 * Uses manual mapping (can be replaced with MapStruct if needed).
 */
@Component
public class UserProfilePersistenceMapper {

    public UserProfileJpaEntity toEntity(UserProfile domain) {
        return UserProfileJpaEntity.builder()
                .id(domain.getId())
                .externalUserId(domain.getExternalUserId().getValue())
                .email(domain.getEmail().getValue())
                .firstName(domain.getUserName().getFirstName())
                .lastName(domain.getUserName().getLastName())
                .role(domain.getRole().name())
                .active(domain.isActive())
                .points(domain.getPoints())
                .build();
    }

    public UserProfile toDomain(UserProfileJpaEntity entity) {
        return UserProfile.reconstruct(
                entity.getId(),
                ExternalUserId.of(entity.getExternalUserId()),
                Email.of(entity.getEmail()),
                UserName.of(entity.getFirstName(), entity.getLastName()),
                UserRole.of(entity.getRole()),
                entity.getPoints(),
                entity.isActive()
        );
    }
}
