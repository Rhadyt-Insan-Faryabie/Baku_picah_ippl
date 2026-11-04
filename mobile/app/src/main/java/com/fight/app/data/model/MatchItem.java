package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A mutual match between fighters (reciprocal likes) and associated conversation summary.
 */
public class MatchItem {

    public Fighter fighter;
    public double score;
    public List<String> reasons;

    @SerializedName("last_message")
    public String lastMessage;

    @SerializedName("last_message_time")
    public String lastMessageTime;

    @SerializedName("status_label")
    public String statusLabel;

    public boolean unread;

    public MatchItem() {
    }

    public MatchItem(Fighter fighter, double score, String lastMessage, String lastMessageTime, String statusLabel, boolean unread) {
        this.fighter = fighter;
        this.score = score;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.statusLabel = statusLabel;
        this.unread = unread;
    }
}
