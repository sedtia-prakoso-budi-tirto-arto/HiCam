package com.example.fp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AlbumView extends AppCompatActivity {
    private DatabaseManager databaseManager;
    private TextView scrollText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view);


        LinearLayout layout = findViewById(R.id.albumLayout);

        // Mendapatkan semua subfolder di dalam "/storage/emulated/0/Pictures/hicam/"
        File hicamDir = new File("/storage/emulated/0/Pictures/hicam/");
        File[] subfolders = hicamDir.listFiles(File::isDirectory);

        if (subfolders != null) {
            for (File subfolder : subfolders) {
                // Membuat button dinamis untuk setiap subfolder
//                Toast.makeText(this, "folder :"+ subfolder.getName(), Toast.LENGTH_LONG).show();
                Button button = createButtonForFolder(subfolder.getName());
                layout.addView(button);

                // Menambahkan listener untuk button
                button.setOnClickListener(v -> openAlbum(subfolder.getName()));
            }
        }
    }

    private Button createButtonForFolder(String folderName) {
        Button button = new Button(this);
        button.setText(folderName);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        return button;
    }

    private void openAlbum(String folderName) {
        // Buat intent baru untuk album sesuai dengan folder
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra("folderName", folderName);
        startActivity(intent);
    }

    private void showDescription(String folderName) {
        Cursor dbGetDesc = databaseManager.getDesc(folderName);

        String deskripsi = null;
        if (dbGetDesc.moveToFirst()) {
            String deskripsiColumnName = "deskripsi";

            int deskripsiColumnIndex = dbGetDesc.getColumnIndex(deskripsiColumnName);

            if (deskripsiColumnIndex != -1) {
                deskripsi = dbGetDesc.getString(deskripsiColumnIndex);
                Log.d("Deskripsi", deskripsi);

                if (scrollText != null) {
                    scrollText.setText(deskripsi);
                } else {
                    Log.e("Error", "TextView not found in layout");
                }
            } else {
                Log.e("Error", "Column not found: " + deskripsiColumnName);
                if (scrollText != null) {
                    scrollText.setText("Album belum memiliki deskripsi");
                } else {
                    Log.e("Error", "TextView not found in layout");
                }
            }
        } else {
            Log.e("Error", "No data found for folder: " + folderName);
            if (scrollText != null) {
                scrollText.setText("Tidak ada deskripsi untuk album ini");
            } else {
                Log.e("Error", "TextView not found in layout");
            }
        }
    }
}
