package com.example.platemate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.CSI3370.recipedock.R;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // 1. Submit Button (Takes you to the Home Screen)
        android.widget.Button btnSubmitCreateAccount = findViewById(R.id.btnSubmitCreateAccount);
        btnSubmitCreateAccount.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(CreateAccountActivity.this, MainActivity.class);
                // Clear the back stack so they can't hit "back" and return to the signup page
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // 2. Go Back Button (Drops you back to the Login screen)
        android.widget.Button btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBackToLogin.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // Destroys this page and returns to LoginActivity
                finish();
            }
        });
    }
}