package com.fight.app.ui.discover;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.MatchApi;
import com.fight.app.data.model.Fighter;
import com.fight.app.data.model.OpponentCard;
import com.fight.app.data.model.SwipeResult;
import com.fight.app.ui.chat.ChatActivity;
import com.fight.app.ui.detail.FighterDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AI-powered fighter discovery feed (Figma 3:356).
 * Shows interactive fighter swipe card with compatibility score, reasons, and like/pass actions.
 * Displays mutual match popup (Figma 3:514) on successful reciprocal match.
 */
public class DiscoverFragment extends Fragment {

    private MaterialTextView tvDiscoveryLocation;
    private AppCompatImageButton btnFilter;
    private Chip chipFilterSameClass;
    private Chip chipFilterMMA;
    private Chip chipFilterLevel;

    private ProgressBar progressBarDiscovery;
    private LinearLayout layoutEmpty;
    private MaterialButton btnReload;

    private FrameLayout flCardContainer;
    private AppCompatImageView ivOpponentPortrait;
    private MaterialTextView tvMatchScore;
    private MaterialTextView tvOpponentNickname;
    private MaterialTextView tvOpponentName;
    private MaterialTextView tvOpponentMeta;
    private MaterialTextView tvOpponentLocation;
    private MaterialTextView tvOpponentRecord;
    private MaterialTextView tvOpponentWeight;

    private LinearLayout layoutAiReason;
    private MaterialTextView tvMatchReasons;

    private LinearLayout layoutActionButtons;
    private AppCompatImageButton btnPass;
    private AppCompatImageButton btnChallenge;
    private AppCompatImageButton btnViewDetail;

