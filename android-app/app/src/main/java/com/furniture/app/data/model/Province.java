package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Province {

    @SerializedName("id")
    private String id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("data")
    private List<District> districts;

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public List<District> getDistricts() { return districts; }

    @Override
    public String toString() { return fullName != null ? fullName : ""; }

    // ── nested District ──────────────────────────────────────────────────────

    public static class District {

        @SerializedName("id")
        private String id;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("data")
        private List<Ward> wards;

        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public List<Ward> getWards() { return wards; }

        @Override
        public String toString() { return fullName != null ? fullName : ""; }
    }

    // ── nested Ward ──────────────────────────────────────────────────────────

    public static class Ward {

        @SerializedName("id")
        private String id;

        @SerializedName("full_name")
        private String fullName;

        public String getId() { return id; }
        public String getFullName() { return fullName; }

        @Override
        public String toString() { return fullName != null ? fullName : ""; }
    }
}
