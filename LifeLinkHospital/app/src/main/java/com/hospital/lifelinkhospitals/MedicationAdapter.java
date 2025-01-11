package com.hospital.lifelinkhospitals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hospital.lifelinkhospitals.model.MedicationResponse;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {
    private List<MedicationResponse> medications = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(medications.get(position));
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public void setMedications(List<MedicationResponse> medications) {
        this.medications = medications != null ? medications : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView dosageText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.medicationName);
            dosageText = itemView.findViewById(R.id.medicationDosage);
        }

        void bind(MedicationResponse medication) {
            nameText.setText(medication.getMedicationName());
            dosageText.setText(medication.getDosage());
        }
    }
}
