package com.example.learnkeep;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("learnkeep_session", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save login session
    public void saveLogin(String name, String email, String token) {

        editor.putBoolean("isLogin", true);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("token", token);
        editor.apply();
    }

    // Check login
    public boolean isLoggedIn() {
        return prefs.getBoolean("isLogin", false);
    }

    // Get user name
    public String getName() {
        return prefs.getString("name", "");
    }

    // Get user email
    public String getEmail() {
        return prefs.getString("email", "");
    }

    // Get JWT token
    public String getToken() {
        return prefs.getString("token", "");
    }

    // Logout
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
