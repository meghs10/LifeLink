package com.example.LikeLink.Repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.LikeLink.Model.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);
}