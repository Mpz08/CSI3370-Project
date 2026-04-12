package com.example.platemate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.CSI3370.recipedock.R;

public class TrendingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        // 1. Home Button (Takes you back to MainActivity)
        android.widget.Button navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(TrendingActivity.this, MainActivity.class);
                // Clear the back stack so we don't pile up endless pages
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 2. Upload Button
        android.widget.Button navUpload = findViewById(R.id.navUpload);
        navUpload.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(TrendingActivity.this, UploadActivity.class);
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