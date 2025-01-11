package com.hospital.lifelinkhospitals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.lifelinkhospitals.R;
import com.hospital.lifelinkhospitals.model.EmergencyContactResponse;
import com.hospital.lifelinkhospitals.model.IncomingPatient;
import com.hospital.lifelinkhospitals.model.PatientResponse;
import java.util.ArrayList;
import java.util.List;

public class IncomingPatientsAdapter extends RecyclerView.Adapter<IncomingPatientsAdapter.ViewHolder> {
    private List<IncomingPatient> patients = new ArrayList<>();
    private final OnPatientClickListener listener;
    private final Context context;

    public interface OnPatientClickListener {
        void onPatientClick(IncomingPatient patient);
    }

    public IncomingPatientsAdapter(OnPatientClickListener listener) {
        this.listener = listener;
        this.context = listener instanceof Context ? (Context) listener : null;
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
        holder.bind(patient);
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public void setPatients(List<IncomingPatient> patients) {
        this.patients = patients != null ? patients : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPatientClick(patients.get(position));
                }
            });
        }

        void bind(IncomingPatient patient) {
            if (patient == null) return;

            PatientResponse details = patient.getPatientDetails();
            if (details != null) {
                // Set patient name
                patientName.setText(details.getFullName());
                patientName.setVisibility(View.VISIBLE);

                // Set age and gender info
                String ageGenderInfo = String.format("%d years | %s | %s",
                        details.getAge(),
                        details.getGender(),
                        details.getBloodType());
                patientAgeGender.setText(ageGenderInfo);
                patientAgeGender.setVisibility(View.VISIBLE);

                // Set physical details
                String physicalInfo = String.format("Height: %.1f cm | Weight: %.1f kg",
                        details.getHeight(),
                        details.getWeight());
                patientPhysicalDetails.setText(physicalInfo);
                patientPhysicalDetails.setVisibility(View.VISIBLE);

                // Set vitals if available
                if (details.getCurrentMedications() != null && !details.getCurrentMedications().isEmpty()) {
                    StringBuilder medsInfo = new StringBuilder("Current Medications:\n");
                    details.getCurrentMedications().forEach(med -> 
                        medsInfo.append("• ").append(med.getMedicationName())
                               .append(" (").append(med.getDosage()).append(")\n")
                    );
                    patientVitals.setText(medsInfo.toString());
                    patientVitals.setVisibility(View.VISIBLE);
                } else {
                    patientVitals.setVisibility(View.GONE);
                }

                // Set allergies if available
                if (details.getAllergies() != null && !details.getAllergies().isEmpty()) {
                    String allergiesInfo = "Allergies: " + String.join(", ", details.getAllergies());
                    patientContact.setText(allergiesInfo);
                    patientContact.setVisibility(View.VISIBLE);
                } else {
                    patientContact.setVisibility(View.GONE);
                }

                // Set emergency contact if available
                if (details.getEmergencyContacts() != null && !details.getEmergencyContacts().isEmpty()) {
                    EmergencyContactResponse contact = details.getEmergencyContacts().get(0);
                    String contactInfo = String.format("Emergency: %s (%s)",
                            contact.getContactName(),
                            contact.getPhoneNumber());
                    emergencyContact.setText(contactInfo);
                    emergencyContact.setVisibility(View.VISIBLE);
                } else {
                    emergencyContact.setVisibility(View.GONE);
                }
            } else {
                // Show loading or placeholder state
                patientName.setText("Loading patient information...");
                patientAgeGender.setVisibility(View.GONE);
                patientVitals.setVisibility(View.GONE);
                patientPhysicalDetails.setVisibility(View.GONE);
                patientContact.setVisibility(View.GONE);
                emergencyContact.setVisibility(View.GONE);
            }
        }
    }
} 