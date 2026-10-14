package com.fight.app.data.api;

import com.fight.app.data.model.AuthResponse;
import com.fight.app.data.model.Fighter;
import com.fight.app.data.model.LoginRequest;
import com.fight.app.data.model.RegisterRequest;
import com.fight.app.data.model.UpdateProfileRequest;

/** Auth & profile endpoints. */
public final class AuthApi {

    private AuthApi() {
    }

    public static void login(String email, String password, ApiCallback<AuthResponse> callback) {
        ApiClient.post("/auth/login", new LoginRequest(email, password), null,
                AuthResponse.class, callback);
    }

    public static void register(RegisterRequest body, ApiCallback<AuthResponse> callback) {
        ApiClient.post("/auth/register", body, null, AuthResponse.class, callback);
    }

    public static void me(String token, ApiCallback<Fighter> callback) {
        ApiClient.get("/auth/me", token, Fighter.class, callback);
    }

    public static void updateProfile(String token, UpdateProfileRequest body,
                                     ApiCallback<Fighter> callback) {
        ApiClient.put("/fighters/me", body, token, Fighter.class, callback);
    }
}
