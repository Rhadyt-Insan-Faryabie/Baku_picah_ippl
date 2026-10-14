package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Onboarding / welcome screen.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MaterialButton btnStart = findViewById(R.id.btnStart);
        MaterialButton btnLogin = findViewById(R.id.btnToLogin);

        btnStart.setOnClickListener(v -> startActivity(new Intent(this, ProfileSetupActivity.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
