package com.schoollibrary.user.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for UserProfileJpaEntity.
 */
@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, String> {
    Optional<UserProfileJpaEntity> findByExternalUserId(String externalUserId);
    Optional<UserProfileJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByExternalUserId(String externalUserId);
}
