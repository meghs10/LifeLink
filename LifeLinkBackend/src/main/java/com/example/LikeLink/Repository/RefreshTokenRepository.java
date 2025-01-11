package com.example.LikeLink.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.LikeLink.Model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(String userId);
    void deleteByUserId(String userId);
}
