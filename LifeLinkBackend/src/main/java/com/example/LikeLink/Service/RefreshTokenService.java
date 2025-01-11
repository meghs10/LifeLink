package com.example.LikeLink.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.LikeLink.Exception.TokenRefreshException;
import com.example.LikeLink.Model.RefreshToken;
import com.example.LikeLink.Repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${jwt.refreshToken.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUserId(userId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), 
                "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
