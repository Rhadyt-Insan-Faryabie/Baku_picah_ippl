package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Request body for PUT /fighters/me (all fields optional). */
public class UpdateProfileRequest {

    @SerializedName("full_name")
    public final String fullName;

    public final String city;

    @SerializedName("weight_kg")
    public final Double weightKg;

    @SerializedName("height_cm")
    public final Double heightCm;

    @SerializedName("martial_arts")
    public final List<String> martialArts;

    public final Integer wins;
    public final Integer losses;
    public final Integer draws;

    public UpdateProfileRequest(String fullName, String city, Double weightKg,
                                Double heightCm, List<String> martialArts) {
        this(fullName, city, weightKg, heightCm, martialArts, null, null, null);
    }

    public UpdateProfileRequest(String fullName, String city, Double weightKg,
                                Double heightCm, List<String> martialArts,
                                Integer wins, Integer losses, Integer draws) {
        this.fullName = fullName;
        this.city = city;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.martialArts = martialArts;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }
}
