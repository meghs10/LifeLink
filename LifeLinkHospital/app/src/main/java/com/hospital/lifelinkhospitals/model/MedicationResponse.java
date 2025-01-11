package com.hospital.lifelinkhospitals.model;

public class MedicationResponse {
    private String medicationName;
    private String dosage;

    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String name) { this.medicationName = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
}
