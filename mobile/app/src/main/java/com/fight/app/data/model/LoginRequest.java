package com.fight.app.data.model;

/** Request body for POST /auth/login. */
public class LoginRequest {

    public final String email;
    public final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
