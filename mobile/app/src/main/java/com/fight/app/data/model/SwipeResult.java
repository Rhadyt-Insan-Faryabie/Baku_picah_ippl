package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Result of a like/pass swipe on an opponent.
 */
public class SwipeResult {
    public String action;

    @SerializedName("target_id")
    public int targetId;

    public boolean matched;

    public Fighter opponent;
}
