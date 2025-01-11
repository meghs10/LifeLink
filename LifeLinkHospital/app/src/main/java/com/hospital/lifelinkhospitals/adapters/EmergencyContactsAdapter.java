package com.hospital.lifelinkhospitals.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.lifelinkhospitals.R;
import com.hospital.lifelinkhospitals.model.EmergencyContactResponse;
import java.util.ArrayList;
import java.util.List;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.ViewHolder> {
    private List<EmergencyContactResponse> contacts = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContactResponse contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<EmergencyContactResponse> contacts) {
        this.contacts = contacts != null ? contacts : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView phoneText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.contactName);
            phoneText = itemView.findViewById(R.id.contactPhone);
        }

        void bind(EmergencyContactResponse contact) {
            nameText.setText(contact.getContactName());
            phoneText.setText(contact.getPhoneNumber());
        }
    }
}