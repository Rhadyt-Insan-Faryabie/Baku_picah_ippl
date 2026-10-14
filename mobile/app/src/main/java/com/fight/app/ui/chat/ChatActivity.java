package com.fight.app.ui.chat;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fight.app.R;
import com.fight.app.ui.bout.ProposeBoutBottomSheet;
import com.google.android.material.button.MaterialButton;

/**
 * 1-on-1 chat between matched fighters, with a sparring agreement banner
 * and a shortcut to propose a bout.
 */
public class ChatActivity extends AppCompatActivity {

    private LinearLayout messageList;
    private EditText etMessage;
    private int bubbleColor;
    private int textOnBlood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = findViewById(R.id.messageList);
        etMessage = findViewById(R.id.etMessage);
        MaterialButton btnSend = findViewById(R.id.btnSend);
        TextView tvPropose = findViewById(R.id.tvPropose);

        bubbleColor = getResources().getColor(R.color.bp_blood, getTheme());
        textOnBlood = getResources().getColor(R.color.bp_white, getTheme());

        btnSend.setOnClickListener(v -> sendMessage());
        tvPropose.setOnClickListener(v -> new ProposeBoutBottomSheet().show(
                getSupportFragmentManager(), "propose_bout"));
    }

    private void sendMessage() {
        String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setTextColor(textOnBlood);
        bubble.setTextSize(14f);
        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        bubble.setPadding(pad, pad, pad, pad);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bubbleColor);
        bg.setCornerRadius(24f * getResources().getDisplayMetrics().density);
        bubble.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        params.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
        bubble.setLayoutParams(params);

        messageList.addView(bubble);
        etMessage.setText("");
    }
}
