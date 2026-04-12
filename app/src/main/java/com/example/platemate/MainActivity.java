package com.example.platemate;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        android.widget.Button btnFind = findViewById(R.id.btnFindGuide);
        btnFind.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, GuideListActivity.class);
                startActivity(intent);
            }
        });

        android.widget.Button btnShareSkill = findViewById(R.id.btnShareSkill);
        btnShareSkill.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        // 1. Trending Button
        android.widget.Button navTrending = findViewById(R.id.navTrending);
        navTrending.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, TrendingActivity.class);
                startActivity(intent);
            }
        });

        // 2. Upload Button
        android.widget.Button navUpload = findViewById(R.id.navUpload);
        navUpload.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, UploadActivity.class);
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