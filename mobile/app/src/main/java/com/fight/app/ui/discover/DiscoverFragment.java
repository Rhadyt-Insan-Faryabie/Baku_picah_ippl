package com.fight.app.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fight.app.R;
import com.fight.app.ui.detail.FighterDetailActivity;
import com.google.android.material.button.MaterialButton;

/**
 * AI-powered fighter discovery feed (placeholder swipe card + actions).
 */
public class DiscoverFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageButton btnPass = view.findViewById(R.id.btnPass);
        ImageButton btnLike = view.findViewById(R.id.btnLike);
        MaterialButton btnDetail = view.findViewById(R.id.btnViewDetail);

        // Placeholder actions until the backend/matchmaking is wired.
        View.OnClickListener openDetail = v ->
                startActivity(new Intent(requireContext(), FighterDetailActivity.class));

        btnPass.setOnClickListener(v -> { /* TODO: pass */ });
        btnLike.setOnClickListener(openDetail);
        btnDetail.setOnClickListener(openDetail);
    }
}
