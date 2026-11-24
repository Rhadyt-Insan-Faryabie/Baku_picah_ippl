package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

/** Model for Bout representations from backend API. */
public class BoutItem {
    public int id;

    @SerializedName("challenger_id")
    public int challengerId;

    @SerializedName("opponent_id")
    public int opponentId;

    public Fighter challenger;
    public Fighter opponent;

    public String rules;
    public String location;

    @SerializedName("venue_name")
    public String venueName;

    @SerializedName("weight_class")
    public String weightClass;

    public String status;

    @SerializedName("proposed_at")
    public String proposedAt;

    @SerializedName("scheduled_at")
    public String scheduledAt;

    public String notes;

    @SerializedName("opponent_accepted")
    public boolean opponentAccepted;

    @SerializedName("deposit_amount")
    public int depositAmount;

    @SerializedName("challenger_deposit_paid")
    public boolean challengerDepositPaid;

    @SerializedName("opponent_deposit_paid")
    public boolean opponentDepositPaid;

    @SerializedName("winner_id")
    public Integer winnerId;

    @SerializedName("result_method")
    public String resultMethod;

    @SerializedName("result_round")
    public Integer resultRound;

    @SerializedName("result_notes")
    public String resultNotes;
}
