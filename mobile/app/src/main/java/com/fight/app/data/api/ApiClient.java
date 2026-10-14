package com.fight.app.data.api;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Thin OkHttp wrapper used by the app to talk to the FIGHT backend.
 *
 * <p>Requests run on OkHttp's background threads; results are delivered on the
 * main thread so callers can update the UI directly.
 */
public final class ApiClient {

    /** 10.0.2.2 is the host machine as seen from the Android emulator. */
    public static final String BASE_URL = "http://10.0.2.2:8000/api";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    private static final Gson GSON = new Gson();

    private ApiClient() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static void get(String path, String token, Class<?> responseType, ApiCallback<?> callback) {
        Request request = baseBuilder(path, token).get().build();
        enqueue(request, responseType, callback);
    }

    public static void post(String path, Object body, String token, Class<?> responseType, ApiCallback<?> callback) {
        Request request = baseBuilder(path, token)
                .post(RequestBody.create(GSON.toJson(body), JSON))
                .build();
        enqueue(request, responseType, callback);
    }

    public static void put(String path, Object body, String token, Class<?> responseType, ApiCallback<?> callback) {
        Request request = baseBuilder(path, token)
                .put(RequestBody.create(GSON.toJson(body), JSON))
                .build();
        enqueue(request, responseType, callback);
    }

    private static Request.Builder baseBuilder(String path, String token) {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + path)
                .header("Accept", "application/json");
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    @SuppressWarnings("unchecked")
    private static void enqueue(Request request, Class<?> responseType, ApiCallback<?> callback) {
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postError((ApiCallback<Object>) callback, "Tidak dapat terhubung ke server");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (Response resp = response) {
                    String raw = resp.body() != null ? resp.body().string() : "";
                    if (!resp.isSuccessful()) {
                        postError((ApiCallback<Object>) callback, extractError(raw));
                        return;
                    }
                    Object parsed = GSON.fromJson(raw, responseType);
                    MAIN.post(() -> ((ApiCallback<Object>) callback).onSuccess(parsed));
                } catch (Exception e) {
                    postError((ApiCallback<Object>) callback, "Respons server tidak valid");
                }
            }
        });
    }

    private static void postError(ApiCallback<Object> callback, String message) {
        MAIN.post(() -> callback.onError(message));
    }

    /** Extract FastAPI's {@code detail} field into a readable message. */
    private static String extractError(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "Terjadi kesalahan pada server";
        }
        try {
            JsonElement root = JsonParser.parseString(raw);
            if (root.isJsonObject()) {
                JsonObject obj = root.getAsJsonObject();
                if (obj.has("detail")) {
                    JsonElement detail = obj.get("detail");
                    if (detail.isJsonPrimitive()) {
                        return detail.getAsString();
                    }
                    if (detail.isJsonArray()) {
                        JsonArray arr = detail.getAsJsonArray();
                        if (arr.size() > 0 && arr.get(0).isJsonObject()
                                && arr.get(0).getAsJsonObject().has("msg")) {
                            return arr.get(0).getAsJsonObject().get("msg").getAsString();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // fall through to generic message
        }
        return "Terjadi kesalahan pada server";
    }
}
