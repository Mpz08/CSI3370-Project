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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            btnSelect.setText("Video Selected");
            Toast.makeText(this, "Video selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo() {
        if (videoUri == null) {
            Toast.makeText(this, "Select a video first", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fileName = "videos/" + uid + "_" + System.currentTimeMillis() + ".mp4";

        FirebaseStorage.getInstance().getReference()
                .child(fileName)
                .putFile(videoUri)
                .addOnSuccessListener(taskSnapshot ->
                        FirebaseStorage.getInstance().getReference()
                                .child(fileName)
                                .getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String videoUrl = uri.toString();

                                    Map<String, Object> video = new HashMap<>();
                                    video.put("title", title);
                                    video.put("description", description);
                                    video.put("videoUrl", videoUrl);
                                    video.put("userId", uid);
                                    video.put("timestamp", System.currentTimeMillis());
                                    video.put("likes", 0);

                                    FirebaseFirestore.getInstance()
                                            .collection("videos")
                                            .add(video)
                                            .addOnSuccessListener(doc -> {
                                                Toast.makeText(this, "Video uploaded!", Toast.LENGTH_SHORT).show();
                                                etTitle.setText("");
                                                etDescription.setText("");
                                                videoUri = null;
                                                btnSelect.setText("Tap to Select Video from Gallery");
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                            );
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}