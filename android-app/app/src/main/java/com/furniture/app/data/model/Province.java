package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Province {

    @SerializedName("code")
    private int code;

    @SerializedName("name")
    private String name;

    @SerializedName("districts")
    private List<District> districts;

    public int getCode() { return code; }
    public String getName() { return name; }
    public List<District> getDistricts() { return districts; }

    @Override
    public String toString() { return name != null ? name : ""; }

    // ── nested District ──────────────────────────────────────────────────────

    public static class District {

        @SerializedName("code")
        private int code;

        @SerializedName("name")
        private String name;

        @SerializedName("wards")
        private List<Ward> wards;

        public int getCode() { return code; }
        public String getName() { return name; }
        public List<Ward> getWards() { return wards; }

        @Override
        public String toString() { return name != null ? name : ""; }
    }

    // ── nested Ward ──────────────────────────────────────────────────────────

    public static class Ward {

        @SerializedName("code")
        private int code;

        @SerializedName("name")
        private String name;

        public int getCode() { return code; }
        public String getName() { return name; }

        @Override
        public String toString() { return name != null ? name : ""; }
    }
}
