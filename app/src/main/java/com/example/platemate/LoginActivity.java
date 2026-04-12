package com.example.platemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.CSI3370.recipedock.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

// 1. Login Button → go to Trending (NOT MainActivity)
        android.widget.Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(LoginActivity.this, TrendingActivity.class);
                startActivity(intent);
                finish();
            }
        });

// 2. Create Account Button
        android.widget.Button btnGoToCreate = findViewById(R.id.btnGoToCreate);
        btnGoToCreate.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

    }
}