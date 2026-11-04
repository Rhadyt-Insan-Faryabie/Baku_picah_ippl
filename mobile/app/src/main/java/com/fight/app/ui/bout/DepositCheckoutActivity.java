package com.fight.app.ui.bout;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Escrow deposit checkout matching Figma 3:808.
 * Handles payment method selection and deposit confirmation.
 */
public class DepositCheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_checkout);

        AppCompatImageButton btnBack = findViewById(R.id.btnBackDeposit);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        MaterialButton btnUploadProof = findViewById(R.id.btnUploadProof);
        if (btnUploadProof != null) {
            btnUploadProof.setOnClickListener(v ->
                    Toast.makeText(this, "Pilih foto bukti pembayaran", Toast.LENGTH_SHORT).show());
        }

        MaterialButton btnPay = findViewById(R.id.btnPay);
        if (btnPay != null) {
            btnPay.setOnClickListener(v -> {
                Toast.makeText(this, "Deposit berhasil dikonfirmasi! Sedang diverifikasi oleh admin.", Toast.LENGTH_LONG).show();
                finish();
            });
        }
    }
}
