package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.MainActivity;
import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.AuthApi;
import com.fight.app.data.model.AuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Login screen backed by POST /api/auth/login.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnGoogle = findViewById(R.id.btnGoogle);
        TextView tvForgot = findViewById(R.id.tvForgot);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-In belum tersedia", Toast.LENGTH_SHORT).show());
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        AuthApi.login(email, password, new ApiCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data) {
                setLoading(false);
                SessionManager.save(LoginActivity.this, data.accessToken,
                        data.fighter.id, data.fighter.username, data.fighter.fullName);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Memproses..." : getString(R.string.login));
    }
}
