package com.example.LikeLink.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.LikeLink.Model.IncomingPatient;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomingPatientRepository extends MongoRepository<IncomingPatient, String> {
    Optional<IncomingPatient> findByUserId(String userId);
    List<IncomingPatient> findIncomingPatientsById(String Id); 
    List<IncomingPatient> findByHospitalId(String id);
}