package com.example.platemate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.CSI3370.recipedock.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_VIDEO = 1;

    private Uri videoUri;

    private EditText etTitle, etDescription;
    private Button btnSelect, btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

// --- YOUR UPLOAD LOGIC ---
        etTitle = findViewById(R.id.etVideoTitle);
        etDescription = findViewById(R.id.etVideoDescription);
        btnSelect = findViewById(R.id.btnSelectVideo);
        btnPost = findViewById(R.id.btnPostVideo);

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
        });

        btnPost.setOnClickListener(v -> uploadVideo());


// --- TEAMMATE NAVIGATION UI ---
        android.widget.Button navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(UploadActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        android.widget.Button navTrending = findViewById(R.id.navTrending);
        navTrending.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(UploadActivity.this, TrendingActivity.class);
            startActivity(intent);
        });

        android.widget.Button navProfile = findViewById(R.id.navProfile);
        navProfile.setOnClickListener(v -> {
        });
    }
    private void uploadVideo() {
        if (videoUri == null) {
            android.widget.Toast.makeText(this, "Select a video first", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            android.widget.Toast.makeText(this, "Enter a title", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
            android.widget.Toast.makeText(this, "User not logged in", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fileName = "videos/" + uid + "_" + System.currentTimeMillis() + ".mp4";

        com.google.firebase.storage.FirebaseStorage.getInstance().getReference()
                .child(fileName)
                .putFile(videoUri)
                .addOnSuccessListener(taskSnapshot ->
                        com.google.firebase.storage.FirebaseStorage.getInstance().getReference()
                                .child(fileName)
                                .getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String videoUrl = uri.toString();

                                    java.util.Map<String, Object> video = new java.util.HashMap<>();
                                    video.put("title", title);
                                    video.put("description", description);
                                    video.put("videoUrl", videoUrl);
                                    video.put("userId", uid);
                                    video.put("timestamp", System.currentTimeMillis());
                                    video.put("likes", 0);

                                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                            .collection("videos")
                                            .add(video)
                                            .addOnSuccessListener(doc -> {
                                                android.widget.Toast.makeText(this, "Video uploaded!", android.widget.Toast.LENGTH_SHORT).show();
                                                etTitle.setText("");
                                                etDescription.setText("");
                                                videoUri = null;
                                                btnSelect.setText("Tap to Select Video");
                                            })
                                            .addOnFailureListener(e ->
                                                    android.widget.Toast.makeText(this, "Firestore error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show()
                                            );
                                })
                                .addOnFailureListener(e ->
                                        android.widget.Toast.makeText(this, "URL error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show()
                                )
                )
                .addOnFailureListener(e ->
                        android.widget.Toast.makeText(this, "Upload failed: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show()
                );
    }
}