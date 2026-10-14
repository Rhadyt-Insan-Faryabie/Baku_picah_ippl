package com.fight.app.ui.bout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fight.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

/**
 * Bottom sheet to propose a bout: date, time, location, weight target. Rules are MMA.
 */
public class ProposeBoutBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_propose_bout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton btnPropose = view.findViewById(R.id.btnPropose);
        btnPropose.setOnClickListener(v -> {
            // TODO: POST /api/bout
            Toast.makeText(requireContext(), "Ajuan laga terkirim", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
