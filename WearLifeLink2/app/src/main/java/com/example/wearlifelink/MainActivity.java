package com.example.wearlifelink;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.example.wearlifelink.databinding.ActivityMainBinding;

public class MainActivity extends FragmentActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}