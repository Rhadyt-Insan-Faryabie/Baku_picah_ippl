package com.fight.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** A fighter profile as returned by the backend. */
public class Fighter {

    public int id;
    public String username;
    public String email;
    public String role;

    @SerializedName("full_name")
    public String fullName;

    public String city;

    @SerializedName("weight_kg")
    public Double weightKg;

    @SerializedName("height_cm")
    public Double heightCm;

    @SerializedName("weight_class")
    public String weightClass;

    /** Background martial arts, e.g. ["Boxing", "BJJ"]. Rules are always MMA. */
    @SerializedName("martial_arts")
    public List<String> martialArts;

    public int wins;
    public int losses;
    public int draws;

    public String displayName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return username;
    }

    public String record() {
        return wins + "-" + losses + "-" + draws;
    }
}
