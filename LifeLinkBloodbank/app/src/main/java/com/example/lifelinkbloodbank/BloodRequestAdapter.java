package com.example.lifelinkbloodbank;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifelinkbloodbank.R;
import com.example.lifelinkbloodbank.model.BloodRequestUI;
import com.google.android.material.button.MaterialButton;

public class BloodRequestAdapter extends ListAdapter<BloodRequestUI, BloodRequestAdapter.ViewHolder> {

    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAcceptRequest(BloodRequestUI request);
        void onRejectRequest(BloodRequestUI request);
    }

    public BloodRequestAdapter(OnRequestActionListener listener) {
        super(new DiffUtil.ItemCallback<BloodRequestUI>() {
            @Override
            public boolean areItemsTheSame(@NonNull BloodRequestUI oldItem,
                                           @NonNull BloodRequestUI newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull BloodRequestUI oldItem,
                                              @NonNull BloodRequestUI newItem) {
                return oldItem.getStatus().equals(newItem.getStatus()) &&
                        oldItem.getHospitalName().equals(newItem.getHospitalName());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_request, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hospitalName;
        private final TextView bloodType;
        private final TextView address;
        private final TextView phoneNumber;
        ViewHolder(View itemView, OnRequestActionListener listener) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            bloodType = itemView.findViewById(R.id.bloodType);
            address = itemView.findViewById(R.id.address);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);


        }

        void bind(BloodRequestUI request) {
            // Set text fields
            hospitalName.setText(request.getHospitalName());
            bloodType.setText(request.getBloodTypeDisplay());
            address.setText(request.getAddress());
            phoneNumber.setText(request.getContactNumber());

            // Handle button visibility based on status
            boolean isPending = request.isPending();

            // Style blood type background
            bloodType.setBackgroundResource(R.drawable.bg_blood_type);

            // Set icons for address and phone
            address.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_location, 0, 0, 0);
            phoneNumber.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_phone, 0, 0, 0);

        }

        private BloodRequestUI getItem(int position) {
            return ((BloodRequestAdapter) getBindingAdapter()).getCurrentList().get(position);
        }
    }
}