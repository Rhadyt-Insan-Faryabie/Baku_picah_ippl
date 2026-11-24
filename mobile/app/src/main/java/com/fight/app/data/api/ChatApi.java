package com.fight.app.data.api;

import com.fight.app.data.model.ChatMessage;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ChatApi {

    private ChatApi() {}

    public static void getMessages(String token, int partnerId, ApiCallback<List<ChatMessage>> callback) {
        Type listType = new TypeToken<List<ChatMessage>>() {}.getType();
        ApiClient.get("/chat/" + partnerId + "/messages", token, listType, callback);
    }

    public static void sendMessage(
            String token,
            int partnerId,
            String content,
            Integer boutId,
            ApiCallback<ChatMessage> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("receiver_id", partnerId);
        body.put("content", content);
        if (boutId != null) {
            body.put("bout_id", boutId);
        }
        ApiClient.post("/chat/" + partnerId + "/messages", body, token, ChatMessage.class, callback);
    }
}
