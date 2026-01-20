package com.schoollibrary.user.adapter.persistence;

import com.schoollibrary.user.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementing the UserProfileRepository port.
 * Translates between domain model and JPA persistence.
 */
@Component
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements com.schoollibrary.user.domain.UserProfileRepository {

    private final UserProfileJpaRepository jpaRepository;
    private final UserProfilePersistenceMapper mapper;

    @Override
    public UserProfile save(UserProfile userProfile) {
        UserProfileJpaEntity entity = mapper.toEntity(userProfile);
        UserProfileJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserProfile> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserProfile> findByExternalUserId(ExternalUserId externalUserId) {
        return jpaRepository.findByExternalUserId(externalUserId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserProfile> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByExternalUserId(ExternalUserId externalUserId) {
        return jpaRepository.existsByExternalUserId(externalUserId.getValue());
    }

    @Override
    public void delete(UserProfile userProfile) {
        jpaRepository.deleteById(userProfile.getId());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
