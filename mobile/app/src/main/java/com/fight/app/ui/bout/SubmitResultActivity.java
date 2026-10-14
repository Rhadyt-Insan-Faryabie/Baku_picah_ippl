package com.fight.app.ui.bout;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Post-fight result submission: winner, method, round, sportmanship rating.
 */
public class SubmitResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_result);

        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            // TODO: POST /api/bout/{id}/result
            Toast.makeText(this, "Hasil terkirim, menunggu konfirmasi lawan", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
