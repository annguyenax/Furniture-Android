package com.furniture.app.util;

import android.widget.EditText;

public final class InputValidator {

    private InputValidator() {}

    public static boolean validateEmail(EditText field) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError("Vui lòng nhập email");
            field.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            field.setError("Email không hợp lệ");
            field.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validatePassword(EditText field, int minLength) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError("Vui lòng nhập mật khẩu");
            field.requestFocus();
            return false;
        }
        if (value.length() < minLength) {
            field.setError("Mật khẩu phải từ " + minLength + " ký tự");
            field.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validateRequired(EditText field, String fieldName) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError("Vui lòng nhập " + fieldName);
            field.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validateMinLength(EditText field, String fieldName, int minLength) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError("Vui lòng nhập " + fieldName);
            field.requestFocus();
            return false;
        }
        if (value.length() < minLength) {
            field.setError(fieldName + " phải từ " + minLength + " ký tự");
            field.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validatePhone(EditText field) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError("Vui lòng nhập số điện thoại");
            field.requestFocus();
            return false;
        }
        if (!value.matches("^(0|\\+84)[0-9]{9,10}$")) {
            field.setError("Số điện thoại không hợp lệ (VD: 0901234567)");
            field.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validatePasswordMatch(EditText password, EditText confirm) {
        String p = password.getText().toString().trim();
        String c = confirm.getText().toString().trim();
        if (!p.equals(c)) {
            confirm.setError("Mật khẩu xác nhận không khớp");
            confirm.requestFocus();
            return false;
        }
        return true;
    }

    public static String getText(EditText field) {
        return field.getText().toString().trim();
    }
}
