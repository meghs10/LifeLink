package com.example.LikeLink.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.LikeLink.Enum.InsuranceType;
import com.example.LikeLink.Model.Insurance;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends MongoRepository<Insurance, String> {
    List<Insurance> findByUserId(String userId);
    Optional<Insurance> findByIdAndUserId(String id, String userId);
    List<Insurance> findByUserIdAndInsuranceType(String userId, InsuranceType type);
}