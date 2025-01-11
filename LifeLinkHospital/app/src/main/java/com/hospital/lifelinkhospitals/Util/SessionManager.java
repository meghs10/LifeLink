package com.hospital.lifelinkhospitals.Util;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hospital.lifelinkhospitals.Login;
import com.hospital.lifelinkhospitals.api.RetrofitClient;
import com.hospital.lifelinkhospitals.model.TokenRefreshRequest;
import com.hospital.lifelinkhospitals.model.TokenRefreshResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {
    private static final String PREF_NAME = "LifeLinkPrefs";

    // Authentication keys
    private static final String KEY_TOKEN = "userToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_TOKEN_TYPE = "tokenType";

    // User information keys
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_NAME = "userName";
    private static final String KEY_ROLE = "userRole";

    // Session state
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LAST_LOGIN = "lastLogin";

    private static  final String HOSPITAL_ID = "hospital_id";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user session after successful login/signup
     */
    public void saveUserSession(String token, String refreshToken, String email,
                                String userId, String name, String role) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_TOKEN_TYPE, "Bearer");
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Update user profile information
     */
    public void updateUserProfile(String name, String email) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void saveHospitalId(String hospitalId) {
        editor.putString(HOSPITAL_ID, hospitalId);
        editor.apply();
    }

    /**
     * Update authentication tokens
     */
    public void updateTokens(String token, String refreshToken) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    /**
     * Get the full authorization header value
     */
    public String getAuthorizationHeader() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }

    /**
     * Clear session and navigate to login
     */
    public void logout() {
        editor.clear();
        editor.apply();

        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Check if token needs refresh (e.g., if last login was more than 23 hours ago)
     */
    public boolean needsTokenRefresh() {
        long lastLogin = prefs.getLong(KEY_LAST_LOGIN, 0);
        long currentTime = System.currentTimeMillis();
        long hoursPassed = (currentTime - lastLogin) / (1000 * 60 * 60);
        return hoursPassed >= 23;
    }

    // Getters for session data
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin() {
        String role = getRole();
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    public String getHospitalId(){return prefs.getString(HOSPITAL_ID, null);}

    /**
     * Get user's display name (or email if name is not set)
     */
    public String getDisplayName() {
        String name = getName();
        return name != null && !name.isEmpty() ? name : getEmail();
    }

    /**
     * Clear all session data
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    /**
     * Get last login timestamp
     */
    public long getLastLoginTime() {
        return prefs.getLong(KEY_LAST_LOGIN, 0);
    }

    /**
     * Check if specific permission/role is granted
     */
    public boolean hasPermission(String requiredRole) {
        String userRole = getRole();
        if (userRole == null) return false;

        // Add your role hierarchy logic here
        if (userRole.equals("ADMIN")) return true;
        return userRole.equals(requiredRole);
    }

    /**
     * Update last login time
     */
    public void updateLastLoginTime() {
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Check if session is valid
     */
    public boolean isSessionValid() {
        return isLoggedIn() && getToken() != null;
    }
}