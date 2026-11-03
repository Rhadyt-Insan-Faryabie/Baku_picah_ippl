package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.AuthApi;
import com.fight.app.data.model.AuthResponse;
import com.fight.app.data.model.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.regex.Pattern;

/** Registration screen backed by POST /api/auth/register. */
public class RegisterActivity extends AppCompatActivity {

    private static final Pattern ASCII_LETTER = Pattern.compile("[A-Za-z]");
    private static final Pattern ASCII_DIGIT = Pattern.compile("[0-9]");
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialCheckBox cbConsent;
    private MaterialButton btnRegister;
    private MaterialButton btnGoogle;
    private ImageButton btnTogglePassword;
    private ImageButton btnToggleConfirmPassword;
    private boolean passwordVisible;
    private boolean confirmPasswordVisible;
    private RegisterState requestState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbConsent = findViewById(R.id.cbConsent);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        requestState = new ViewModelProvider(this).get(RegisterState.class);

        passwordVisible = savedInstanceState != null
                && savedInstanceState.getBoolean("passwordVisible", false);
        confirmPasswordVisible = savedInstanceState != null
                && savedInstanceState.getBoolean("confirmPasswordVisible", false);
        updatePasswordVisibility();
        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            updatePasswordVisibility();
        });
        btnToggleConfirmPassword.setOnClickListener(v -> {
            confirmPasswordVisible = !confirmPasswordVisible;
            updatePasswordVisibility();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // The server may create the account even if the user leaves mid-request.
                if (Boolean.TRUE.equals(requestState.loading.getValue())) {
                    Toast.makeText(RegisterActivity.this,
                            R.string.register_wait_for_request, Toast.LENGTH_SHORT).show();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.btnRegisterMore).setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add(0, 1, 0, R.string.register_login);
            menu.getMenu().add(0, 2, 1, R.string.register_forgot_password);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    navigateToLogin();
                } else {
                    startActivity(new Intent(this, ForgotPasswordActivity.class));
                }
                return true;
            });
            menu.show();
        });
        findViewById(R.id.tvLogin).setOnClickListener(v -> navigateToLogin());
        btnRegister.setOnClickListener(v -> attemptRegister());
        btnGoogle.setOnClickListener(v -> Toast.makeText(this,
                R.string.register_google_unavailable, Toast.LENGTH_SHORT).show());
        cbConsent.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                cbConsent.setError(null);
                cbConsent.setErrorShown(false);
            }
        });
        etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });

        requestState.loading.observe(this, loading -> {
            setLoading(Boolean.TRUE.equals(loading));
            AuthResponse response = requestState.response;
            requestState.response = null;
            if (response != null) {
                SessionManager.save(this, response.accessToken,
                        response.fighter.id, response.fighter.username, response.fighter.fullName);
                startActivity(new Intent(this, ProfileSetupActivity.class));
                finish();
            } else if (requestState.failed) {
                requestState.failed = false;
                String message = requestState.error;
                requestState.error = null;
                Toast.makeText(this, TextUtils.isEmpty(message)
                        ? getString(R.string.register_error_request) : message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("passwordVisible", passwordVisible);
        outState.putBoolean("confirmPasswordVisible", confirmPasswordVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updatePasswordVisibility();
        setLoading(Boolean.TRUE.equals(requestState.loading.getValue()));
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    private void updatePasswordVisibility() {
        applyPasswordVisibility(etPassword, btnTogglePassword, passwordVisible,
                R.string.register_show_password, R.string.register_hide_password);
        applyPasswordVisibility(etConfirmPassword, btnToggleConfirmPassword, confirmPasswordVisible,
                R.string.register_show_confirm_password, R.string.register_hide_confirm_password);
    }

    private void applyPasswordVisibility(TextInputEditText field, ImageButton toggle,
                                         boolean visible, int showDescription, int hideDescription) {
        int start = field.getSelectionStart();
        int end = field.getSelectionEnd();
        field.setTransformationMethod(visible
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance());
        toggle.setImageResource(visible ? R.drawable.bp_login_eye : R.drawable.bp_login_eye_off);
        toggle.setContentDescription(getString(visible ? hideDescription : showDescription));
        if (start >= 0 && end >= 0) {
            field.setSelection(start, end);
        }
    }

    private void attemptRegister() {
        if (Boolean.TRUE.equals(requestState.loading.getValue())) {
            return;
        }
        String name = text(etName).trim();
        String email = text(etEmail).trim();
        String password = text(etPassword);
        String confirmation = text(etConfirmPassword);
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        cbConsent.setError(null);
        cbConsent.setErrorShown(false);

        View firstInvalid = null;
        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.register_error_name));
            firstInvalid = etName;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(TextUtils.isEmpty(email)
                    ? R.string.register_error_email_required : R.string.register_error_email_invalid));
            if (firstInvalid == null) firstInvalid = etEmail;
        }
        int passwordError = 0;
        if (TextUtils.isEmpty(password)) {
            passwordError = R.string.register_error_password_required;
        } else if (password.codePointCount(0, password.length()) < 8
                        || !ASCII_LETTER.matcher(password).find()
                || !ASCII_DIGIT.matcher(password).find()) {
            passwordError = R.string.register_error_password_invalid;
        } else if (password.codePointCount(0, password.length()) > 128) {
            passwordError = R.string.register_error_password_length;
        }
        if (passwordError != 0) {
            etPassword.setError(getString(passwordError));
            if (firstInvalid == null) firstInvalid = etPassword;
        }
        if (TextUtils.isEmpty(confirmation) || !password.equals(confirmation)) {
            etConfirmPassword.setError(getString(TextUtils.isEmpty(confirmation)
                    ? R.string.register_error_confirm_required : R.string.register_error_confirm_mismatch));
            if (firstInvalid == null) firstInvalid = etConfirmPassword;
        }
        // This is a client declaration, not age verification or server-recorded consent.
        if (!cbConsent.isChecked()) {
            cbConsent.setError(getString(R.string.register_error_consent));
            cbConsent.setErrorAccessibilityLabel(getString(R.string.register_error_consent));
            cbConsent.setErrorShown(true);
            if (firstInvalid == null) firstInvalid = cbConsent;
        }
        if (firstInvalid != null) {
            firstInvalid.requestFocus();
            if (firstInvalid == cbConsent) {
                cbConsent.announceForAccessibility(getString(R.string.register_error_consent));
            }
            return;
        }

        requestState.register(new RegisterRequest(deriveUsername(email), email, password, name));
    }

    private String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
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
        etName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        etConfirmPassword.setEnabled(!loading);
        cbConsent.setEnabled(!loading);
        btnTogglePassword.setEnabled(!loading);
        btnToggleConfirmPassword.setEnabled(!loading);
        btnGoogle.setEnabled(!loading);
        btnRegister.setEnabled(!loading);
        findViewById(R.id.btnBack).setEnabled(!loading);
        findViewById(R.id.tvLogin).setEnabled(!loading);
        findViewById(R.id.btnRegisterMore).setEnabled(!loading);
        btnRegister.setText(loading ? R.string.register_processing : R.string.register_submit);
        btnRegister.setIconResource(loading ? 0 : R.drawable.bp_login_arrow_right);
    }

    /** Retain an in-flight request across rotation without retaining an Activity. */
    public static class RegisterState extends ViewModel {
        final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
        AuthResponse response;
        String error;
        boolean failed;
        private boolean cleared;

        void register(RegisterRequest body) {
            if (Boolean.TRUE.equals(loading.getValue())) return;
            loading.setValue(true);
            AuthApi.register(body, new ApiCallback<AuthResponse>() {
                @Override
                public void onSuccess(AuthResponse data) {
                    if (cleared) return;
                    response = data;
                    loading.setValue(false);
                }

                @Override
                public void onError(String message) {
                    if (cleared) return;
                    error = message;
                    failed = true;
                    loading.setValue(false);
                }
            });
        }

        @Override
        protected void onCleared() {
            cleared = true;
        }
    }
}
