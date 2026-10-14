package com.fight.app.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Persists the auth token and basic user info in SharedPreferences.
 */
public final class SessionManager {

    private static final String PREF_NAME = "bp_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "full_name";

    private SessionManager() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void save(Context context, String token, int userId, String username, String fullName) {
        prefs(context).edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_FULL_NAME, fullName)
                .apply();
    }

    public static String getToken(Context context) {
        return prefs(context).getString(KEY_TOKEN, null);
    }

    public static String getFullName(Context context) {
        return prefs(context).getString(KEY_FULL_NAME, "");
    }

    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return token != null && !token.isEmpty();
    }

    public static void clear(Context context) {
        prefs(context).edit().clear().apply();
    }
}
