package com.example.demoquanlysinhvienlayout.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.demoquanlysinhvienlayout.database.DbHelper;
import com.example.demoquanlysinhvienlayout.model.LopHoc;
import java.util.ArrayList;
import java.util.List;

public class LopHocDAO {
    private SQLiteDatabase db;

    public LopHocDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(LopHoc lopHoc) {
        ContentValues values = new ContentValues();
        values.put("tenlop", lopHoc.getTenlophoc());
        return db.insert("lophocs", null, values);
    }

    public List<LopHoc> getAll() {
        List<LopHoc> list = new ArrayList<>();
        Cursor cursor = db.query("lophocs", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String tenlop = cursor.getString(cursor.getColumnIndexOrThrow("tenlop"));
                list.add(new LopHoc(id, tenlop));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int delete(int id) {
        return db.delete("lophocs", "id = ?", new String[]{String.valueOf(id)});
    }
}