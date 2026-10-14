package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

/** Response payload for login/register. */
public class AuthResponse {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("token_type")
    public String tokenType;

    public Fighter fighter;
}
