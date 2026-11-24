package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

/** 1-on-1 Chat message model. */
public class ChatMessage {
    public int id;

    @SerializedName("sender_id")
    public int senderId;

    @SerializedName("receiver_id")
    public int receiverId;

    @SerializedName("bout_id")
    public Integer boutId;

    public String content;

    @SerializedName("is_read")
    public boolean isRead;

    @SerializedName("created_at")
    public String createdAt;

    public Fighter sender;
    public Fighter receiver;
}
