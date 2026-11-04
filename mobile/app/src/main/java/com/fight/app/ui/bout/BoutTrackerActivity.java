package com.fight.app.ui.bout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;

import com.fight.app.R;
import com.fight.app.ui.chat.ChatActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Bout status tracker matching Figma 3:935.
 * Visualizes the 5-step lifecycle:
 * Pending Opponent → Opponent Accepted → Awaiting Admin Verification → Confirmed → Completed.
 */
public class BoutTrackerActivity extends AppCompatActivity {

    private AppCompatImageButton btnBackTracker;
    private AppCompatImageButton btnTrackerMore;
    private MaterialButton btnContactOpponentTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bout_tracker);

        btnBackTracker = findViewById(R.id.btnBackTracker);
        btnTrackerMore = findViewById(R.id.btnTrackerMore);
        btnContactOpponentTracker = findViewById(R.id.btnContactOpponentTracker);

        btnBackTracker.setOnClickListener(v -> finish());

        btnContactOpponentTracker.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("opponent_name", "Ardi “The Hammer”");
            intent.putExtra("opponent_username", "ardi_hammer");
            startActivity(intent);
        });

        btnTrackerMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add(0, 1, 0, "Ketentuan Escrow & Jaminan");
            popup.getMenu().add(0, 2, 1, "Hubungi Layanan Bantuan Admin");
            popup.getMenu().add(0, 3, 2, "Batalkan Proposal Laga");

            popup.setOnMenuItemClickListener(item -> {
                Toast.makeText(this, "Fitur " + item.getTitle() + " aktif.", Toast.LENGTH_SHORT).show();
                return true;
            });
            popup.show();
        });
    }
}
