package com.hospital.lifelinkhospitals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.hospital.lifelinkhospitals.Util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);


        LottieAnimationView animationView = findViewById(R.id.animationView);
        animationView.playAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if user is already logged in
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, Dashboard.class));
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
