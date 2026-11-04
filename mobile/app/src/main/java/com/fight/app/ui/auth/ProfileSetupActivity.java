package com.fight.app.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.fight.app.MainActivity;
import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.AuthApi;
import com.fight.app.data.model.Fighter;
import com.fight.app.data.model.UpdateProfileRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 2-Step Interactive Fighter Profile Setup based on Figma (3:207 and 3:279).
 * - Step 1: Identitas Fighter (Name, Nickname, Birthdate/Age, Weight/Height, Auto Weight Class, City, Photo).
 * - Step 2: Gaya Bertarung (Martial arts disciplines, Experience, Stance, W-L-D Record, Gym, Coach, Bio).
 */
public class ProfileSetupActivity extends AppCompatActivity {

    private static final String CITY_PLACEHOLDER = "Pilih kota domisili…";

    // Header & Navigation
    private AppCompatImageButton btnBack;
    private AppCompatImageButton btnMore;
    private MaterialTextView tvStepEyebrow;
    private MaterialTextView tvStepTitle;
    private View viewProgressBar;

    // Containers
    private LinearLayout layoutStep1;
    private LinearLayout layoutStep2;

    // Step 1 Views
    private FrameLayout flPhotoPlaceholder;
    private AppCompatImageView ivPhotoPreview;
    private AppCompatImageView ivDefaultCamera;
    private MaterialButton btnUploadPhoto;
    private TextInputEditText etFullName;
    private TextInputEditText etNickname;
    private FrameLayout btnPickBirthDate;
    private MaterialTextView tvBirthDate;
    private MaterialTextView tvAge;
    private TextInputEditText etHeight;
    private TextInputEditText etWeight;
    private MaterialTextView tvWeightClass;
    private Spinner spCity;
    private MaterialButton btnNextStep;

    // Step 2 Views
    private ChipGroup cgMartialArts;
    private Spinner spExperience;
    private Spinner spStance;
    private TextInputEditText etWins;
    private TextInputEditText etLosses;
    private TextInputEditText etDraws;
    private TextInputEditText etGym;
    private TextInputEditText etCoach;
    private TextInputEditText etBio;
    private MaterialCheckBox cbAgreement;
    private MaterialButton btnSaveProfile;

    // State
    private int currentStep = 1;
    private final Calendar birthCalendar = Calendar.getInstance();
    private final List<String> cityOptions = new ArrayList<>();
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        initViews();
        setupPhotoPicker();
        setupStep1();
        setupStep2();

