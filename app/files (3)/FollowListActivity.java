package com.example.cookfeed.profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cookfeed.adapters.FollowListAdapter;
import com.example.cookfeed.databinding.ActivityFollowListBinding;
import com.example.cookfeed.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowListActivity extends AppCompatActivity {

    private ActivityFollowListBinding binding;
    private FirebaseFirestore db;
    private String type; // "followers" or "following"
    private String uid;
    private final List<User> users = new ArrayList<>();
    private FollowListAdapter adapter;

    // Sort state: 0 = newest, 1 = oldest, 2 = A-Z, 3 = Z-A
    private int sortMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        type = getIntent().getStringExtra("type");
        uid  = getIntent().getStringExtra("uid");

        binding.toolbar.setTitle(type.equals("followers") ? "Followers" : "Following");
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new FollowListAdapter(this, users);
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerUsers.setAdapter(adapter);

        // Sort buttons
        binding.btnSortRecent.setOnClickListener(v -> { sortMode = 0; applySort(); });
        binding.btnSortOldest.setOnClickListener(v -> { sortMode = 1; applySort(); });
        binding.btnSortAZ.setOnClickListener(v -> { sortMode = 2; applySort(); });
        binding.btnSortZA.setOnClickListener(v -> { sortMode = 3; applySort(); });

        loadUsers();
    }

    private void loadUsers() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String lookupField  = type.equals("followers") ? "followeeId" : "followerId";
        String returnField  = type.equals("followers") ? "followerId" : "followeeId";

        db.collection("follows")
                .whereEqualTo(lookupField, uid)
                .get()
                .addOnSuccessListener(snap -> {
                    List<String> uids = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        uids.add(doc.getString(returnField));
                    }

                    if (uids.isEmpty()) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Fetch user profiles (batch of 10 max per Firestore whereIn)
                    List<String> batch = uids.subList(0, Math.min(10, uids.size()));
                    db.collection("users")
                            .whereIn("uid", batch)
                            .get()
                            .addOnSuccessListener(userSnap -> {
                                users.clear();
                                for (QueryDocumentSnapshot doc : userSnap) {
                                    users.add(doc.toObject(User.class));
                                }
                                binding.progressBar.setVisibility(View.GONE);
                                applySort();
                            });
                })
                .addOnFailureListener(e -> binding.progressBar.setVisibility(View.GONE));
    }

    private void applySort() {
        switch (sortMode) {
            case 0: // Newest (by createdAt desc)
                Collections.sort(users, (a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                });
                break;
            case 1: // Oldest
                Collections.sort(users, (a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return a.getCreatedAt().compareTo(b.getCreatedAt());
                });
                break;
            case 2: // A-Z
                Collections.sort(users, (a, b) ->
                        a.getUsername().compareToIgnoreCase(b.getUsername()));
                break;
            case 3: // Z-A
                Collections.sort(users, (a, b) ->
                        b.getUsername().compareToIgnoreCase(a.getUsername()));
                break;
        }
        adapter.notifyDataSetChanged();
    }
}
