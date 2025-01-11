package com.example.wearlifelink;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.wearlifelink.adapter.EmergencyContactAdapter;
import com.example.wearlifelink.databinding.ActivityEmergencyContactBinding;
import com.example.wearlifelink.model.EmergencyContact;
import com.example.wearlifelink.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List; 
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class EmergencyContactActivity extends FragmentActivity {
    private ActivityEmergencyContactBinding binding;
    private DatabaseHelper dbHelper;
    private List<EmergencyContact> contacts = new ArrayList<>();
    private EmergencyContactAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        setupUI();
        loadContacts();
    }

    private void setupUI() {
        WearableRecyclerView recyclerView = binding.contactsRecyclerView;

        // Set circular scrolling layout
        recyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, new CustomScrollingLayoutCallback())
        );

        // Enable circular scrolling
        recyclerView.setCircularScrollingGestureEnabled(true);
        recyclerView.setBezelFraction(0.5f);
        recyclerView.setScrollDegreesPerScreen(90);

        adapter = new EmergencyContactAdapter(contacts, this::onContactClick);
        recyclerView.setAdapter(adapter);

        binding.addContactButton.setOnClickListener(v -> showAddContactDialog());
    }

    private void saveContact(EmergencyContact contact) {
        // Save to database and update UI
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(DatabaseHelper.COLUMN_PHONE, contact.getPhoneNumber());
        values.put(DatabaseHelper.COLUMN_EMAIL, contact.getEmail());
        values.put(DatabaseHelper.COLUMN_IS_PRIMARY, contact.isPrimary() ? 1 : 0);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
        
        if (id != -1) {
            contact.setId(id);
            contacts.add(contact);
            adapter.notifyItemInserted(contacts.size() - 1);
            Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
        }
    }

     private void loadContacts() {
        contacts.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_PHONE,
            DatabaseHelper.COLUMN_EMAIL,
            DatabaseHelper.COLUMN_IS_PRIMARY
        };

        Cursor cursor = db.query(
            DatabaseHelper.TABLE_CONTACTS,
            columns,
            null, null, null, null,
            DatabaseHelper.COLUMN_IS_PRIMARY + " DESC"
        );

        while (cursor.moveToNext()) {
            EmergencyContact contact = new EmergencyContact(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_PRIMARY)) == 1
            );
            contact.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
            contacts.add(contact);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
    private void onContactClick(EmergencyContact contact) {
        // Handle contact click (e.g., show edit/delete options)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] options = {"Edit", "Delete", "Set as Primary"};
        
        builder.setTitle(contact.getName())
               .setItems(options, (dialog, which) -> {
                   switch (which) {
                       case 0:
                           showEditContactDialog(contact);
                           break;
                       case 1:
                           deleteContact(contact);
                           break;
                       case 2:
                           setPrimaryContact(contact);
                           break;
                   }
               })
               .show();
    }
    private class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        private static final float MAX_ICON_PROGRESS = 0.65f;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {
            float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            float progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - progressToCenter);
            child.setScaleY(1 - progressToCenter);
        }
    }

    private void showEditContactDialog(EmergencyContact contact) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText phoneInput = dialogView.findViewById(R.id.phoneInput);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);

        // Pre-fill with existing contact data
        nameInput.setText(contact.getName());
        phoneInput.setText(contact.getPhoneNumber());
        emailInput.setText(contact.getEmail());

        new AlertDialog.Builder(this)
                .setTitle("Edit Contact")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString();
                    String phone = phoneInput.getText().toString();
                    String email = emailInput.getText().toString();

                    if (!name.isEmpty() && !phone.isEmpty()) {
                        contact.setName(name);
                        contact.setPhoneNumber(phone);
                        contact.setEmail(email);
                        updateContactInDb(contact);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteContact(EmergencyContact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + contact.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DatabaseHelper.TABLE_CONTACTS,
                            DatabaseHelper.COLUMN_ID + " = ?",
                            new String[]{String.valueOf(contact.getId())});

                    contacts.remove(contact);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setPrimaryContact(EmergencyContact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // First, remove primary status from all contacts
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_PRIMARY, 0);
        db.update(DatabaseHelper.TABLE_CONTACTS, values, null, null);

        // Set new primary contact
        values.put(DatabaseHelper.COLUMN_IS_PRIMARY, 1);
        db.update(DatabaseHelper.TABLE_CONTACTS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});

        // Update local data
        for (EmergencyContact c : contacts) {
            c.setPrimary(c.getId() == contact.getId());
        }

        adapter.notifyDataSetChanged();
        Toast.makeText(this, contact.getName() + " set as primary contact", Toast.LENGTH_SHORT).show();
    }

    // Helper method for updating contact in database
    private void updateContactInDb(EmergencyContact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(DatabaseHelper.COLUMN_PHONE, contact.getPhoneNumber());
        values.put(DatabaseHelper.COLUMN_EMAIL, contact.getEmail());

        db.update(DatabaseHelper.TABLE_CONTACTS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});

        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();

    }
    private void showAddContactDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText phoneInput = dialogView.findViewById(R.id.phoneInput);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);
        CheckBox primaryCheckbox = dialogView.findViewById(R.id.primaryCheckbox);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Contact")
                .setView(dialogView)
                .setPositiveButton("Add", null)  // We'll set this later to prevent auto-dismiss
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = nameInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                boolean isPrimary = primaryCheckbox.isChecked();

                // Validate inputs
                if (name.isEmpty()) {
                    nameInput.setError("Name is required");
                    return;
                }
                if (phone.isEmpty()) {
                    phoneInput.setError("Phone number is required");
                    return;
                }

                // Create and save new contact
                EmergencyContact newContact = new EmergencyContact(name, phone, email, isPrimary);
                saveContact(newContact);
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}