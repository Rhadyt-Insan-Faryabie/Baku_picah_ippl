package com.fight.app.ui.bout;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Post-fight result submission matching Figma 3:1110.
 * Allows submitting winner, method, finish round, and sportsmanship rating.
 */
public class SubmitResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_result);

        AppCompatImageButton btnBack = findViewById(R.id.btnBackResult);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(this, "Hasil laga berhasil dikirim! Menunggu konfirmasi lawan & validasi admin.", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
