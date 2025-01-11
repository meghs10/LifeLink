package com.example.LikeLink.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.LikeLink.Model.Hospital;

public interface HospitalRepository extends MongoRepository<Hospital, String> {
    boolean existsByUserId(String userId);
    Optional<Hospital> findByUserId(String userId);
}