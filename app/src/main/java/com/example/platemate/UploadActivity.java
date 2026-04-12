package com.example.platemate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UploadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        // 1. Home Button (Takes you back to MainActivity)
        android.widget.Button navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(UploadActivity.this, MainActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 2. Trending Button
        android.widget.Button navTrending = findViewById(R.id.navTrending);
        navTrending.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(UploadActivity.this, TrendingActivity.class);
                startActivity(intent);
            }
        });

        // 3. Profile Button
        android.widget.Button navProfile = findViewById(R.id.navProfile);
        navProfile.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
            }
        });

    }
}