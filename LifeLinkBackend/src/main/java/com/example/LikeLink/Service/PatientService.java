package com.example.LikeLink.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LikeLink.Exception.ResourceNotFoundException;
import com.example.LikeLink.Model.EmergencyContact;
import com.example.LikeLink.Model.Medication;
import com.example.LikeLink.Model.PastSurgery;
import com.example.LikeLink.Model.Patient;
import com.example.LikeLink.Repository.PatientRepository;
import com.example.LikeLink.Repository.UserRepository;
import com.example.LikeLink.dto.request.EmergencyContactDto;
import com.example.LikeLink.dto.request.MedicationDto;
import com.example.LikeLink.dto.request.PastSurgeryDto;
import com.example.LikeLink.dto.request.PatientRequest;
import com.example.LikeLink.dto.response.EmergencyContactResponse;
import com.example.LikeLink.dto.response.MedicationResponse;
import com.example.LikeLink.dto.response.PastSurgeryResponse;
import com.example.LikeLink.dto.response.PatientResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientResponse createProfile(PatientRequest request, String email) {
        log.debug("Creating patient profile for email: {}", email);

        userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (patientRepository.existsByEmail(email)) {
            throw new IllegalStateException("Profile already exists for user with email: " + email);
        }

        Patient patient = Patient.builder()
            .fullName(request.getFullName())
            .age(request.getAge())
            .gender(request.getGender())
            .emergencyContacts(request.getEmergencyContacts().stream()
                .map(this::mapToEmergencyContact)
                .collect(Collectors.toList()))
            .medicalHistory(request.getMedicalHistory())
            .pastSurgeries(request.getPastSurgeries().stream()
                .map(this::mapToPastSurgery)
                .collect(Collectors.toList()))
            .currentMedications(request.getCurrentMedications().stream()
                .map(this::mapToMedication)
                .collect(Collectors.toList()))
            .allergies(request.getAllergies())
            .bloodType(request.getBloodType())
            .weight(request.getWeight())
            .height(request.getHeight())
            .dietaryRestrictions(request.getDietaryRestrictions())
            .organDonor(request.isOrganDonor())
            .culturalConsiderations(request.getCulturalConsiderations())
            .email(email)
            .build();

        Patient savedPatient = patientRepository.save(patient);
        log.info("Created patient profile for email: {}", email);
        
        return mapToPatientResponse(savedPatient);
    }

    public PatientResponse getProfile(String email) {
        log.debug("Fetching patient profile for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));
            
        return mapToPatientResponse(patient);
    }

    @Transactional
    public PatientResponse updateProfile(PatientRequest request, String email) {
        log.debug("Updating patient profile for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        patient.setFullName(request.getFullName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setEmergencyContacts(request.getEmergencyContacts().stream()
            .map(this::mapToEmergencyContact)
            .collect(Collectors.toList()));
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setPastSurgeries(request.getPastSurgeries().stream()
            .map(this::mapToPastSurgery)
            .collect(Collectors.toList()));
        patient.setCurrentMedications(request.getCurrentMedications().stream()
            .map(this::mapToMedication)
            .collect(Collectors.toList()));
        patient.setAllergies(request.getAllergies());
        patient.setBloodType(request.getBloodType());
        patient.setWeight(request.getWeight());
        patient.setHeight(request.getHeight());
        patient.setDietaryRestrictions(request.getDietaryRestrictions());
        patient.setOrganDonor(request.isOrganDonor());
        patient.setCulturalConsiderations(request.getCulturalConsiderations());

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Updated patient profile for email: {}", email);
        
        return mapToPatientResponse(updatedPatient);
    }

    private EmergencyContact mapToEmergencyContact(EmergencyContactDto dto) {
        return EmergencyContact.builder()
            .contactName(dto.getContactName())
            .phoneNumber(dto.getPhoneNumber())
            .build();
    }

    private PastSurgery mapToPastSurgery(PastSurgeryDto dto) {
        return PastSurgery.builder()
            .surgeryType(dto.getSurgeryType())
            .approximateDate(dto.getApproximateDate())
            .build();
    }

    private Medication mapToMedication(MedicationDto dto) {
        return Medication.builder()
            .medicationName(dto.getMedicationName())
            .dosage(dto.getDosage())
            .build();
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        if (patient == null) return null;
        
        return PatientResponse.builder()
            .id(patient.getId())
            .fullName(patient.getFullName())
            .age(patient.getAge())
            .gender(patient.getGender())
            .emergencyContacts(mapToEmergencyContactResponses(patient.getEmergencyContacts()))
            .medicalHistory(patient.getMedicalHistory())
            .pastSurgeries(mapToPastSurgeryResponses(patient.getPastSurgeries()))
            .currentMedications(mapToMedicationResponses(patient.getCurrentMedications()))
            .allergies(patient.getAllergies())
            .bloodType(patient.getBloodType())
            .weight(patient.getWeight())
            .height(patient.getHeight())
            .dietaryRestrictions(patient.getDietaryRestrictions())
            .organDonor(patient.isOrganDonor())
            .culturalConsiderations(patient.getCulturalConsiderations())
            .createdAt(formatDateTime(patient.getCreatedAt()))
            .lastUpdatedAt(formatDateTime(patient.getLastUpdatedAt()))
            .build();
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private List<EmergencyContactResponse> mapToEmergencyContactResponses(List<EmergencyContact> contacts) {
        if (contacts == null) return null;
        return contacts.stream()
            .map(contact -> EmergencyContactResponse.builder()
                .contactName(contact.getContactName())
                .phoneNumber(contact.getPhoneNumber())
                .build())
            .collect(Collectors.toList());
    }

    private List<PastSurgeryResponse> mapToPastSurgeryResponses(List<PastSurgery> surgeries) {
        if (surgeries == null) return null;
        return surgeries.stream()
            .map(surgery -> PastSurgeryResponse.builder()
                .surgeryType(surgery.getSurgeryType())
                .approximateDate(surgery.getApproximateDate())
                .build())
            .collect(Collectors.toList());
    }

    private List<MedicationResponse> mapToMedicationResponses(List<Medication> medications) {
        if (medications == null) return null;
        return medications.stream()
            .map(medication -> MedicationResponse.builder()
                .medicationName(medication.getMedicationName())
                .dosage(medication.getDosage())
                .build())
            .collect(Collectors.toList());
    }
    @Transactional
    public PatientResponse updateEmergencyContacts(List<EmergencyContactDto> contacts, String email) {
        log.debug("Updating emergency contacts for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        List<EmergencyContact> updatedContacts = contacts.stream()
            .map(this::mapToEmergencyContact)
            .collect(Collectors.toList());
        
        patient.setEmergencyContacts(updatedContacts);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated emergency contacts for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updateMedications(List<MedicationDto> medications, String email) {
        log.debug("Updating medications for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        List<Medication> updatedMedications = medications.stream()
            .map(this::mapToMedication)
            .collect(Collectors.toList());
        
        patient.setCurrentMedications(updatedMedications);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated medications for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updatePastSurgeries(List<PastSurgeryDto> surgeries, String email) {
        log.debug("Updating past surgeries for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        List<PastSurgery> updatedSurgeries = surgeries.stream()
            .map(this::mapToPastSurgery)
            .collect(Collectors.toList());
        
        patient.setPastSurgeries(updatedSurgeries);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated past surgeries for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updateAllergies(List<String> allergies, String email) {
        log.debug("Updating allergies for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        patient.setAllergies(allergies);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated allergies for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updateMedicalHistory(List<String> medicalHistory, String email) {
        log.debug("Updating medical history for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        patient.setMedicalHistory(medicalHistory);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated medical history for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updateDietaryRestrictions(List<String> restrictions, String email) {
        log.debug("Updating dietary restrictions for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        patient.setDietaryRestrictions(restrictions);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated dietary restrictions for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updateCulturalConsiderations(List<String> considerations, String email) {
        log.debug("Updating cultural considerations for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        patient.setCulturalConsiderations(considerations);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated cultural considerations for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    }

    @Transactional
    public PatientResponse updateOrganDonorStatus(boolean isOrganDonor, String email) {
        log.debug("Updating organ donor status for email: {}", email);
        
        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));

        patient.setOrganDonor(isOrganDonor);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Updated organ donor status for email: {}", email);
        return mapToPatientResponse(updatedPatient);
    } 
    
    @Transactional
    public PatientResponse getById(String id) { 
    	 Patient patient = patientRepository.findById(id)
    	            .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + id));

    	        return mapToPatientResponse(patient);
    }
}