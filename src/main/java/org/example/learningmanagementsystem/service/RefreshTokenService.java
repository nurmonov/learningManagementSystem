package org.example.learningmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.entity.RefreshToken;
import org.example.learningmanagementsystem.repo.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DURATION_DAYS = 14;

    private final RefreshTokenRepository refreshTokenRepository;




    public Optional<RefreshToken> createOrGetRefreshToken(Integer userId) {
        if(!refreshTokenRepository.existsByUserId(userId)){
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUserId(userId);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(
                    Instant.now().plus(REFRESH_TOKEN_DURATION_DAYS, ChronoUnit.DAYS)
            );
            return Optional.of(refreshTokenRepository.save(refreshToken));
        }
        return refreshTokenRepository.getTokenByUserId(userId);
    }



    public RefreshToken verify(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }


    public void deleteByUserId(Integer userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}

