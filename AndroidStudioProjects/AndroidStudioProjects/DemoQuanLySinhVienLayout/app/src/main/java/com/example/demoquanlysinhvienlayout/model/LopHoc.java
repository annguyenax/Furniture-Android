package com.example.demoquanlysinhvienlayout.model;

import java.io.Serializable;

public class LopHoc implements Serializable {
    private int id;
    private String tenlophoc;

    public LopHoc() {
    }

    public LopHoc(int id, String tenlophoc) {
        this.id = id;
        this.tenlophoc = tenlophoc;
    }

    public LopHoc(String tenlophoc) {
        this.tenlophoc = tenlophoc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenlophoc() {
        return tenlophoc;
    }

    public void setTenlophoc(String tenlophoc) {
        this.tenlophoc = tenlophoc;
    }

    @Override
    public String toString() {
        return tenlophoc;
    }
}