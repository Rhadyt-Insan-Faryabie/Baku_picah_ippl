package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.MainActivity;
import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Multi-step profile setup (weight, height, discipline, record, gym).
 * Single-screen placeholder for now.
 */
public class ProfileSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            // TODO: persist via /api/fighters/me then continue.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
