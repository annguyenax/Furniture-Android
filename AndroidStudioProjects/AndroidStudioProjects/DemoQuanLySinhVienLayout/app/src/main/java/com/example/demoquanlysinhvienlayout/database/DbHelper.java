package com.example.demoquanlysinhvienlayout.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "QLSinhVien";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String lophoc = "CREATE TABLE lophocs(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenlop TEXT NOT NULL)";

        String sinhvien = "CREATE TABLE sinhviens(" +
                "id TEXT PRIMARY KEY, " +
                "hoten TEXT NOT NULL, " +
                "ngaysinh TEXT, " +
                "lophocid INTEGER, " +
                "FOREIGN KEY(lophocid) REFERENCES lophocs(id))";

        db.execSQL(lophoc);
        db.execSQL(sinhvien);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS sinhviens");
        db.execSQL("DROP TABLE IF EXISTS lophocs");
        onCreate(db);
    }
}