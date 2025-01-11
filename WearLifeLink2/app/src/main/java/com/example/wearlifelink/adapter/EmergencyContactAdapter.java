package com.example.wearlifelink.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wearlifelink.model.EmergencyContact;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder> {
    private List<EmergencyContact> contacts;
    private OnContactClickListener listener;

    public interface OnContactClickListener {
        void onContactClick(EmergencyContact contact);
    }

    public EmergencyContactAdapter(List<EmergencyContact> contacts, OnContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact contact = contacts.get(position);
        holder.text1.setText(contact.getName());
        holder.text2.setText(contact.getPhoneNumber());
        if (contact.isPrimary()) {
            holder.text1.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onContactClick(contacts.get(position));
                }
            });
        }
    }
}
