package com.example.cookfeed.profile;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.cookfeed.adapters.TagUserAdapter;
import com.example.cookfeed.databinding.ActivityCreatePostBinding;
import com.example.cookfeed.models.Post;
import com.example.cookfeed.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private static final int MAX_VIDEO_DURATION_MS = 3 * 60 * 1000; // 3 minutes

    private ActivityCreatePostBinding binding;
    private FirebaseFirestore db;
    private String currentUid;
    private Uri selectedMediaUri = null;
    private String mediaType = null; // "photo" or "video"
    private final List<User> tagSuggestions = new ArrayList<>();
    private final List<String> taggedUserIds = new ArrayList<>();
    private TagUserAdapter tagAdapter;

    // Photo picker
    private final ActivityResultLauncher<String> photoPicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedMediaUri = uri;
                    mediaType = "photo";
                    showMediaPreview(uri, false);
                }
            });

    // Video picker
    private final ActivityResultLauncher<String> videoPicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    if (!isVideoWithinLimit(uri)) {
                        Toast.makeText(this, "Video must be 3 minutes or less.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    selectedMediaUri = uri;
                    mediaType = "video";
                    showMediaPreview(uri, true);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnAddPhoto.setOnClickListener(v -> photoPicker.launch("image/*"));
        binding.btnAddVideo.setOnClickListener(v -> videoPicker.launch("video/*"));

        binding.btnPost.setOnClickListener(v -> submitPost());

        // Tag/mention suggestions
        tagAdapter = new TagUserAdapter(this, tagSuggestions, user -> {
            // User selected from suggestions
            if (!taggedUserIds.contains(user.getUid())) {
                taggedUserIds.add(user.getUid());
                addTagChip(user.getUsername());
            }
            binding.etTag.setText("");
            binding.recyclerTagSuggestions.setVisibility(View.GONE);
        });
        binding.recyclerTagSuggestions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerTagSuggestions.setAdapter(tagAdapter);

        // Listen for @mentions in description
        binding.etDescription.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                int atIdx = text.lastIndexOf("@");
                if (atIdx >= 0) {
                    String query = text.substring(atIdx + 1).toLowerCase();
                    if (!query.isEmpty()) searchUsers(query);
                } else {
                    binding.recyclerTagSuggestions.setVisibility(View.GONE);
                }
            }
        });

        // Separate tag field
        binding.etTag.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                if (!query.isEmpty()) searchUsers(query);
                else binding.recyclerTagSuggestions.setVisibility(View.GONE);
            }
        });
    }

    private boolean isVideoWithinLimit(Uri uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();
            long duration = Long.parseLong(durationStr);
            return duration <= MAX_VIDEO_DURATION_MS;
        } catch (Exception e) {
            return true; // If we can't check, allow it
        }
    }

    private void showMediaPreview(Uri uri, boolean isVideo) {
        binding.mediaPreviewContainer.setVisibility(View.VISIBLE);
        binding.videoIndicator.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        Glide.with(this).load(uri).centerCrop().into(binding.imgPreview);
    }

    private void searchUsers(String query) {
        db.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(5)
                .get()
                .addOnSuccessListener(snap -> {
                    tagSuggestions.clear();
                    for (QueryDocumentSnapshot doc : snap) {
                        User u = doc.toObject(User.class);
                        if (!u.getUid().equals(currentUid)) {
                            tagSuggestions.add(u);
                        }
                    }
                    tagAdapter.notifyDataSetChanged();
                    binding.recyclerTagSuggestions.setVisibility(
                            tagSuggestions.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }

    private void addTagChip(String username) {
        // Append @username chip text to the tag display
        String current = binding.tvTaggedUsers.getText().toString();
        binding.tvTaggedUsers.setText(current.isEmpty()
                ? "@" + username
                : current + "  @" + username);
        binding.tvTaggedUsers.setVisibility(View.VISIBLE);
    }

    private void submitPost() {
        String description = binding.etDescription.getText().toString().trim();

        if (selectedMediaUri == null) {
            Toast.makeText(this, "Please select a photo or video.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        // Upload media to Firebase Storage
        String fileName = UUID.randomUUID().toString();
        String path = mediaType.equals("photo")
                ? "posts/photos/" + fileName + ".jpg"
                : "posts/videos/" + fileName + ".mp4";

        StorageReference ref = FirebaseStorage.getInstance().getReference(path);
        ref.putFile(selectedMediaUri)
                .addOnSuccessListener(snap ->
                        ref.getDownloadUrl().addOnSuccessListener(downloadUri ->
                                savePostToFirestore(downloadUri.toString(), description)))
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void savePostToFirestore(String mediaUrl, String description) {
        // Fetch current user info to denormalize into the post
        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(doc -> {
                    Post post = new Post();
                    post.setAuthorUid(currentUid);
                    post.setAuthorUsername(doc.getString("username"));
                    post.setAuthorDisplayName(doc.getString("displayName"));
                    post.setAuthorProfilePicUrl(doc.getString("profilePicUrl"));
                    post.setMediaUrl(mediaUrl);
                    post.setMediaType(mediaType);
                    post.setDescription(description);
                    post.setTaggedUserIds(new ArrayList<>(taggedUserIds));
                    post.setLikesCount(0);
                    post.setCommentsCount(0);
                    post.setViewsCount(0);
                    post.setPinned(false);
                    post.setCreatedAt(Timestamp.now());

                    db.collection("posts").add(post)
                            .addOnSuccessListener(ref -> {
                                // Set the postId field on the document
                                ref.update("postId", ref.getId());
                                setLoading(false);
                                Toast.makeText(this, "Posted! 🎉", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Toast.makeText(this, "Failed to save post.", Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void setLoading(boolean loading) {
        binding.btnPost.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnPost.setText(loading ? "Posting..." : "Post");
    }
}
