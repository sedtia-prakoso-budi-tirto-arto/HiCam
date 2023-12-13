package com.example.fp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class GaleriView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Wadah Utama
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // Menambahkan judul "GALERI" secara dinamis
        TextView titleTextView = new TextView(this);
        titleTextView.setText("GALERI");
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Set text size in sp
        titleTextView.setTypeface(null, Typeface.BOLD); // Set text style to bold
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, dpToPx(20), 0, 20); // Convert dp to pixels and adjust padding
        mainLayout.addView(titleTextView);

        // Wadah Scroll
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // Wadah Utama untuk subfolder
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        scrollView.addView(layout);

        // Mendapatkan semua subfolder di dalam "/storage/emulated/0/Pictures/hicam/"
        File hicamDir = new File("/storage/emulated/0/Pictures/hicam/");
        File[] subfolders = hicamDir.listFiles(File::isDirectory);

        if (subfolders != null) {
            for (File subfolder : subfolders) {
                // Membuat LinearLayout dinamis untuk setiap subfolder
                LinearLayout folderLayout = createLayoutForFolder(subfolder.getName());
                layout.addView(folderLayout);

                // Menambahkan listener untuk folderLayout
                folderLayout.setOnClickListener(v -> openAlbum(subfolder.getName()));
            }
        }

        // Menambahkan scrollView ke mainLayout
        mainLayout.addView(scrollView);

        // Set mainLayout sebagai content view
        setContentView(mainLayout);
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private LinearLayout createLayoutForFolder(String folderName) {
        LinearLayout folderLayout = new LinearLayout(this);
        folderLayout.setOrientation(LinearLayout.VERTICAL);
        folderLayout.setGravity(Gravity.CENTER);
        folderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Convert pixels to dp
        int widthInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                700,
                getResources().getDisplayMetrics()
        );
        int heightInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                225,
                getResources().getDisplayMetrics()
        );

        // Menambahkan ImageView untuk gambar (jika ada)
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.album1); // Ganti dengan sumber gambar yang sesuai
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                widthInDp,
                heightInDp
        ));
        folderLayout.addView(imageView);

        // Menambahkan TextView untuk nama folder
        TextView textView = new TextView(this);
        textView.setText(folderName);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        folderLayout.addView(textView);

        // Return folderLayout yang sudah berisi semua elemen anak
        return folderLayout;
    }

    private void openAlbum(String folderName) {
        // Buat intent baru untuk album sesuai dengan folder
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra("folderName", folderName);
        startActivity(intent);
    }
}