    // Recommendation Queue
    private final List<OpponentCard> queue = new ArrayList<>();
    private boolean isLoading = false;
    private boolean sameWeightClassOnly = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupListeners();
        loadRecommendations();
    }

    private void initViews(View view) {
        tvDiscoveryLocation = view.findViewById(R.id.tvDiscoveryLocation);
        btnFilter = view.findViewById(R.id.btnFilter);
        chipFilterSameClass = view.findViewById(R.id.chipFilterSameClass);
        chipFilterMMA = view.findViewById(R.id.chipFilterMMA);
        chipFilterLevel = view.findViewById(R.id.chipFilterLevel);

        progressBarDiscovery = view.findViewById(R.id.progressBarDiscovery);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        btnReload = view.findViewById(R.id.btnReload);

        flCardContainer = view.findViewById(R.id.flCardContainer);
        ivOpponentPortrait = view.findViewById(R.id.ivOpponentPortrait);
        tvMatchScore = view.findViewById(R.id.tvMatchScore);
        tvOpponentNickname = view.findViewById(R.id.tvOpponentNickname);
        tvOpponentName = view.findViewById(R.id.tvOpponentName);
        tvOpponentMeta = view.findViewById(R.id.tvOpponentMeta);
        tvOpponentLocation = view.findViewById(R.id.tvOpponentLocation);
        tvOpponentRecord = view.findViewById(R.id.tvOpponentRecord);
        tvOpponentWeight = view.findViewById(R.id.tvOpponentWeight);

        layoutAiReason = view.findViewById(R.id.layoutAiReason);
        tvMatchReasons = view.findViewById(R.id.tvMatchReasons);

        layoutActionButtons = view.findViewById(R.id.layoutActionButtons);
        btnPass = view.findViewById(R.id.btnPass);
        btnChallenge = view.findViewById(R.id.btnChallenge);
        btnViewDetail = view.findViewById(R.id.btnViewDetail);
    }

    private void setupListeners() {
        btnPass.setOnClickListener(v -> act("pass"));
        btnChallenge.setOnClickListener(v -> act("like"));
        btnViewDetail.setOnClickListener(v -> openDetail());

        btnReload.setOnClickListener(v -> loadRecommendations());

        chipFilterSameClass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sameWeightClassOnly = isChecked;
            loadRecommendations();
        });

        btnFilter.setOnClickListener(v -> {
            chipFilterSameClass.setChecked(!chipFilterSameClass.isChecked());
        });
    }

    private void loadRecommendations() {
        String token = SessionManager.getToken(requireContext());
        if (token == null || token.isEmpty()) {
            // Not logged in: show fallback sample card
            showSampleFallback();
            return;
        }

        setLoadingState(true);
        MatchApi.getRecommendations(token, sameWeightClassOnly, 20, new ApiCallback<List<OpponentCard>>() {
            @Override
            public void onSuccess(List<OpponentCard> data) {
                if (!isAdded()) return;
                setLoadingState(false);
                queue.clear();
                if (data != null && !data.isEmpty()) {
                    queue.addAll(data);
                    showCurrentCard();
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                setLoadingState(false);
                // Fallback to sample if server unreachable or error
                if (queue.isEmpty()) {
                    showSampleFallback();
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showCurrentCard() {
        if (queue.isEmpty()) {
            showEmptyState();
            return;
        }

        OpponentCard current = queue.get(0);
        Fighter fighter = current.fighter;

        flCardContainer.setVisibility(View.VISIBLE);
        layoutAiReason.setVisibility(View.VISIBLE);
        layoutActionButtons.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        // Header location update
        if (fighter.city != null && !fighter.city.isEmpty()) {
            tvDiscoveryLocation.setText(fighter.city.toUpperCase(Locale.ROOT) + " · RADIUS 15 KM");
        } else {
            tvDiscoveryLocation.setText("SELURUH KOTA · SEMUA KELAS");
        }

        // Match Score
        tvMatchScore.setText(current.displayScore());

        // Name & Nickname
        String displayName = fighter.displayName().toUpperCase(Locale.ROOT);
        tvOpponentName.setText(displayName);
        tvOpponentNickname.setText("THE CONTENDER");

        // Subline: Age · Martial Arts · Weight Class
        StringBuilder meta = new StringBuilder();
        if (fighter.martialArts != null && !fighter.martialArts.isEmpty()) {
            meta.append(String.join(", ", fighter.martialArts)).append(" · ");
        }
        meta.append(fighter.weightClass != null ? fighter.weightClass : "Catchweight");
        tvOpponentMeta.setText(meta.toString());

        // Location
        String cityText = fighter.city != null ? fighter.city : "Indonesia";
        tvOpponentLocation.setText(cityText + " · Sekitar kotamu");

        // Record & Weight Badges
        tvOpponentRecord.setText(fighter.wins + " W / " + fighter.losses + " L / " + fighter.draws + " D");
        if (fighter.weightKg != null) {
            tvOpponentWeight.setText(String.format(Locale.getDefault(), "%.1f kg · MMA", fighter.weightKg));
        } else {
            tvOpponentWeight.setText("MMA Division");
        }

        // AI Match Reason
        tvMatchReasons.setText(current.displayReasons());

        // Animate card appearance smoothly
        flCardContainer.setAlpha(0.6f);
        flCardContainer.animate().alpha(1.0f).setDuration(200).start();
    }

    private void act(String action) {
        if (queue.isEmpty() || isLoading) return;

        OpponentCard current = queue.get(0);
        String name = current.fighter.displayName();
        String token = SessionManager.getToken(requireContext());

        // Card exit animation
        float translationX = "like".equals(action) ? 400f : -400f;
        flCardContainer.animate()
                .translationX(translationX)
                .alpha(0f)
                .setDuration(180)
                .withEndAction(() -> {
                    flCardContainer.setTranslationX(0f);
                    flCardContainer.setAlpha(1f);
                    queue.remove(0);
                    showCurrentCard();
                })
                .start();

        if (token == null || token.isEmpty()) {
            if ("like".equals(action)) {
                Toast.makeText(requireContext(), "Kamu menantang " + name + "!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), name + " dilewati.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if ("like".equals(action)) {
            MatchApi.like(token, current.fighter.id, new ApiCallback<SwipeResult>() {
                @Override
                public void onSuccess(SwipeResult data) {
                    if (!isAdded()) return;
                    if (data != null && data.matched) {
                        showMutualMatchDialog(name);
                    } else {
                        Toast.makeText(requireContext(), "Kamu menantang " + name + ". Menunggu respons mereka.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    // silently handled
                }
            });
        } else {
            MatchApi.pass(token, current.fighter.id, new ApiCallback<SwipeResult>() {
                @Override
                public void onSuccess(SwipeResult data) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), name + " dilewati.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    // silently handled
                }
            });
        }
    }

    private void showMutualMatchDialog(String opponentName) {
        if (!isAdded()) return;
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_mutual_match);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        MaterialTextView tvMatchOpponent = dialog.findViewById(R.id.tvMatchOpponent);
        MaterialButton btnOpenChat = dialog.findViewById(R.id.btnOpenChat);
        MaterialButton btnContinueDiscovery = dialog.findViewById(R.id.btnContinueDiscovery);

        tvMatchOpponent.setText("Kamu dan " + opponentName + " siap tanding!");

        btnOpenChat.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(requireContext(), ChatActivity.class));
        });

        btnContinueDiscovery.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void openDetail() {
        if (queue.isEmpty()) return;
        OpponentCard current = queue.get(0);
        Intent intent = new Intent(requireContext(), FighterDetailActivity.class);
        if (current.fighter != null) {
            intent.putExtra("fighter_id", current.fighter.id);
            intent.putExtra("full_name", current.fighter.displayName());
            intent.putExtra("username", current.fighter.username);
            intent.putExtra("city", current.fighter.city);
            if (current.fighter.weightKg != null) intent.putExtra("weight_kg", current.fighter.weightKg);
            if (current.fighter.heightCm != null) intent.putExtra("height_cm", current.fighter.heightCm);
            intent.putExtra("weight_class", current.fighter.weightClass);
            intent.putExtra("wins", current.fighter.wins);
            intent.putExtra("losses", current.fighter.losses);
            intent.putExtra("draws", current.fighter.draws);
            if (current.fighter.martialArts != null) {
                intent.putStringArrayListExtra("martial_arts", new ArrayList<>(current.fighter.martialArts));
            }
        }
        intent.putExtra("match_score", (int) Math.round(current.score * 100));
        startActivity(intent);
    }

    private void setLoadingState(boolean loading) {
        isLoading = loading;
        progressBarDiscovery.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            flCardContainer.setVisibility(View.GONE);
            layoutAiReason.setVisibility(View.GONE);
            layoutActionButtons.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        flCardContainer.setVisibility(View.GONE);
        layoutAiReason.setVisibility(View.GONE);
        layoutActionButtons.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showSampleFallback() {
        queue.clear();

        OpponentCard sample = new OpponentCard();
        sample.fighter = new Fighter();
        sample.fighter.id = 1;
        sample.fighter.username = "ardi_saputra";
        sample.fighter.fullName = "Ardi Saputra";
        sample.fighter.city = "Bandung";
        sample.fighter.weightKg = 64.5;
        sample.fighter.heightCm = 172.0;
        sample.fighter.weightClass = "Lightweight";
        sample.fighter.wins = 5;
        sample.fighter.losses = 2;
        sample.fighter.draws = 0;
        sample.fighter.martialArts = new ArrayList<>();
        sample.fighter.martialArts.add("Boxing");
        sample.fighter.martialArts.add("Muay Thai");

        sample.score = 0.92;
        sample.reasons = new ArrayList<>();
        sample.reasons.add("Sama kelas Lightweight");
        sample.reasons.add("Satu kota (Bandung)");
        sample.reasons.add("Kesamaan: Boxing");

        queue.add(sample);
        showCurrentCard();
    }
}
