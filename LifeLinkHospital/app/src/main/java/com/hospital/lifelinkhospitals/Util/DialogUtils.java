package com.hospital.lifelinkhospitals.Util;

import android.content.Context;
import android.graphics.Color;

import android.view.View;



import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;


import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtils {
    private static AlertDialog loadingDialog;

    public static void showSuccessDialog(Context context, String title, String message, Runnable onDismiss) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public static void showErrorDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showLoadingDialog(Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Please wait");
        builder.setMessage("Processing...");
        builder.setCancelable(false);
        loadingDialog = builder.show();
    }

    public static void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public static void showSnackbar(View view, String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(Color.RED);
            snackbar.setTextColor(Color.WHITE);
        }
        snackbar.show();
    }
}