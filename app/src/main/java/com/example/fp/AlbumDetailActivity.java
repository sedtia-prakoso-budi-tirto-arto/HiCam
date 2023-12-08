package com.example.fp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        GridLayout layout = findViewById(R.id.albumDetailLayout);
        TextView folderNameTextView = findViewById(R.id.folderNameTextView);

        // Menerima nama folder dari intent
        String folderName = getIntent().getStringExtra("folderName");
        folderNameTextView.setText(folderName);

        List<Uri> imageUris = getImagesInFolder(folderName);

//        if (!imageUris.isEmpty()) {
//            int columnCount = 3;
//            int rowCount = (imageUris.size() + columnCount - 1) / columnCount;
//
//            layout.setColumnCount(columnCount);
//            layout.setRowCount(rowCount);
//
//            for (int i = 0; i < imageUris.size(); i++) {
//                Uri imageUri = imageUris.get(i);
//                // Membuat ImageView dinamis untuk setiap file
//                ImageView imageView = createImageViewForUri(imageUri);
//                layout.addView(imageView);
//
//                // Menentukan baris dan kolom untuk setiap gambar
//                GridLayout.Spec rowSpec = GridLayout.spec(i / columnCount);
//                GridLayout.Spec colSpec = GridLayout.spec(i % columnCount);
//                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, colSpec);
//                imageView.setLayoutParams(layoutParams);
//            }
//        } else {
//            Toast.makeText(this, "Tidak ada gambar", Toast.LENGTH_LONG).show();
//        }
        if (!imageUris.isEmpty()) {
            int columnCount = 3;
            int rowCount = (imageUris.size() + columnCount - 1) / columnCount;

            layout.setColumnCount(columnCount);
            layout.setRowCount(rowCount);

            for (int i = 0; i < imageUris.size(); i++) {
                Uri imageUri = imageUris.get(i);
                // Membuat ImageView dinamis untuk setiap file
                ImageView imageView = createImageViewForUri(imageUri);
                layout.addView(imageView);
            }
        }

    }



    private List<Uri> getImagesInFolder(String folderName) {
        List<Uri> imageUris = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Proyeksi untuk mendapatkan data file gambar
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{"%/" + folderName + "/%"};

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(columnIndex);
                Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
                imageUris.add(imageUri);
            }
            cursor.close();
        }

        return imageUris;
    }
    private ImageView createImageViewForFile(File file) {
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(android.net.Uri.fromFile(file));
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return imageView;
    }

    private ImageView createImageViewForUri(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);

        // Set layout parameters
        GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
        GridLayout.Spec colSpec = GridLayout.spec(GridLayout.UNDEFINED);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, colSpec);

        // Set width and height
        layoutParams.width = dpToPx(90); // Convert dp to pixels
        layoutParams.height = dpToPx(120); // Convert dp to pixels

        // Set margin
        int margin = dpToPx(20); // Convert dp to pixels
        layoutParams.setMargins(margin, margin, margin, margin);

        imageView.setLayoutParams(layoutParams);

        return imageView;

    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

