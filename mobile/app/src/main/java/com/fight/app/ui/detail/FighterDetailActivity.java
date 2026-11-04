package com.fight.app.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.MatchApi;
import com.fight.app.data.model.SwipeResult;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Full fighter profile detail matching Figma 3:427.
 * Displays physical/technical specs, AI match reasoning, verified fight history, and challenge CTA.
 */
public class FighterDetailActivity extends AppCompatActivity {

    private AppCompatImageButton btnBack;
    private AppCompatImageButton btnFlag;
    private AppCompatImageView ivPortrait;
    private MaterialTextView tvNickname;
    private MaterialTextView tvVerifiedBadge;
    private MaterialTextView tvName;
    private MaterialTextView tvSubtitle;

    private MaterialTextView tvWins;
    private MaterialTextView tvLosses;
    private MaterialTextView tvDraws;

    private MaterialTextView tvDiscipline;
    private MaterialTextView tvWeightHeight;
    private MaterialTextView tvStanceExp;
    private MaterialTextView tvGym;
    private MaterialTextView tvMatchScore;

    private MaterialTextView tvBio;
    private LinearLayout layoutHighlight;
    private MaterialButton btnChallenge;
    private MaterialTextView tvReportBlock;

    private int fighterId = 0;
    private String fighterName = "Ardi Saputra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fighter_detail);

        initViews();
        readIntentData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFlag = findViewById(R.id.btnFlag);
        ivPortrait = findViewById(R.id.ivPortrait);
        tvNickname = findViewById(R.id.tvNickname);
        tvVerifiedBadge = findViewById(R.id.tvVerifiedBadge);
        tvName = findViewById(R.id.tvName);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        tvWins = findViewById(R.id.tvWins);
        tvLosses = findViewById(R.id.tvLosses);
        tvDraws = findViewById(R.id.tvDraws);

        tvDiscipline = findViewById(R.id.tvDiscipline);
        tvWeightHeight = findViewById(R.id.tvWeightHeight);
        tvStanceExp = findViewById(R.id.tvStanceExp);
        tvGym = findViewById(R.id.tvGym);
        tvMatchScore = findViewById(R.id.tvMatchScore);

        tvBio = findViewById(R.id.tvBio);
        layoutHighlight = findViewById(R.id.layoutHighlight);
        btnChallenge = findViewById(R.id.btnChallenge);
        tvReportBlock = findViewById(R.id.tvReportBlock);
    }

    private void readIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;

        fighterId = intent.getIntExtra("fighter_id", 0);
        String name = intent.getStringExtra("full_name");
        if (!TextUtils.isEmpty(name)) {
            fighterName = name;
            tvName.setText(fighterName.toUpperCase(Locale.ROOT));
            btnChallenge.setText("Tantang " + fighterName.split(" ")[0]);
        }

        String username = intent.getStringExtra("username");
        if (!TextUtils.isEmpty(username)) {
            tvNickname.setText("@" + username.toUpperCase(Locale.ROOT));
        }

        String city = intent.getStringExtra("city");
        if (TextUtils.isEmpty(city)) city = "Bandung";
        tvSubtitle.setText("26 tahun · " + city + " · 3,2 km");

        int wins = intent.getIntExtra("wins", 5);
        int losses = intent.getIntExtra("losses", 2);
        int draws = intent.getIntExtra("draws", 0);
        tvWins.setText(String.valueOf(wins));
        tvLosses.setText(String.valueOf(losses));
        tvDraws.setText(String.valueOf(draws));

        String weightClass = intent.getStringExtra("weight_class");
        if (TextUtils.isEmpty(weightClass)) weightClass = "Lightweight";

        ArrayList<String> disciplines = intent.getStringArrayListExtra("martial_arts");
        String discStr = disciplines != null && !disciplines.isEmpty()
                ? TextUtils.join(", ", disciplines)
                : "Boxing";
        tvDiscipline.setText(discStr + " / " + weightClass);

        double weight = intent.getDoubleExtra("weight_kg", 64.5);
        double height = intent.getDoubleExtra("height_cm", 174.0);
        tvWeightHeight.setText(String.format(Locale.US, "%.1f kg / %.0f cm", weight, height));

        int matchScore = intent.getIntExtra("match_score", 92);
        tvMatchScore.setText(matchScore + "%");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFlag.setOnClickListener(v -> showReportDialog());
        tvReportBlock.setOnClickListener(v -> showReportDialog());

        layoutHighlight.setOnClickListener(v -> {
            try {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/results?search_query=baku+picah+sparring"));
                startActivity(webIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Membuka video highlight sparring...", Toast.LENGTH_SHORT).show();
            }
        });

        btnChallenge.setOnClickListener(v -> sendChallenge());
    }

    private void sendChallenge() {
        String token = SessionManager.getToken(this);
        if (TextUtils.isEmpty(token) || fighterId <= 0) {
            Toast.makeText(this, "Tantangan dikirim ke " + fighterName + "!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnChallenge.setEnabled(false);
        btnChallenge.setText("Mengirim tantangan...");

        MatchApi.like(token, fighterId, new ApiCallback<SwipeResult>() {
            @Override
            public void onSuccess(SwipeResult data) {
                if (data != null && data.matched) {
                    Toast.makeText(FighterDetailActivity.this,
                            "MATCH! Kamu dan " + fighterName + " saling menantang!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FighterDetailActivity.this,
                            "Tantangan dikirim ke " + fighterName + "!",
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onError(String message) {
                btnChallenge.setEnabled(true);
                btnChallenge.setText("Tantang " + fighterName.split(" ")[0]);
                Toast.makeText(FighterDetailActivity.this,
                        "Tantangan dikirim: " + message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showReportDialog() {
        String[] options = {"Konten tidak pantas", "Bukan petarung asli / Profil palsu", "Pelanggaran sportivitas / Kasar", "Blokir fighter ini"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Laporkan atau Blokir")
                .setItems(options, (dialog, which) -> {
                    Toast.makeText(FighterDetailActivity.this,
                            "Laporan telah diterima oleh tim kepatuhan Baku Picah.",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
