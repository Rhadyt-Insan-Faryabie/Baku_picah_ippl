package com.fight.app.ui.auth;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.fight.app.R;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Recovery UI only: the backend does not yet provide a password reset endpoint.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String STATE_STATUS = "recoveryStatus";

    private TextInputEditText etEmail;
    private TextView tvStatus;
    private int statusResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        tvStatus = findViewById(R.id.tvRecoveryStatus);

        findViewById(R.id.btnSend).setOnClickListener(v -> submitRecovery());
        etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitRecovery();
                return true;
            }
            return false;
        });

        findViewById(R.id.btnRecoveryBack).setOnClickListener(v -> navigateToLogin());
        findViewById(R.id.tvBack).setOnClickListener(v -> navigateToLogin());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToLogin();
            }
        });
        findViewById(R.id.btnRecoveryMore).setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add(0, R.id.tvBack, 0, R.string.recovery_menu_login);
            menu.getMenu().add(0, R.id.btnRecoveryMore, 1, R.string.recovery_menu_register);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.tvBack) {
                    navigateToLogin();
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                return true;
            });
            menu.show();
        });

        if (savedInstanceState != null) {
            int savedStatus = savedInstanceState.getInt(STATE_STATUS, 0);
            if (savedStatus == R.string.recovery_email_required
                    || savedStatus == R.string.recovery_email_invalid
                    || savedStatus == R.string.recovery_unavailable) {
                showStatus(savedStatus);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_STATUS, statusResource);
        super.onSaveInstanceState(outState);
    }

    private void submitRecovery() {
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.requestFocus();
            showStatus(R.string.recovery_email_required);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.requestFocus();
            showStatus(R.string.recovery_email_invalid);
            return;
        }

        // Never claim delivery or call an endpoint that the backend does not expose.
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (keyboard != null) {
            keyboard.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
        }
        showStatus(R.string.recovery_unavailable);
    }

    private void showStatus(int messageResource) {
        statusResource = messageResource;
        tvStatus.setText(messageResource);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.post(() -> tvStatus.requestRectangleOnScreen(
                new Rect(0, 0, tvStatus.getWidth(), tvStatus.getHeight()), false));
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
