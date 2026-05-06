package com.example.demogridviewnangcao;

import java.io.Serializable;

public class HinhAnh implements Serializable {
    private int Hinh;
    private String Ten;
    public HinhAnh(int hinh, String ten)
    {
        Hinh = hinh;
        Ten = ten;
    }
    public int getHinh() {
        return Hinh;
    }
    public void setHinh(int hinh) {
        Hinh = hinh;
    }
    public String getTen() {
        return Ten;
    }
    public void setTen(String ten) {
        Ten = ten;
    }
}
