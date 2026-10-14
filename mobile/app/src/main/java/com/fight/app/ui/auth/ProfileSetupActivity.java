package com.fight.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.MainActivity;
import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.AuthApi;
import com.fight.app.data.model.Fighter;
import com.fight.app.data.model.UpdateProfileRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Profile setup (weight, height, city, martial-arts background).
 * Bout rules are always MMA. Persists via PUT /api/fighters/me.
 */
public class ProfileSetupActivity extends AppCompatActivity {

    private static final String CITY_PLACEHOLDER = "Pilih kota…";

    private TextInputEditText etWeight;
    private TextInputEditText etHeight;
    private TextInputEditText etMartialArts;
    private Spinner spCity;
    private MaterialButton btnSave;

    private final List<String> cityOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etMartialArts = findViewById(R.id.etMartialArts);
        spCity = findViewById(R.id.spCity);
        btnSave = findViewById(R.id.btnSave);

        setupCitySpinner();
        btnSave.setOnClickListener(v -> save());
    }

    private void setupCitySpinner() {
        cityOptions.add(CITY_PLACEHOLDER);
        cityOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.bp_cities)));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.item_dropdown_bp, cityOptions);
        adapter.setDropDownViewResource(R.layout.item_dropdown_bp);
        spCity.setAdapter(adapter);
    }

    private void save() {
        String token = SessionManager.getToken(this);
        if (token == null || token.isEmpty()) {
            // Not logged in (e.g. opened from Welcome): just continue.
            goToMain();
            return;
        }

        String city = selectedCity();
        List<String> martialArts = parseMartialArts(text(etMartialArts));
        Double weight = parseDouble(text(etWeight));
        Double height = parseDouble(text(etHeight));

        UpdateProfileRequest body = new UpdateProfileRequest(
                SessionManager.getFullName(this), city, weight, height,
                martialArts.isEmpty() ? null : martialArts);

        setLoading(true);
        AuthApi.updateProfile(token, body, new ApiCallback<Fighter>() {
            @Override
            public void onSuccess(Fighter data) {
                setLoading(false);
                Toast.makeText(ProfileSetupActivity.this, "Profil tersimpan", Toast.LENGTH_SHORT).show();
                goToMain();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(ProfileSetupActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String selectedCity() {
        int position = spCity.getSelectedItemPosition();
        if (position <= 0 || position >= cityOptions.size()) {
            return null;
        }
        return cityOptions.get(position);
    }

    private List<String> parseMartialArts(String value) {
        List<String> result = new ArrayList<>();
        if (TextUtils.isEmpty(value)) {
            return result;
        }
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    private Double parseDouble(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        btnSave.setText(loading ? "Menyimpan..." : "Simpan & Lanjut");
    }
}
