package com.furniture.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {
    private static final String PREF_NAME = "furniture_secure_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PROFILE_PIC = "profile_picture";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_SHIPPING_NAME = "shipping_name";
    private static final String KEY_SHIPPING_PHONE = "shipping_phone";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = createEncryptedPreferences(context);
    }

    private SharedPreferences createEncryptedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to plain SharedPreferences if encryption fails (e.g. old device)
            return context.getSharedPreferences(PREF_NAME + "_plain", Context.MODE_PRIVATE);
        }
    }

    public void saveUserSession(String accessToken, String refreshToken, int userId,
                               String username, String email, String phone, String firstName,
                               String lastName, String profilePicture, String role) {
        sharedPreferences.edit()
                .putString(KEY_USER_ROLE, normalizeRole(role))
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, phone)
                .putString(KEY_FIRST_NAME, firstName)
                .putString(KEY_LAST_NAME, lastName)
                .putString(KEY_PROFILE_PIC, profilePicture)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void saveUserSession(String accessToken, String refreshToken, int userId,
                               String username, String email, String phone, String firstName,
                               String lastName, String profilePicture) {
        saveUserSession(accessToken, refreshToken, userId, username, email, phone,
                firstName, lastName, profilePicture, "CUSTOMER");
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public String getFirstName() {
        return sharedPreferences.getString(KEY_FIRST_NAME, null);
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_NAME, null);
    }

    public String getProfilePicture() {
        return sharedPreferences.getString(KEY_PROFILE_PIC, null);
    }

    public void saveAvatarUrl(String url) {
        sharedPreferences.edit().putString(KEY_PROFILE_PIC, url).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        if (firstName != null && lastName != null) return firstName + " " + lastName;
        if (firstName != null) return firstName;
        return getUsername();
    }

    public String getUserEmail() { return getEmail(); }

    public void setAccessToken(String token) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public String getToken() { return getAccessToken(); }

    public String getUserPhone() { return getPhone(); }

    public String getUserAddress() {
        return sharedPreferences.getString(KEY_ADDRESS, null);
    }

    public void setUserAddress(String address) {
        sharedPreferences.edit().putString(KEY_ADDRESS, address).apply();
    }

    public void saveShippingInfo(String name, String phone, String address) {
        sharedPreferences.edit()
                .putString(KEY_SHIPPING_NAME, name)
                .putString(KEY_SHIPPING_PHONE, phone)
                .putString(KEY_ADDRESS, address)
                .apply();
    }

    public String getShippingName() {
        String name = sharedPreferences.getString(KEY_SHIPPING_NAME, null);
        return name != null ? name : getUserName();
    }

    public String getShippingPhone() {
        String phone = sharedPreferences.getString(KEY_SHIPPING_PHONE, null);
        return phone != null ? phone : getPhone();
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "CUSTOMER");
    }

    public boolean isAdmin() {
        return "ADMIN".equals(normalizeRole(getUserRole()));
    }

    private String normalizeRole(String role) {
        if (role == null) return "CUSTOMER";
        String normalized = role.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }
        return normalized.isEmpty() ? "CUSTOMER" : normalized;
    }

    public void updateUserInfo(String firstName, String lastName, String phone) {
        sharedPreferences.edit()
                .putString(KEY_FIRST_NAME, firstName)
                .putString(KEY_LAST_NAME, lastName)
                .putString(KEY_PHONE, phone)
                .apply();
    }
}
