package com.example.lifelinkbloodbank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lifelinkbloodbank.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView appTitle;
    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blood_red));

        setContentView(R.layout.activity_main);

        initializeViews();
        setupSplashAnimation();
    }

    private void initializeViews() {
        animationView = findViewById(R.id.animationView);
        appTitle = findViewById(R.id.appTitle);

        // Initially hide the title
        appTitle.setVisibility(View.INVISIBLE);
    }

    private void setupSplashAnimation() {
        // Set up Lottie animation
        animationView.setAnimation(R.raw.bloodbank);
        animationView.playAnimation();

        // Handle animation completion
        animationView.addAnimatorListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                animateTitle();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        // Fallback navigation
        new Handler().postDelayed(this::navigateToNextScreen, 4000);
    }

    private void animateTitle() {
        appTitle.setVisibility(View.VISIBLE);
        appTitle.setAlpha(0f);
        appTitle.animate()
                .alpha(1f)
                .setDuration(1000)
                .withEndAction(this::navigateToNextScreen)
                .start();
    }

    private void navigateToNextScreen() {
        if (!isNavigating) {
            isNavigating = true;

            SessionManager sessionManager = new SessionManager(this);
            Intent intent;

            if (sessionManager.isLoggedIn()) {
                if ("BLOOD_BANK".equals(sessionManager.getRole())) {
                    intent = new Intent(this, BloodBankDashboard.class);
                } else {
                    intent = new Intent(this, Login.class);
                }
            } else {
                intent = new Intent(this, Login.class);
            }

            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}