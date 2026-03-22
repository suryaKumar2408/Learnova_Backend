package com.Learnova.Learnova_Backend.repository;

import com.Learnova.Learnova_Backend.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(String userId);
    void deleteByToken(String token);
}
