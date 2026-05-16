package com.furniture.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "furniture_app_pref";
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

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession(String accessToken, String refreshToken, int userId,
                               String username, String email, String phone, String firstName,
                               String lastName, String profilePicture, String role) {
        editor.putString(KEY_USER_ROLE, role != null ? role : "CUSTOMER");
        saveUserSession(accessToken, refreshToken, userId, username, email, phone, firstName, lastName, profilePicture);
    }

    public void saveUserSession(String accessToken, String refreshToken, int userId,
                               String username, String email, String phone, String firstName,
                               String lastName, String profilePicture) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_PROFILE_PIC, profilePicture);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
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

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else {
            return getUsername();
        }
    }

    public String getUserEmail() {
        return getEmail();
    }

    public void setAccessToken(String token) {
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return getAccessToken();
    }

    public String getUserPhone() {
        return getPhone();
    }

    public String getUserAddress() {
        return sharedPreferences.getString("address", null);
    }

    public void setUserAddress(String address) {
        editor.putString("address", address);
        editor.apply();
    }

    public void saveShippingInfo(String name, String phone, String address) {
        editor.putString("shipping_name", name);
        editor.putString("shipping_phone", phone);
        editor.putString("address", address);
        editor.apply();
    }

    public String getShippingName() {
        String name = sharedPreferences.getString("shipping_name", null);
        return name != null ? name : getUserName();
    }

    public String getShippingPhone() {
        String phone = sharedPreferences.getString("shipping_phone", null);
        return phone != null ? phone : getPhone();
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "CUSTOMER");
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getUserRole());
    }

    public boolean isVendor() {
        return "VENDOR".equals(getUserRole());
    }

    public void updateUserInfo(String firstName, String lastName, String phone) {
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }
}
