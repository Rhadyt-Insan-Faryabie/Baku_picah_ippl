package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

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
    private ImageButton btnTogglePassword;
    private boolean passwordVisible;

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

        findViewById(R.id.btnBack).setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.btnLoginMore).setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add(0, R.id.tvForgot, 0, R.string.forgot_password);
            menu.getMenu().add(0, R.id.tvRegister, 1, R.string.register);
            menu.setOnMenuItemClickListener(item -> {
                Class<?> destination = item.getItemId() == R.id.tvForgot
                        ? ForgotPasswordActivity.class : RegisterActivity.class;
                startActivity(new Intent(this, destination));
                return true;
            });
            menu.show();
        });

        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        passwordVisible = savedInstanceState != null
                && savedInstanceState.getBoolean("passwordVisible", false);
        updatePasswordVisibility();
        btnTogglePassword.setOnClickListener(v -> {
            int selectionStart = etPassword.getSelectionStart();
            int selectionEnd = etPassword.getSelectionEnd();
            passwordVisible = !passwordVisible;
            updatePasswordVisibility();
            if (selectionStart >= 0 && selectionEnd >= 0) {
                etPassword.setSelection(selectionStart, selectionEnd);
            }
        });
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (btnLogin.isEnabled()) {
                    attemptLogin();
                }
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-In belum tersedia", Toast.LENGTH_SHORT).show());
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("passwordVisible", passwordVisible);
        super.onSaveInstanceState(outState);
    }

    private void updatePasswordVisibility() {
        etPassword.setTransformationMethod(passwordVisible
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance());
        btnTogglePassword.setImageResource(passwordVisible
                ? R.drawable.bp_login_eye : R.drawable.bp_login_eye_off);
        btnTogglePassword.setContentDescription(getString(passwordVisible
                ? R.string.hide : R.string.show));
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
        btnLogin.setText(loading ? R.string.login_processing : R.string.login);
        btnLogin.setIconResource(loading ? 0 : R.drawable.bp_login_arrow_right);
    }
}
