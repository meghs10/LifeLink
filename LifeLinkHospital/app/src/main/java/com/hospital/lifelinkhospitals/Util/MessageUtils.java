package com.hospital.lifelinkhospitals.Util;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class MessageUtils {
    public static void showSuccess(TextView messageView, String message) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setTextColor(Color.parseColor("#00A172")); // surgical_green
        messageView.setText(message);
    }

    public static void showError(TextView messageView, String message) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setTextColor(Color.RED);
        messageView.setText(message);
    }

    public static void hideMessage(TextView messageView) {
        messageView.setVisibility(View.GONE);
        messageView.setText("");
    }
}
