package com.fight.app.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Password reset request screen.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        MaterialButton btnSend = findViewById(R.id.btnSend);
        TextView tvBack = findViewById(R.id.tvBack);

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Masukkan emailmu", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: call /api/auth/forgot-password.
            Toast.makeText(this, "Tautan reset dikirim ke " + email, Toast.LENGTH_LONG).show();
            finish();
        });

        tvBack.setOnClickListener(v -> finish());
    }
}
