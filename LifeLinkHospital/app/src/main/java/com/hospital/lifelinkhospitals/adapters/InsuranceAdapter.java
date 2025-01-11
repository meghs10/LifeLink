package com.hospital.lifelinkhospitals.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.lifelinkhospitals.R;
import com.hospital.lifelinkhospitals.model.InsuranceResponse;
import java.util.ArrayList;
import java.util.List;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.ViewHolder> {
    private List<InsuranceResponse> insurances = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_insurance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InsuranceResponse insurance = insurances.get(position);
        holder.bind(insurance);
    }

    @Override
    public int getItemCount() {
        return insurances.size();
    }

    public void setInsurances(List<InsuranceResponse> insurances) {
        this.insurances = insurances != null ? insurances : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView insuranceProviderText;
        private final TextView policyNumberText;
        private final TextView groupNumberText;
        private final TextView policyHolderText;

        ViewHolder(View itemView) {
            super(itemView);
            insuranceProviderText = itemView.findViewById(R.id.insuranceProvider);
            policyNumberText = itemView.findViewById(R.id.policyNumber);
            groupNumberText = itemView.findViewById(R.id.groupNumber);
            policyHolderText = itemView.findViewById(R.id.policyHolder);
        }

        void bind(InsuranceResponse insurance) {
            insuranceProviderText.setText(insurance.getInsuranceProviderName());
            policyNumberText.setText("Policy #: " + insurance.getPolicyNumber());
            groupNumberText.setText("Group #: " + insurance.getGroupNumber());
            policyHolderText.setText("Policy Holder: " + insurance.getPolicyHolderName());
        }
    }
} 