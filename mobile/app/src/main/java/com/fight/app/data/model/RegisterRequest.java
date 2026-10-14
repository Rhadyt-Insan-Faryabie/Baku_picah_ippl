package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Request body for POST /auth/register. */
public class RegisterRequest {

    public final String username;
    public final String email;
    public final String password;

    @SerializedName("full_name")
    public final String fullName;

    public final String city;

    @SerializedName("weight_kg")
    public final Double weightKg;

    @SerializedName("height_cm")
    public final Double heightCm;

    @SerializedName("martial_arts")
    public final List<String> martialArts;

    public RegisterRequest(String username, String email, String password, String fullName) {
        this(username, email, password, fullName, null, null, null, null);
    }

    public RegisterRequest(String username, String email, String password, String fullName,
                           String city, Double weightKg, Double heightCm, List<String> martialArts) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.city = city;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.martialArts = martialArts;
    }
}
