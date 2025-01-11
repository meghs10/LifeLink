package com.hospital.lifelinkhospitals;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hospital.lifelinkhospitals.R;
import com.hospital.lifelinkhospitals.model.EmergencyContactResponse;
import com.hospital.lifelinkhospitals.model.IncomingPatient;
import com.hospital.lifelinkhospitals.model.MedicationResponse;
import com.hospital.lifelinkhospitals.model.PatientResponse;

import java.util.ArrayList;
import java.util.List;

public class IncomingPatientsAdapter extends RecyclerView.Adapter<IncomingPatientsAdapter.ViewHolder> {
    private static final String TAG = "IncomingPatientsAdapter";
    private List<IncomingPatient> patients = new ArrayList<>();
    private final OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(IncomingPatient patient);
    }

    public IncomingPatientsAdapter(OnPatientClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incoming_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncomingPatient patient = patients.get(position);
        holder.bind(patient, listener);
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public void setPatients(List<IncomingPatient> patients) {
        this.patients = patients;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView patientName;
        private final TextView patientAgeGender;
        private final TextView patientVitals;
        private final TextView patientPhysicalDetails;
        private final TextView patientContact;
        private final TextView emergencyContact;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.patientCard);
            patientName = itemView.findViewById(R.id.patientName);
            patientAgeGender = itemView.findViewById(R.id.patientAgeGender);
            patientVitals = itemView.findViewById(R.id.patientVitals);
            patientPhysicalDetails = itemView.findViewById(R.id.patientPhysicalDetails);
            patientContact = itemView.findViewById(R.id.patientContact);
            emergencyContact = itemView.findViewById(R.id.emergencyContact);
        }

        void bind(IncomingPatient patient, OnPatientClickListener listener) {
            if (patient != null) {
                PatientResponse details = patient.getPatientDetails();
                if (details != null) {
                    // Set patient name
                    patientName.setText(details.getFullName());

                    // Set age and gender
                    patientAgeGender.setText(String.format("Age: %d | Gender: %s | Blood Type: %s",
                            details.getAge(),
                            details.getGender(),
                            details.getBloodType()));

                    // Set physical details
                    patientPhysicalDetails.setText(String.format("Height: %.1f cm | Weight: %.1f kg",
                            details.getHeight(),
                            details.getWeight()));

                    // Set emergency contact if available
                    if (details.getEmergencyContacts() != null && !details.getEmergencyContacts().isEmpty()) {
                        EmergencyContactResponse primaryContact = details.getEmergencyContacts().get(0);
                        emergencyContact.setText(String.format("Emergency Contact: %s (%s)",
                                primaryContact.getContactName(),
                                primaryContact.getPhoneNumber()));
                        emergencyContact.setVisibility(View.VISIBLE);
                    } else {
                        emergencyContact.setVisibility(View.GONE);
                    }

                    // Set medications if available
                    if (details.getCurrentMedications() != null && !details.getCurrentMedications().isEmpty()) {
                        StringBuilder medicationsText = new StringBuilder("Current Medications:\n");
                        for (MedicationResponse med : details.getCurrentMedications()) {
                            medicationsText.append(String.format("• %s (%s)\n",
                                    med.getMedicationName(),
                                    med.getDosage()));
                        }
                        patientVitals.setText(medicationsText.toString());
                        patientVitals.setVisibility(View.VISIBLE);
                    } else {
                        patientVitals.setVisibility(View.GONE);
                    }

                    // Set allergies if available
                    if (details.getAllergies() != null && !details.getAllergies().isEmpty()) {
                        patientContact.setText("Allergies: " + TextUtils.join(", ", details.getAllergies()));
                        patientContact.setVisibility(View.VISIBLE);
                    } else {
                        patientContact.setVisibility(View.GONE);
                    }
                } else {
                    // If no details available, show minimal information
                    patientName.setText("Patient ID: " + patient.getId());
                    patientAgeGender.setText("User ID: " + patient.getUserId());
                    patientVitals.setVisibility(View.GONE);
                    patientPhysicalDetails.setVisibility(View.GONE);
                    patientContact.setVisibility(View.GONE);
                    emergencyContact.setVisibility(View.GONE);
                }

                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPatientClick(patient);
                    }
                });
            }
        }
    }
}