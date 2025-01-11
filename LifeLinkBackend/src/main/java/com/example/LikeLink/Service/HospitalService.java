package com.example.LikeLink.Service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LikeLink.Exception.ResourceNotFoundException;
import com.example.LikeLink.Model.Hospital;
import com.example.LikeLink.Model.IncomingPatient;
import com.example.LikeLink.Repository.HospitalRepository;
import com.example.LikeLink.Repository.IncomingPatientRepository;
import com.example.LikeLink.dto.request.HospitalRegistrationRequest;

import ch.qos.logback.classic.Logger;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private IncomingPatientRepository incomingPatientRepository;
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HospitalService.class);

    @Transactional
    public Hospital registerHospital(String userId, HospitalRegistrationRequest request) {
        // Check if hospital already exists for this user
        if (hospitalRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Hospital already registered for this user");
        }

        Hospital hospital = new Hospital();
        hospital.setUserId(userId);
        hospital.setHospitalName(request.getHospitalName());
        hospital.setHospitalType(request.getHospitalType());
        hospital.setLicenseNumber(request.getLicenseNumber());
        hospital.setYearEstablished(request.getYearEstablished());
        hospital.setPhoneNumber(request.getPhoneNumber());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setState(request.getState());
        hospital.setPinCode(request.getPinCode());
        hospital.setLatitude(request.getLatitude());
        hospital.setLongitude(request.getLongitude());
        hospital.setTotalBeds(request.getTotalBeds());
        hospital.setIcuBeds(request.getIcuBeds());
        hospital.setEmergencyBeds(request.getEmergencyBeds());
        hospital.setHasAmbulanceService(request.isHasAmbulanceService());
        hospital.setHasEmergencyService(request.isHasEmergencyService());
        hospital.setDepartments(request.getDepartments());

        return hospitalRepository.save(hospital);
    }

    public Hospital getHospitalById(String hospitalId) {
        Optional<Hospital> hospital = hospitalRepository.findById(hospitalId);
        if(hospital.isEmpty()) return null; 
        return hospital.get();
    }
    
    public Hospital getById(String id) { 
    	Optional<Hospital> hospital = hospitalRepository.findById(id); 
    	if(hospital.isEmpty()) return null; 
    	return hospital.get();
    }

	public Hospital getHospitalByUserId(String userId) {
		Optional<Hospital> hospital = hospitalRepository.findByUserId(userId); 
		if(hospital.isEmpty()) return null; 
		return hospital.get();
	}

}