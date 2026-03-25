package com.example.platemate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.content.res.ColorStateList;

public class VideoDetailActivity extends AppCompatActivity {
    boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        Button btnFollow = findViewById(R.id.btnFollow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFollowing) {
                    btnFollow.setText("Follow");
                    btnFollow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
                    isFollowing = false;
                } else {
                    btnFollow.setText("Following");
                    btnFollow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
                    isFollowing = true;
                }
            }
        });
    }
}