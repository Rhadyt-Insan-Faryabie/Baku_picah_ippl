package com.fight.app.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.MainActivity;
import com.fight.app.R;
import com.fight.app.data.SessionManager;

/**
 * Branded splash screen; routes to the app if logged in, otherwise to Welcome.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1200L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean loggedIn = SessionManager.isLoggedIn(this);
            Class<?> next = loggedIn ? MainActivity.class : WelcomeActivity.class;
            startActivity(new Intent(SplashActivity.this, next));
            finish();
        }, SPLASH_DELAY_MS);
    }
}
