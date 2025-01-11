package com.hospital.lifelinkhospitals.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.lifelinkhospitals.R;
import com.hospital.lifelinkhospitals.model.PastSurgeryResponse;
import java.util.ArrayList;
import java.util.List;

public class PastSurgeriesAdapter extends RecyclerView.Adapter<PastSurgeriesAdapter.ViewHolder> {
    private List<PastSurgeryResponse> surgeries = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_surgery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PastSurgeryResponse surgery = surgeries.get(position);
        holder.bind(surgery);
    }

    @Override
    public int getItemCount() {
        return surgeries.size();
    }

    public void setSurgeries(List<PastSurgeryResponse> surgeries) {
        this.surgeries = surgeries != null ? surgeries : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView surgeryNameText;
        private final TextView surgeryDateText;

        ViewHolder(View itemView) {
            super(itemView);
            surgeryNameText = itemView.findViewById(R.id.surgeryName);
            surgeryDateText = itemView.findViewById(R.id.surgeryDate);
        }

        void bind(PastSurgeryResponse surgery) {
            surgeryNameText.setText(surgery.getSurgeryType());
            surgeryDateText.setText(surgery.getApproximateDate());
        }
    }
}