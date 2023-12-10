package com.example.fp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private SQLiteDatabase database;

    // Inisialisasi DatabaseManager dengan DatabaseHelper
    public DatabaseManager(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // Metode untuk menambahkan data
    public void insertData(String name) {
        ContentValues values = new ContentValues();
        values.put("Name", name);
        database.insert("MyTable", null, values);
    }

    // Metode untuk membaca data
    public Cursor getAllData() {
        return database.rawQuery("SELECT * FROM MyTable", null);
    }

    // Metode untuk memperbarui data
    public void updateData(int id, String newName) {
        ContentValues values = new ContentValues();
        values.put("Name", newName);
        database.update("MyTable", values, "ID=?", new String[]{String.valueOf(id)});
    }

    // Metode untuk menghapus data
    public void deleteData(int id) {
        database.delete("MyTable", "ID=?", new String[]{String.valueOf(id)});
    }
}
