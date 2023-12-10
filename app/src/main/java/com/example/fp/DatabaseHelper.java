package com.example.fp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "HiCam", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Buat tabel jika belum ada
        String createTableQuery = "CREATE TABLE IF NOT EXISTS detail_lokasi (ID INTEGER PRIMARY KEY AUTOINCREMENT, lokasi TEXT, deskripsi TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
