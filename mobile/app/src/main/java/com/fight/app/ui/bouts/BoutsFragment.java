package com.fight.app.ui.bouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fight.app.R;
import com.fight.app.ui.bout.BoutTrackerActivity;
import com.fight.app.ui.bout.SubmitResultActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Bout list + status tracker entry point.
 */
public class BoutsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton btnTrack = view.findViewById(R.id.btnTrackBout);
        MaterialButton btnResult = view.findViewById(R.id.btnSubmitResult);

        btnTrack.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BoutTrackerActivity.class)));
        btnResult.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SubmitResultActivity.class)));
    }
}
