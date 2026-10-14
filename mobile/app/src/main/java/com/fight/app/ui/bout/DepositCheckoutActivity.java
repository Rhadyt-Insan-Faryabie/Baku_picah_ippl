package com.fight.app.ui.bout;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.google.android.material.button.MaterialButton;

/**
 * Deposit & commitment escrow checkout.
 */
public class DepositCheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_checkout);

        MaterialButton btnUpload = findViewById(R.id.btnUploadProof);
        MaterialButton btnPay = findViewById(R.id.btnPay);

        btnUpload.setOnClickListener(v ->
                Toast.makeText(this, "Pilih bukti pembayaran", Toast.LENGTH_SHORT).show());
        btnPay.setOnClickListener(v -> {
            // TODO: POST /api/payments/escrow
            Toast.makeText(this, "Deposit dibayar (placeholder)", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
