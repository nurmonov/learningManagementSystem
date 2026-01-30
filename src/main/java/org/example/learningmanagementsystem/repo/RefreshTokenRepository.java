package org.example.learningmanagementsystem.repo;

import org.example.learningmanagementsystem.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Integer userId);

    Optional<RefreshToken> getTokenByUserId(Integer userId);

    boolean existsByUserId(Integer userId);
}