        showStep(1);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);
        tvStepEyebrow = findViewById(R.id.tvStepEyebrow);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        viewProgressBar = findViewById(R.id.viewProgressBar);

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);

        // Step 1
        flPhotoPlaceholder = findViewById(R.id.flPhotoPlaceholder);
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview);
        ivDefaultCamera = findViewById(R.id.ivDefaultCamera);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        etFullName = findViewById(R.id.etFullName);
        etNickname = findViewById(R.id.etNickname);
        btnPickBirthDate = findViewById(R.id.btnPickBirthDate);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvAge = findViewById(R.id.tvAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        tvWeightClass = findViewById(R.id.tvWeightClass);
        spCity = findViewById(R.id.spCity);
        btnNextStep = findViewById(R.id.btnNextStep);

        // Step 2
        cgMartialArts = findViewById(R.id.cgMartialArts);
        spExperience = findViewById(R.id.spExperience);
        spStance = findViewById(R.id.spStance);
        etWins = findViewById(R.id.etWins);
        etLosses = findViewById(R.id.etLosses);
        etDraws = findViewById(R.id.etDraws);
        etGym = findViewById(R.id.etGym);
        etCoach = findViewById(R.id.etCoach);
        etBio = findViewById(R.id.etBio);
        cbAgreement = findViewById(R.id.cbAgreement);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        btnBack.setOnClickListener(v -> handleBack());
        btnMore.setOnClickListener(v ->
                Toast.makeText(this, "Baku Picah Profile Setup v1.0", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupPhotoPicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                (Uri uri) -> {
                    if (uri != null) {
                        ivPhotoPreview.setImageURI(uri);
                        ivPhotoPreview.setVisibility(View.VISIBLE);
                        ivDefaultCamera.setVisibility(View.GONE);
                    }
                }
        );

        View.OnClickListener pickPhoto = v -> imagePickerLauncher.launch("image/*");
        btnUploadPhoto.setOnClickListener(pickPhoto);
        flPhotoPlaceholder.setOnClickListener(pickPhoto);
    }

    private void setupStep1() {
        // Pre-fill name from session if available
        String savedName = SessionManager.getFullName(this);
        if (!TextUtils.isEmpty(savedName)) {
            etFullName.setText(savedName);
        }

        // Default birth date setup (20 years ago)
        birthCalendar.add(Calendar.YEAR, -22);
        updateBirthDateDisplay();

        btnPickBirthDate.setOnClickListener(v -> showDatePicker());

        // Real-time weight class calculation
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndShowWeightClass(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // City Spinner
        cityOptions.clear();
        cityOptions.add(CITY_PLACEHOLDER);
        cityOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.bp_cities)));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown_bp, cityOptions);
        cityAdapter.setDropDownViewResource(R.layout.item_dropdown_bp);
        spCity.setAdapter(cityAdapter);

        btnNextStep.setOnClickListener(v -> validateAndProceedToStep2());
    }

    private void setupStep2() {
        // Stance & Experience Spinners
        ArrayAdapter<CharSequence> expAdapter = ArrayAdapter.createFromResource(
                this, R.array.bp_experiences, R.layout.item_dropdown_bp
        );
        expAdapter.setDropDownViewResource(R.layout.item_dropdown_bp);
        spExperience.setAdapter(expAdapter);

        ArrayAdapter<CharSequence> stanceAdapter = ArrayAdapter.createFromResource(
                this, R.array.bp_stances, R.layout.item_dropdown_bp
        );
        stanceAdapter.setDropDownViewResource(R.layout.item_dropdown_bp);
        spStance.setAdapter(stanceAdapter);

        // Pre-select Boxing & MMA by default to guide user
        Chip chipBoxing = findViewById(R.id.chipBoxing);
        if (chipBoxing != null) chipBoxing.setChecked(true);

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    birthCalendar.set(Calendar.YEAR, year);
                    birthCalendar.set(Calendar.MONTH, month);
                    birthCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateBirthDateDisplay();
                },
                birthCalendar.get(Calendar.YEAR),
                birthCalendar.get(Calendar.MONTH),
                birthCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateBirthDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        tvBirthDate.setText(sdf.format(birthCalendar.getTime()));

        // Calculate age
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        if (age < 0) age = 0;
        tvAge.setText(age + " tahun");
    }

    private void calculateAndShowWeightClass(String weightStr) {
        if (TextUtils.isEmpty(weightStr)) {
            tvWeightClass.setText("Masukkan berat badan untuk estimasi kelas");
            tvWeightClass.setTextColor(ContextCompat.getColor(this, R.color.bp_login_muted));
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            String className;
            String targetDesc;

            if (weight <= 56.7) {
                className = "Flyweight";
                targetDesc = "≤ 56.7 kg";
            } else if (weight <= 61.2) {
                className = "Bantamweight";
                targetDesc = "≤ 61.2 kg";
            } else if (weight <= 65.8) {
                className = "Featherweight";
                targetDesc = "≤ 65.8 kg";
            } else if (weight <= 70.3) {
                className = "Lightweight";
                targetDesc = "≤ 70.3 kg";
            } else if (weight <= 77.1) {
                className = "Welterweight";
                targetDesc = "≤ 77.1 kg";
            } else if (weight <= 83.9) {
                className = "Middleweight";
                targetDesc = "≤ 83.9 kg";
            } else if (weight <= 92.9) {
                className = "Light Heavyweight";
                targetDesc = "≤ 92.9 kg";
            } else {
                className = "Heavyweight";
                targetDesc = "> 92.9 kg (Open Weight)";
            }

            tvWeightClass.setText(className + " · target " + targetDesc);
            tvWeightClass.setTextColor(ContextCompat.getColor(this, R.color.bp_login_text));
        } catch (NumberFormatException e) {
            tvWeightClass.setText("Format berat badan tidak valid");
            tvWeightClass.setTextColor(ContextCompat.getColor(this, R.color.bp_login_accent));
        }
    }

    private void validateAndProceedToStep2() {
        String fullName = text(etFullName);
        String heightStr = text(etHeight);
        String weightStr = text(etWeight);

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Nama lengkap harus diisi");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(heightStr)) {
            etHeight.setError("Tinggi badan harus diisi");
            etHeight.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(weightStr)) {
            etWeight.setError("Berat badan harus diisi");
            etWeight.requestFocus();
            return;
        }

        if (spCity.getSelectedItemPosition() <= 0) {
            Toast.makeText(this, "Silakan pilih kota domisili", Toast.LENGTH_SHORT).show();
            spCity.performClick();
            return;
        }

        showStep(2);
    }

    private void showStep(int step) {
        currentStep = step;
        ViewGroup.LayoutParams params = viewProgressBar.getLayoutParams();

        if (step == 1) {
            layoutStep1.setVisibility(View.VISIBLE);
            layoutStep2.setVisibility(View.GONE);
            tvStepEyebrow.setText(R.string.setup_step1_eyebrow);
            tvStepTitle.setText(R.string.setup_step1_title);

            // 50% width
            int halfWidth = getResources().getDisplayMetrics().widthPixels / 2;
            params.width = halfWidth;
            viewProgressBar.setLayoutParams(params);
        } else {
            layoutStep1.setVisibility(View.GONE);
            layoutStep2.setVisibility(View.VISIBLE);
            tvStepEyebrow.setText(R.string.setup_step2_eyebrow);
            tvStepTitle.setText(R.string.setup_step2_title);

            // 100% width
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            viewProgressBar.setLayoutParams(params);
        }
    }

    private void handleBack() {
        if (currentStep == 2) {
            showStep(1);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentStep == 2) {
            showStep(1);
        } else {
            super.onBackPressed();
        }
    }

    private void saveProfile() {
        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "Kamu harus menyetujui aturan sportivitas untuk melanjutkan", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedMartialArts = new ArrayList<>();
        for (int i = 0; i < cgMartialArts.getChildCount(); i++) {
            View child = cgMartialArts.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selectedMartialArts.add(chip.getText().toString());
                }
            }
        }

        if (selectedMartialArts.isEmpty()) {
            Toast.makeText(this, "Pilih minimal 1 disiplin bela diri", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = SessionManager.getToken(this);
        if (token == null || token.isEmpty()) {
            // Jika masuk sebagai tamu / tidak ada sesi login, langsung ke halaman utama
            goToMain();
            return;
        }

        String fullName = text(etFullName);
        String city = spCity.getSelectedItem().toString();
        Double weight = parseDouble(text(etWeight));
        Double height = parseDouble(text(etHeight));

        int wins = parseInt(text(etWins));
        int losses = parseInt(text(etLosses));
        int draws = parseInt(text(etDraws));

        UpdateProfileRequest body = new UpdateProfileRequest(
                fullName,
                city,
                weight,
                height,
                selectedMartialArts,
                wins,
                losses,
                draws
        );

        setLoading(true);
        AuthApi.updateProfile(token, body, new ApiCallback<Fighter>() {
            @Override
            public void onSuccess(Fighter data) {
                setLoading(false);
                Toast.makeText(ProfileSetupActivity.this, "Profil petarung berhasil disimpan!", Toast.LENGTH_SHORT).show();
                goToMain();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(ProfileSetupActivity.this, "Gagal menyimpan: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnSaveProfile.setEnabled(!loading);
        btnSaveProfile.setText(loading ? "Menyimpan data..." : getString(R.string.setup_save_action));
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private static String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private static Double parseDouble(String raw) {
        if (TextUtils.isEmpty(raw)) return null;
        try {
            return Double.parseDouble(raw.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int parseInt(String raw) {
        if (TextUtils.isEmpty(raw)) return 0;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
