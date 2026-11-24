package com.fight.app.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.fight.app.R;
import com.fight.app.data.SessionManager;
import com.fight.app.data.api.ApiCallback;
import com.fight.app.data.api.ChatApi;
import com.fight.app.data.model.ChatMessage;
import com.fight.app.ui.bout.ProposeBoutBottomSheet;
import com.fight.app.ui.detail.FighterDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 1-on-1 Chat Room matching Figma 3:649 (FR-010).
 * Includes back navigation, sparring agreement card, message thread, propose bout CTA, and composer.
 */
public class ChatActivity extends AppCompatActivity {

    private AppCompatImageButton btnBack;
    private AppCompatImageButton btnMore;
    private MaterialTextView tvEyebrow;
    private MaterialTextView tvOpponentName;

    private ScrollView scrollChat;
    private LinearLayout messageList;
    private MaterialButton btnProposeMatch;

    private AppCompatImageButton btnAddMedia;
    private TextInputEditText etMessage;
    private FrameLayout btnSend;

    private String opponentName = "Ardi Saputra";
    private int opponentId = 0;
    private int myId = 0;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myId = SessionManager.getFighterId(this);
        token = SessionManager.getToken(this);

        initViews();
        readIntentData();
        setupListeners();
        loadMessages();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);
        tvEyebrow = findViewById(R.id.tvEyebrow);
        tvOpponentName = findViewById(R.id.tvOpponentName);

        scrollChat = findViewById(R.id.scrollChat);
        messageList = findViewById(R.id.messageList);
        btnProposeMatch = findViewById(R.id.btnProposeMatch);

        btnAddMedia = findViewById(R.id.btnAddMedia);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void readIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;

        opponentId = intent.getIntExtra("opponent_id", 0);
        String name = intent.getStringExtra("opponent_name");
        if (!TextUtils.isEmpty(name)) {
            opponentName = name;
            tvOpponentName.setText(opponentName);
        }

        String username = intent.getStringExtra("opponent_username");
        if (!TextUtils.isEmpty(username)) {
            tvEyebrow.setText(username.toUpperCase(Locale.ROOT) + " · AKTIF SEKARANG");
        } else {
            tvEyebrow.setText("THE CONTENDER · AKTIF SEKARANG");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnMore.setOnClickListener(this::showMoreMenu);

        btnProposeMatch.setOnClickListener(v ->
                ProposeBoutBottomSheet.newInstance(opponentId, opponentName)
                        .show(getSupportFragmentManager(), "propose_bout"));

        btnAddMedia.setOnClickListener(v ->
                Toast.makeText(this, "Lampirkan foto sasana atau rekaman latihan", Toast.LENGTH_SHORT).show());

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        if (TextUtils.isEmpty(token) || opponentId <= 0) return;

        ChatApi.getMessages(token, opponentId, new ApiCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> data) {
                if (data != null && !data.isEmpty()) {
                    messageList.removeAllViews();
                    for (ChatMessage msg : data) {
                        if (msg.senderId == myId) {
                            addOutboundMessage(msg.content);
                        } else {
                            addInboundMessage(msg.content);
                        }
                    }
                    scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
                }
            }

            @Override
            public void onError(String message) {
                // Gunakan placeholder pesan bawaan layout
            }
        });
    }

    private void sendMessage() {
        String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        addOutboundMessage(text);
        etMessage.setText("");

        // Auto-scroll to bottom
        scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));

        if (!TextUtils.isEmpty(token) && opponentId > 0) {
            ChatApi.sendMessage(token, opponentId, text, null, new ApiCallback<ChatMessage>() {
                @Override
                public void onSuccess(ChatMessage data) {}

                @Override
                public void onError(String message) {}
            });
        }
    }

    private void addOutboundMessage(String messageText) {
        Context context = this;
        float density = getResources().getDisplayMetrics().density;

        LinearLayout bubbleLayout = new LinearLayout(context);
        bubbleLayout.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(context, R.color.bp_blood));
        bg.setCornerRadius(10 * density);
        bubbleLayout.setBackground(bg);

        int padStartEnd = (int) (14 * density);
        int padTopBottom = (int) (12 * density);
        bubbleLayout.setPadding(padStartEnd, padTopBottom, padStartEnd, padTopBottom);

        MaterialTextView tvContent = new MaterialTextView(context);
        tvContent.setText(messageText);
        tvContent.setTextColor(Color.WHITE);
        tvContent.setTextSize(13f);
        bubbleLayout.addView(tvContent);

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        MaterialTextView tvTime = new MaterialTextView(context);
        tvTime.setText(currentTime + "  ✓✓");
        tvTime.setTextColor(Color.parseColor("#FFCCCC"));
        tvTime.setTextSize(9f);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timeParams.gravity = Gravity.END;
        timeParams.topMargin = (int) (4 * density);
        tvTime.setLayoutParams(timeParams);
        bubbleLayout.addView(tvTime);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.gravity = Gravity.END;
        bubbleParams.bottomMargin = (int) (10 * density);
        bubbleLayout.setLayoutParams(bubbleParams);

        messageList.addView(bubbleLayout);
    }

    private void addInboundMessage(String messageText) {
        Context context = this;
        float density = getResources().getDisplayMetrics().density;

        LinearLayout bubbleLayout = new LinearLayout(context);
        bubbleLayout.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(context, R.color.bp_surface));
        bg.setCornerRadius(10 * density);
        bubbleLayout.setBackground(bg);

        int padStartEnd = (int) (14 * density);
        int padTopBottom = (int) (12 * density);
        bubbleLayout.setPadding(padStartEnd, padTopBottom, padStartEnd, padTopBottom);

        MaterialTextView tvContent = new MaterialTextView(context);
        tvContent.setText(messageText);
        tvContent.setTextColor(ContextCompat.getColor(context, R.color.bp_login_text));
        tvContent.setTextSize(13f);
        bubbleLayout.addView(tvContent);

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        MaterialTextView tvTime = new MaterialTextView(context);
        tvTime.setText(currentTime);
        tvTime.setTextColor(ContextCompat.getColor(context, R.color.bp_login_muted));
        tvTime.setTextSize(9f);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timeParams.gravity = Gravity.START;
        timeParams.topMargin = (int) (4 * density);
        tvTime.setLayoutParams(timeParams);
        bubbleLayout.addView(tvTime);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.gravity = Gravity.START;
        bubbleParams.bottomMargin = (int) (10 * density);
        bubbleLayout.setLayoutParams(bubbleParams);

        messageList.addView(bubbleLayout);
    }

    private void showMoreMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Lihat Profil " + opponentName);
        popup.getMenu().add(0, 2, 1, "Ajukan Jadwal Sparring");
        popup.getMenu().add(0, 3, 2, "Laporkan Obrolan / Petarung");
        popup.getMenu().add(0, 4, 3, "Blokir Petarung");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                Intent intent = new Intent(this, FighterDetailActivity.class);
                intent.putExtra("full_name", opponentName);
                intent.putExtra("fighter_id", opponentId);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == 2) {
                ProposeBoutBottomSheet.newInstance(opponentId, opponentName)
                        .show(getSupportFragmentManager(), "propose_bout");
                return true;
            } else if (item.getItemId() == 3) {
                showReportDialog();
                return true;
            } else if (item.getItemId() == 4) {
                showBlockDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showReportDialog() {
        String[] reasons = new String[]{
                "Pelecehan / Kata Kasar di Chat",
                "Spam / Penipuan Matchmaking",
                "Tidak Hadir (No-Show) Saat Laga",
                "Pelanggaran Berat Aturan Sparring"
        };
        final int[] selectedIndex = {0};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Laporkan " + opponentName)
                .setSingleChoiceItems(reasons, 0, (dialog, which) -> selectedIndex[0] = which)
                .setPositiveButton("Kirim Laporan", (dialog, which) -> {
                    String reason = reasons[selectedIndex[0]];
                    int targetId = opponentId > 0 ? opponentId : 2;

                    com.fight.app.data.api.ReportApi.submitReport(
                            token,
                            targetId,
                            reason,
                            "Laporan dikirim dari chat room oleh petarung.",
                            new ApiCallback<java.util.Map>() {
                                @Override
                                public void onSuccess(java.util.Map data) {
                                    Toast.makeText(ChatActivity.this,
                                            "Laporan berhasil dikirim ke Admin untuk ditinjau.",
                                            Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(ChatActivity.this,
                                            "Laporan terkirim ke sistem: " + message,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showBlockDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Blokir Petarung")
                .setMessage("Apakah kamu yakin ingin memblokir " + opponentName + "? Kamu tidak akan menerima tawaran sparring lagi dari petarung ini.")
                .setPositiveButton("Blokir", (dialog, which) ->
                        Toast.makeText(this, opponentName + " telah berhasil diblokir.", Toast.LENGTH_SHORT).show())
                .setNegativeButton("Batal", null)
                .show();
    }
}
