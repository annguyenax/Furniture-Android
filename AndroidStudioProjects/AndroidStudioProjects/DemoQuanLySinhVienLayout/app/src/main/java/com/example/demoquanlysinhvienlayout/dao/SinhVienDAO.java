package com.example.demoquanlysinhvienlayout.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.demoquanlysinhvienlayout.database.DbHelper;
import com.example.demoquanlysinhvienlayout.model.SinhVien;
import com.example.demoquanlysinhvienlayout.helper.DateTimeHelper;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {
    private SQLiteDatabase db;

    public SinhVienDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(SinhVien sv) {
        ContentValues values = new ContentValues();
        values.put("id", sv.getId());
        values.put("hoten", sv.getHoten());
        values.put("ngaysinh", DateTimeHelper.toString(sv.getNgaysinh()));
        values.put("lophocid", sv.getLophocid());
        return db.insert("sinhviens", null, values);
    }

    public List<SinhVien> getByLop(int lophocid) {
        List<SinhVien> list = new ArrayList<>();
        Cursor cursor = db.query("sinhviens", null, "lophocid = ?", 
                new String[]{String.valueOf(lophocid)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    SinhVien sv = new SinhVien();
                    sv.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                    sv.setHoten(cursor.getString(cursor.getColumnIndexOrThrow("hoten")));
                    sv.setNgaysinh(DateTimeHelper.toDate(cursor.getString(cursor.getColumnIndexOrThrow("ngaysinh"))));
                    sv.setLophocid(cursor.getInt(cursor.getColumnIndexOrThrow("lophocid")));
                    list.add(sv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}