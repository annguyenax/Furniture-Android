package com.example.demoquanlysinhvienlayout.model;

import java.io.Serializable;
import java.util.Date;

public class SinhVien implements Serializable {
    private String id;
    private String hoten;
    private Date ngaysinh;
    private int lophocid;

    public SinhVien() {
    }

    public SinhVien(String id, String hoten, Date ngaysinh, int lophocid) {
        this.id = id;
        this.hoten = hoten;
        this.ngaysinh = ngaysinh;
        this.lophocid = lophocid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public Date getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(Date ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public int getLophocid() {
        return lophocid;
    }

    public void setLophocid(int lophocid) {
        this.lophocid = lophocid;
    }
}