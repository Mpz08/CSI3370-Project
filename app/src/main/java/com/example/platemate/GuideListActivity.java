package com.example.platemate;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.CSI3370.recipedock.R;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;


public class GuideListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guide_list);

        // 1. Find the back button we just added to the XML
        android.widget.Button btnGoBack = findViewById(R.id.btnGoBack);

        // 2. Tell the button to listen for a click
        btnGoBack.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // 3. Close this Search screen and drop back to the Main screen!
                finish();
            }
        });

    }

}