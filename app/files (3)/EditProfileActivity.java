package com.example.cookfeed.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cookfeed.R;
import com.example.cookfeed.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private FirebaseFirestore db;
    private String currentUid;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.imgProfilePic.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadCurrentProfile();

        binding.imgProfilePic.setOnClickListener(v -> imagePicker.launch("image/*"));
        binding.btnChangePic.setOnClickListener(v -> imagePicker.launch("image/*"));
        binding.btnSave.setOnClickListener(v -> saveProfile());
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadCurrentProfile() {
        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    binding.etDisplayName.setText(doc.getString("displayName"));
                    binding.etUsername.setText(doc.getString("username"));
                    binding.etBio.setText(doc.getString("bio"));

                    String picUrl = doc.getString("profilePicUrl");
                    if (picUrl != null && !picUrl.isEmpty()) {
                        Glide.with(this).load(picUrl).circleCrop()
                                .placeholder(R.drawable.ic_default_avatar)
                                .into(binding.imgProfilePic);
                    }
                });
    }

    private void saveProfile() {
        String displayName = binding.etDisplayName.getText().toString().trim();
        String username    = binding.etUsername.getText().toString().trim().toLowerCase();
        String bio         = binding.etBio.getText().toString().trim();

        if (displayName.isEmpty()) {
            binding.tilDisplayName.setError("Display name cannot be empty");
            return;
        }
        if (username.length() < 3) {
            binding.tilUsername.setError("Username must be at least 3 characters");
            return;
        }

        setLoading(true);

        if (selectedImageUri != null) {
            // Upload image first, then save profile
            uploadProfilePic(displayName, username, bio);
        } else {
            updateFirestore(displayName, username, bio, null);
        }
    }

    private void uploadProfilePic(String displayName, String username, String bio) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("profile_pics/" + currentUid + ".jpg");
        ref.putFile(selectedImageUri)
                .addOnSuccessListener(snap ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                updateFirestore(displayName, username, bio, uri.toString())))
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFirestore(String displayName, String username, String bio, String picUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", displayName);
        updates.put("username", username);
        updates.put("bio", bio);
        if (picUrl != null) updates.put("profilePicUrl", picUrl);

        db.collection("users").document(currentUid)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean loading) {
        binding.btnSave.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
