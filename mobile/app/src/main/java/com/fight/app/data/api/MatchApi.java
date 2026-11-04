package com.fight.app.data.api;

import com.fight.app.data.model.OpponentCard;
import com.fight.app.data.model.SwipeResult;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Matchmaking & discovery API calls.
 */
public final class MatchApi {

    private static final Type OPPONENT_LIST_TYPE = new TypeToken<List<OpponentCard>>() {}.getType();
    private static final Type MATCH_LIST_TYPE = new TypeToken<List<com.fight.app.data.model.MatchItem>>() {}.getType();

    private MatchApi() {
    }

    public static void getMatches(
            String token,
            ApiCallback<List<com.fight.app.data.model.MatchItem>> callback
    ) {
        ApiClient.get("/match/matches", token, MATCH_LIST_TYPE, callback);
    }

    public static void getRecommendations(
            String token,
            boolean sameWeightClassOnly,
            int limit,
            ApiCallback<List<OpponentCard>> callback
    ) {
        String path = "/match/recommendations?limit=" + limit + "&same_weight_class_only=" + sameWeightClassOnly;
        ApiClient.get(path, token, OPPONENT_LIST_TYPE, callback);
    }

    public static void like(
            String token,
            int targetFighterId,
            ApiCallback<SwipeResult> callback
    ) {
        String path = "/match/" + targetFighterId + "/like";
        ApiClient.post(path, null, token, SwipeResult.class, callback);
    }

    public static void pass(
            String token,
            int targetFighterId,
            ApiCallback<SwipeResult> callback
    ) {
        String path = "/match/" + targetFighterId + "/pass";
        ApiClient.post(path, null, token, SwipeResult.class, callback);
    }
}
