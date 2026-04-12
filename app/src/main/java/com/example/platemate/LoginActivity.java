package com.example.platemate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Wire the Login Button (Goes to Main)
        android.widget.Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // This prevents the user from hitting "back" to go to the login screen after logging in
            }
        });

        // 2. Wire the Create Account Button (Goes to new Create Account page)
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