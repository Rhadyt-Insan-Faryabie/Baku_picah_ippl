package com.fight.app.ui.bouts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fight.app.R;
import com.fight.app.ui.bout.BoutTrackerActivity;
import com.fight.app.ui.bout.SubmitResultActivity;
import com.fight.app.ui.chat.ChatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

/**
 * Bout management & confirmed match hub matching Figma 3:1029 and 3:935.
 * Displays official status, rules, gym venue, escrow guarantee, and quick action links.
 */
public class BoutsFragment extends Fragment {

    private AppCompatImageButton btnBoutMore;
    private MaterialTextView tabUpcoming;
    private MaterialTextView tabCompleted;

    private LinearLayout containerUpcoming;
    private LinearLayout containerCompleted;

    private MaterialButton btnViewGymRoute;
    private MaterialButton btnTrackBout;
    private MaterialButton btnChatOpponent;
    private MaterialButton btnSubmitResult;

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
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        btnBoutMore = view.findViewById(R.id.btnBoutMore);
        tabUpcoming = view.findViewById(R.id.tabUpcoming);
        tabCompleted = view.findViewById(R.id.tabCompleted);

        containerUpcoming = view.findViewById(R.id.containerUpcoming);
        containerCompleted = view.findViewById(R.id.containerCompleted);

        btnViewGymRoute = view.findViewById(R.id.btnViewGymRoute);
        btnTrackBout = view.findViewById(R.id.btnTrackBout);
        btnChatOpponent = view.findViewById(R.id.btnChatOpponent);
        btnSubmitResult = view.findViewById(R.id.btnSubmitResult);
    }

    private void setupListeners() {
        tabUpcoming.setOnClickListener(v -> switchTab(true));
        tabCompleted.setOnClickListener(v -> switchTab(false));

        btnViewGymRoute.setOnClickListener(v -> {
            try {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Garuda+Boxing+Bandung");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Membuka peta lokasi sasana...", Toast.LENGTH_SHORT).show();
            }
        });

        btnTrackBout.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BoutTrackerActivity.class)));

        btnChatOpponent.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("opponent_name", "Ardi “The Hammer”");
            intent.putExtra("opponent_username", "ardi_hammer");
            startActivity(intent);
        });

        btnSubmitResult.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SubmitResultActivity.class)));

        btnBoutMore.setOnClickListener(this::showOptionsMenu);
    }

    private void switchTab(boolean showUpcoming) {
        int activeBg = ContextCompat.getColor(requireContext(), R.color.bp_blood);
        int inactiveBg = ContextCompat.getColor(requireContext(), R.color.bp_surface);
        int activeText = ContextCompat.getColor(requireContext(), R.color.bp_white);
        int inactiveText = ContextCompat.getColor(requireContext(), R.color.bp_login_muted);

        tabUpcoming.setBackgroundColor(showUpcoming ? activeBg : inactiveBg);
        tabUpcoming.setTextColor(showUpcoming ? activeText : inactiveText);

        tabCompleted.setBackgroundColor(!showUpcoming ? activeBg : inactiveBg);
        tabCompleted.setTextColor(!showUpcoming ? activeText : inactiveText);

        containerUpcoming.setVisibility(showUpcoming ? View.VISIBLE : View.GONE);
        containerCompleted.setVisibility(!showUpcoming ? View.VISIBLE : View.GONE);
    }

    private void showOptionsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenu().add(0, 1, 0, "Ketentuan Escrow & Jaminan");
        popup.getMenu().add(0, 2, 1, "Hubungi Layanan Bantuan Admin");
        popup.getMenu().add(0, 3, 2, "Ajukan Pembatalan Laga");

        popup.setOnMenuItemClickListener(item -> {
            Toast.makeText(requireContext(), "Fitur " + item.getTitle() + " aktif.", Toast.LENGTH_SHORT).show();
            return true;
        });
        popup.show();
    }
}
