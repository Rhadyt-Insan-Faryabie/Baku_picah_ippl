package com.fight.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fight.app.R;
import com.fight.app.ui.auth.ProfileSetupActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Fighter profile: stats, record, affiliation.
 */
public class ProfileFragment extends Fragment {

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
        MaterialButton btnEdit = view.findViewById(R.id.btnEditProfile);
        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ProfileSetupActivity.class)));
    }
}
