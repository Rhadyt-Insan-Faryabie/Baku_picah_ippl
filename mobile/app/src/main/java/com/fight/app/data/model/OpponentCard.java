package com.fight.app.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * A recommended opponent with AI match score and reasons.
 */
public class OpponentCard implements Serializable {
    public Fighter fighter;
    public double score;
    public List<String> reasons;

    public int matchPercentage() {
        return (int) Math.round(score * 100);
    }

    public String displayScore() {
        return matchPercentage() + "% COCOK";
    }

    public String displayReasons() {
        if (reasons == null || reasons.isEmpty()) {
            return "Kelas berat, disiplin & pengalaman selaras.";
        }
        return String.join(" · ", reasons);
    }
}
