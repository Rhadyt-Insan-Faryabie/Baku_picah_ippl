package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.AuthApi;
import com.fight.app.data.model.AuthResponse;
import com.fight.app.data.model.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

/**
 * Registration screen backed by POST /api/auth/register.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || password.length() < 6) {
            Toast.makeText(this, "Lengkapi data (password min. 6 karakter)", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = deriveUsername(email);
        RegisterRequest body = new RegisterRequest(username, email, password, name);

        setLoading(true);
        AuthApi.register(body, new ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data) {
                setLoading(false);
                SessionManager.save(RegisterActivity.this, data.accessToken,
                        data.fighter.id, data.fighter.username, data.fighter.fullName);
                startActivity(new Intent(RegisterActivity.this, ProfileSetupActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Use the email local-part as a default username (backend enforces uniqueness). */
    private String deriveUsername(String email) {
        String local = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        String sanitized = local.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase(Locale.ROOT);
        if (sanitized.length() < 3) {
            sanitized = "fighter_" + sanitized;
        }
        return sanitized.length() > 50 ? sanitized.substring(0, 50) : sanitized;
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Memproses..." : getString(R.string.register));
    }
}
