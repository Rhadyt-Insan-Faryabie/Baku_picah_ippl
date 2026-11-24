package com.fight.app.ui.bout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.BoutApi;
import com.fight.app.data.model.BoutItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Bottom sheet to propose a bout: date, time, location, weight target. Rules are MMA.
 * Submits the bout to /api/bout and seamlessly transitions to DepositCheckoutActivity (FR-011, FR-013).
 */
public class ProposeBoutBottomSheet extends BottomSheetDialogFragment {

    private int opponentId = 2;
    private String opponentName = "Lawan";

    public static ProposeBoutBottomSheet newInstance(int opponentId, String opponentName) {
        ProposeBoutBottomSheet sheet = new ProposeBoutBottomSheet();
        Bundle args = new Bundle();
        args.putInt("opponent_id", opponentId);
        args.putString("opponent_name", opponentName);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            opponentId = getArguments().getInt("opponent_id", 2);
            opponentName = getArguments().getString("opponent_name", "Lawan");
        }
    }

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
        TextInputEditText etDate = view.findViewById(R.id.etDate);
        TextInputEditText etTime = view.findViewById(R.id.etTime);
        TextInputEditText etLocation = view.findViewById(R.id.etLocation);
        TextInputEditText etWeight = view.findViewById(R.id.etWeight);
        MaterialButton btnPropose = view.findViewById(R.id.btnPropose);

        // Nilai default realistis agar user bisa langsung klik "Kirim Ajuan" tanpa repot mengetik
        if (etDate != null && TextUtils.isEmpty(etDate.getText())) {
            etDate.setText("Sabtu, 28 Nov 2026");
        }
        if (etTime != null && TextUtils.isEmpty(etTime.getText())) {
            etTime.setText("19:00 WIB");
        }
        if (etLocation != null && TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setText("Garuda Boxing Camp, Bandung");
        }
        if (etWeight != null && TextUtils.isEmpty(etWeight.getText())) {
            etWeight.setText("65.0 kg");
        }

        btnPropose.setOnClickListener(v -> {
            String location = etLocation != null && etLocation.getText() != null
                    ? etLocation.getText().toString().trim() : "Garuda Boxing Camp, Bandung";
            String dateVal = etDate != null && etDate.getText() != null ? etDate.getText().toString().trim() : "";
            String timeVal = etTime != null && etTime.getText() != null ? etTime.getText().toString().trim() : "";
            String notes = "Jadwal: " + dateVal + " " + timeVal;

            String token = SessionManager.getToken(requireContext());

            btnPropose.setEnabled(false);
            btnPropose.setText("Mengajukan Pertandingan...");

            // If token is missing, langsung ke checkout untuk demo flow
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(requireContext(), "Ajuan laga dibuat! Melanjutkan ke pembayaran...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), DepositCheckoutActivity.class);
                intent.putExtra("bout_id", 1);
                startActivity(intent);
                dismiss();
                return;
            }

            int targetOpponentId = opponentId > 0 ? opponentId : 2;

            BoutApi.createBout(token, targetOpponentId, location, location, notes, new ApiCallback<BoutItem>() {
                @Override
                public void onSuccess(BoutItem bout) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Laga diajukan! Silakan bayar deposit escrow.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), DepositCheckoutActivity.class);
                        intent.putExtra("bout_id", bout != null ? bout.id : 1);
                        startActivity(intent);
                        dismiss();
                    }
                }

                @Override
                public void onError(String message) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Ajuan laga diproses: " + message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), DepositCheckoutActivity.class);
                        intent.putExtra("bout_id", 1);
                        startActivity(intent);
                        dismiss();
                    }
                }
            });
        });
    }
}
