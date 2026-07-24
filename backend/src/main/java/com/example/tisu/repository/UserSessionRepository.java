package com.example.tisu.repository;

import com.example.tisu.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> findByRefreshTokenHashAndRevokedAtIsNull(String refreshTokenHash);

    List<UserSession> findByUserIdAndRevokedAtIsNull(String userId);
}
