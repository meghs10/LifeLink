package com.example.LikeLink.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.LikeLink.Model.BloodRequest;
import java.util.List;
@Repository
public interface BloodRequestRepository extends MongoRepository<BloodRequest, String> {
    List<BloodRequest> findByHospitalId(String hospitalId);
    List<BloodRequest> findByStatus(String status);
    List<BloodRequest> findByHospitalIdAndStatus(String hospitalId, String status);
}
