package com.fight.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.AuthApi;
import com.fight.app.data.model.Fighter;
import com.fight.app.ui.auth.ProfileSetupActivity;
import com.fight.app.ui.auth.WelcomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

/**
 * Fighter profile tab matching Figma 3:1174.
 * Displays live stats, MMA record, technical specs, last bout, and account management.
 */
public class ProfileFragment extends Fragment {

    private AppCompatImageButton btnSettings;
    private AppCompatImageView ivProfilePortrait;
    private MaterialTextView tvProfileNickname;
    private MaterialTextView tvProfileName;
    private MaterialTextView tvProfileSubtitle;

    private MaterialTextView tvWins;
    private MaterialTextView tvLosses;
    private MaterialTextView tvDraws;

    private MaterialTextView tvDiscipline;
    private MaterialTextView tvWeightHeight;
    private MaterialTextView tvCity;
    private MaterialTextView tvBio;

    private MaterialButton btnEditProfile;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void initViews(View view) {
        btnSettings = view.findViewById(R.id.btnSettings);
        ivProfilePortrait = view.findViewById(R.id.ivProfilePortrait);
        tvProfileNickname = view.findViewById(R.id.tvProfileNickname);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileSubtitle = view.findViewById(R.id.tvProfileSubtitle);

        tvWins = view.findViewById(R.id.tvWins);
        tvLosses = view.findViewById(R.id.tvLosses);
        tvDraws = view.findViewById(R.id.tvDraws);

        tvDiscipline = view.findViewById(R.id.tvDiscipline);
        tvWeightHeight = view.findViewById(R.id.tvWeightHeight);
        tvCity = view.findViewById(R.id.tvCity);
        tvBio = view.findViewById(R.id.tvBio);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Populate initial placeholder / cached name from session
        if (getContext() != null) {
            String cachedName = SessionManager.getFullName(requireContext());
            if (!TextUtils.isEmpty(cachedName)) {
                tvProfileName.setText(cachedName.toUpperCase(Locale.ROOT));
            }
        }
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfileSetupActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        btnSettings.setOnClickListener(this::showSettingsMenu);
    }

    private void loadProfile() {
        if (getContext() == null) return;

        String token = SessionManager.getToken(requireContext());
        if (TextUtils.isEmpty(token)) {
            redirectToWelcome();
            return;
        }

        AuthApi.me(token, new ApiCallback<Fighter>() {
            @Override
            public void onSuccess(Fighter fighter) {
                if (!isAdded() || getContext() == null) return;
                populateData(fighter);
            }

            @Override
            public void onError(String message) {
                if (!isAdded() || getContext() == null) return;
                // If offline or request fails, keep existing cache and notify gently
                Toast.makeText(getContext(), "Gagal memuat data profil terbaru: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateData(@NonNull Fighter f) {
        // Name & Nickname
        String name = f.displayName();
        tvProfileName.setText(name != null ? name.toUpperCase(Locale.ROOT) : "PETARUNG");

        if (f.username != null && !f.username.isEmpty()) {
            tvProfileNickname.setText("@" + f.username.toUpperCase(Locale.ROOT));
        } else {
            tvProfileNickname.setText("THE CONTENDER");
        }

        // Subtitle: Divisi · Kota
        StringBuilder sub = new StringBuilder();
        if (f.weightClass != null && !f.weightClass.isEmpty()) {
            sub.append("Divisi ").append(f.weightClass);
        } else {
            sub.append("Divisi MMA");
        }
        if (f.city != null && !f.city.isEmpty()) {
            sub.append(" · ").append(f.city);
        }
        tvProfileSubtitle.setText(sub.toString());

        // Stats W-L-D
        tvWins.setText(String.valueOf(f.wins));
        tvLosses.setText(String.valueOf(f.losses));
        tvDraws.setText(String.valueOf(f.draws));

        // Discipline & Weight Class
        StringBuilder disc = new StringBuilder();
        if (f.martialArts != null && !f.martialArts.isEmpty()) {
            disc.append(TextUtils.join(", ", f.martialArts));
        } else {
            disc.append("MMA");
        }
        if (f.weightClass != null && !f.weightClass.isEmpty()) {
            disc.append(" · ").append(f.weightClass);
        }
        tvDiscipline.setText(disc.toString());

        // Weight / Height
        StringBuilder wh = new StringBuilder();
        if (f.weightKg != null && f.weightKg > 0) {
            wh.append(String.format(Locale.US, "%.1f kg", f.weightKg));
        }
        if (f.heightCm != null && f.heightCm > 0) {
            if (wh.length() > 0) wh.append(" / ");
            wh.append(String.format(Locale.US, "%.0f cm", f.heightCm));
        }
        tvWeightHeight.setText(wh.length() > 0 ? wh.toString() : "-");

        // City
        tvCity.setText(f.city != null && !f.city.isEmpty() ? f.city : "Belum diatur");
    }

    private void showSettingsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenu().add(0, 1, 0, "Edit Profil Fighter");
        popup.getMenu().add(0, 2, 1, "Ketentuan Fair Play & Escrow");
        popup.getMenu().add(0, 3, 2, "Keluar Akun (Logout)");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                startActivity(new Intent(requireContext(), ProfileSetupActivity.class));
                return true;
            } else if (item.getItemId() == 2) {
                Toast.makeText(requireContext(), "Baku Picah menjamin fair play & escrow resmi.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == 3) {
                showLogoutDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showLogoutDialog() {
        if (getContext() == null) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Keluar Akun")
                .setMessage("Apakah kamu yakin ingin keluar dari akun Baku Picah?")
                .setPositiveButton("Keluar", (dialog, which) -> {
                    SessionManager.clear(requireContext());
                    redirectToWelcome();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void redirectToWelcome() {
        if (getContext() == null) return;
        Intent intent = new Intent(requireContext(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
