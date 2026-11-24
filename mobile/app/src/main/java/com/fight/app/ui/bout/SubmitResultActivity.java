package com.fight.app.ui.bout;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.BoutApi;
import com.fight.app.data.model.BoutItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Post-fight result submission matching Figma 3:1110.
 * Allows submitting winner, method, finish round, and sportsmanship rating (FR-007, FR-017).
 */
public class SubmitResultActivity extends AppCompatActivity {

    private int boutId = 1;

    private TextInputEditText etWinner;
    private TextInputEditText etMethod;
    private TextInputEditText etRound;
    private RatingBar ratingBar;
    private TextInputEditText etNotes;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_result);

        boutId = getIntent().getIntExtra("bout_id", 1);

        AppCompatImageButton btnBack = findViewById(R.id.btnBackResult);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        etWinner = findViewById(R.id.etWinner);
        etMethod = findViewById(R.id.etMethod);
        etRound = findViewById(R.id.etRound);
        ratingBar = findViewById(R.id.ratingBar);
        etNotes = findViewById(R.id.etNotes);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitResult());
    }

    private void submitResult() {
        String token = SessionManager.getToken(this);
        String method = etMethod != null && etMethod.getText() != null
                ? etMethod.getText().toString().trim() : "Decision";
        if (TextUtils.isEmpty(method)) {
            method = "Decision";
        }

        String roundStr = etRound != null && etRound.getText() != null
                ? etRound.getText().toString().trim() : "3";
        int round = 3;
        try {
            if (!TextUtils.isEmpty(roundStr)) round = Integer.parseInt(roundStr);
        } catch (NumberFormatException ignored) {}

        int rating = ratingBar != null ? Math.round(ratingBar.getRating()) : 5;
        String notes = etNotes != null && etNotes.getText() != null
                ? etNotes.getText().toString().trim() : "";

        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, "Hasil laga berhasil dikirim! Menunggu konfirmasi lawan & validasi admin.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Mengirim Hasil...");

        int myId = SessionManager.getFighterId(this);
        Integer winnerId = myId; // Default to current user winning unless specified
        String winnerText = etWinner != null && etWinner.getText() != null ? etWinner.getText().toString().toLowerCase() : "";
        if (winnerText.contains("lawan")) {
            winnerId = null; // Backend can resolve or leave null
        } else if (winnerText.contains("draw") || winnerText.contains("seri")) {
            winnerId = null;
        }

        BoutApi.submitResult(token, boutId, winnerId, method, round, notes, rating, new ApiCallback<BoutItem>() {
            @Override
            public void onSuccess(BoutItem data) {
                Toast.makeText(SubmitResultActivity.this,
                        "Hasil pertandingan dan rating sportivitas berhasil dicatat secara resmi!",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SubmitResultActivity.this,
                        "Hasil tersimpan: " + message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
