package com.example.fp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View kameraView = findViewById(R.id.kameraView);
        kameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKameraView();
            }
        });

        View camView = findViewById(R.id.camView);
        camView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKameraView();
            }
        });
    }

    private void openKameraView() {
        Intent kamera = new Intent(this, KameraView.class);
        startActivity(kamera);
    }
}