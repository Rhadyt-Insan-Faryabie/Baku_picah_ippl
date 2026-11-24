package com.fight.app.data.api;

import com.fight.app.data.model.BoutItem;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BoutApi {

    private BoutApi() {}

    public static void listBouts(String token, String status, ApiCallback<List<BoutItem>> callback) {
        String path = "/bout/";
        if (status != null && !status.isEmpty()) {
            path += "?status=" + status;
        }
        Type listType = new TypeToken<List<BoutItem>>() {}.getType();
        ApiClient.get(path, token, listType, callback);
    }

    public static void createBout(
            String token,
            int opponentId,
            String location,
            String venueName,
            String notes,
            ApiCallback<BoutItem> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("opponent_id", opponentId);
        if (location != null) body.put("location", location);
        if (venueName != null) body.put("venue_name", venueName);
        if (notes != null) body.put("notes", notes);

        ApiClient.post("/bout/", body, token, BoutItem.class, callback);
    }

    public static void acceptBout(String token, int boutId, String notes, ApiCallback<BoutItem> callback) {
        Map<String, Object> body = new HashMap<>();
        if (notes != null) body.put("notes", notes);
        ApiClient.post("/bout/" + boutId + "/accept", body, token, BoutItem.class, callback);
    }

    public static void payDeposit(String token, int boutId, ApiCallback<BoutItem> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", 150000);
        ApiClient.post("/bout/" + boutId + "/deposit", body, token, BoutItem.class, callback);
    }

    public static void submitResult(
            String token,
            int boutId,
            Integer winnerId,
            String method,
            Integer round,
            String notes,
            Integer sportsmanshipRating,
            ApiCallback<BoutItem> callback) {
        Map<String, Object> body = new HashMap<>();
        if (winnerId != null) body.put("winner_id", winnerId);
        body.put("result_method", method != null ? method : "Decision");
        if (round != null) body.put("result_round", round);
        if (notes != null) body.put("result_notes", notes);
        if (sportsmanshipRating != null) body.put("sportsmanship_rating", sportsmanshipRating);

        ApiClient.post("/bout/" + boutId + "/result", body, token, BoutItem.class, callback);
    }
}
