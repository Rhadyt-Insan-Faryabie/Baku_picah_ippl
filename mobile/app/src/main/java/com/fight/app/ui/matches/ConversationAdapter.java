package com.fight.app.ui.matches;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.fight.app.R;
import com.fight.app.data.model.MatchItem;
import com.fight.app.ui.chat.ChatActivity;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(MatchItem item);
    }

    private final List<MatchItem> items = new ArrayList<>();
    private final OnConversationClickListener listener;

    public ConversationAdapter(OnConversationClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MatchItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageView ivAvatar;
        private final MaterialTextView tvName;
        private final MaterialTextView tvTime;
        private final MaterialTextView tvMessage;
        private final MaterialTextView tvStatus;
        private final View layoutStatusBadge;
        private final View viewUnreadDot;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            layoutStatusBadge = itemView.findViewById(R.id.layoutStatusBadge);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }

        void bind(MatchItem item, OnConversationClickListener listener) {
            String name = (item.fighter != null) ? item.fighter.displayName() : "Petarung";
            tvName.setText(name);

            tvTime.setText(item.lastMessageTime != null ? item.lastMessageTime : "Baru saja");
            tvMessage.setText(item.lastMessage != null ? item.lastMessage : "Siap sparring di ring?");

            String status = item.statusLabel != null ? item.statusLabel : "Mutual match";
            tvStatus.setText(status);

            // Dynamic badge styling based on Figma
            Context context = itemView.getContext();
            GradientDrawable badgeBg = new GradientDrawable();
            badgeBg.setCornerRadius(6 * context.getResources().getDisplayMetrics().density);

            if ("Proposal disepakati".equalsIgnoreCase(status)) {
                badgeBg.setColor(Color.parseColor("#2C1616"));
                tvStatus.setTextColor(Color.parseColor("#FF6666"));
            } else {
                badgeBg.setColor(Color.parseColor("#242424"));
                tvStatus.setTextColor(Color.parseColor("#A6A6A6"));
            }
            layoutStatusBadge.setBackground(badgeBg);

            viewUnreadDot.setVisibility(item.unread ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationClick(item);
                }
            });
        }
    }
}
