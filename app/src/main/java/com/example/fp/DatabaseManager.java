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
    public void insertLocAndDesc(String lokasi, String deskripsi) {
        Cursor cursor = getDesc(lokasi);
        if (cursor.getCount() > 0) {
            // Data dengan lokasi yang sama sudah ada, lakukan tindakan yang sesuai (misalnya, tidak menambahkannya lagi)
            cursor.close();
            return;  // Keluar dari metode
        }

//        cursor.close();
        ContentValues values = new ContentValues();
        values.put("lokasi", lokasi);
        values.put("deskripsi", deskripsi);
        database.insert("detail_lokasi", null, values);
    }

    // Metode untuk membaca data
    public Cursor getAllData() {
        return database.rawQuery("SELECT * FROM detail_lokasi", null);
    }

    public Cursor getDesc(String lokasi) {
        return database.rawQuery("SELECT deskripsi From detail_lokasi WHERE lokasi = ?", new String[]{lokasi});
    }

    // Metode untuk memperbarui data
    public void updateData(int id, String newName) {
        ContentValues values = new ContentValues();
        values.put("Name", newName);
        database.update("detail_lokasi", values, "ID=?", new String[]{String.valueOf(id)});
    }

    // Metode untuk menghapus data
    public void deleteData(int id) {
        database.delete("detail_lokasi", "ID=?", new String[]{String.valueOf(id)});
    }

}
