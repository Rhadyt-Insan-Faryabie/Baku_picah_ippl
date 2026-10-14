package com.fight.app.ui.detail;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Full fighter detail: bio, fight history, highlights, stance, experience.
 */
public class FighterDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fighter_detail);

        MaterialButton btnChallenge = findViewById(R.id.btnChallenge);
        // TODO: create a challenge via the API.
        btnChallenge.setOnClickListener(v -> finish());
    }
}
