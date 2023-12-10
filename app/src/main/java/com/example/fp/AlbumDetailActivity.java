package com.example.fp;

import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity {
    GridView grid;

    List<Uri> imageUris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);


        // Menerima nama folder dari intent
        String folderName = getIntent().getStringExtra("folderName");
//        folderNameTextView.setText(folderName);


        imageUris = getImagesInFolder(folderName);

        if (!imageUris.isEmpty()) {
            CustomGrid adapter = new CustomGrid(AlbumDetailActivity.this, imageUris);
            grid = (GridView) findViewById(R.id.albumDetailLayout);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showImageDialog(imageUris.get(position));

                }
            });

        }
        else {
            showToast("Tidak ada gambar terdeteksi");
        }

    }

    private void showImageDialog(Uri imageUri) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.grid_item_layout);

        PhotoView photoView = dialog.findViewById(R.id.photo_view);
        Glide.with(this)
                .load(imageUri)
                .into(photoView);

        dialog.show();
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

//

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

