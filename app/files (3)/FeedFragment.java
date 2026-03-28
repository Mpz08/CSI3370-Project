package com.example.cookfeed.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cookfeed.adapters.FeedAdapter;
import com.example.cookfeed.databinding.FragmentFeedBinding;
import com.example.cookfeed.models.Post;
import com.example.cookfeed.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private FirebaseFirestore db;
    private String currentUid;
    private FeedAdapter adapter;
    private final List<Object> feedItems = new ArrayList<>(); // Post or User (suggested)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new FeedAdapter(requireContext(), feedItems, currentUid);
        binding.recyclerFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerFeed.setAdapter(adapter);

        loadFeed();
    }

    private void loadFeed() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // Step 1: Get the list of users the current user follows
        db.collection("follows")
                .whereEqualTo("followerId", currentUid)
                .get()
                .addOnSuccessListener(followSnap -> {
                    List<String> followingIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : followSnap) {
                        followingIds.add(doc.getString("followeeId"));
                    }

                    if (followingIds.isEmpty()) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        loadSuggestedUsers(new ArrayList<>()); // Still show suggestions
                        return;
                    }

                    // Step 2: Load posts from those users, newest first
                    // Firestore whereIn supports max 10 ids at a time
                    List<String> batch = followingIds.subList(0, Math.min(10, followingIds.size()));
                    db.collection("posts")
                            .whereIn("authorUid", batch)
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .limit(30)
                            .get()
                            .addOnSuccessListener(postSnap -> {
                                List<Post> posts = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : postSnap) {
                                    Post p = doc.toObject(Post.class);
                                    p.setPostId(doc.getId());
                                    posts.add(p);
                                }
                                buildFeedWithSuggestions(posts, followingIds);
                            })
                            .addOnFailureListener(e -> binding.progressBar.setVisibility(View.GONE));
                });
    }

    /**
     * Interleaves posts with suggested-user cards every 5 posts.
     */
    private void buildFeedWithSuggestions(List<Post> posts, List<String> followingIds) {
        // Load a random suggested user not already followed
        db.collection("users")
                .limit(20)
                .get()
                .addOnSuccessListener(userSnap -> {
                    List<User> candidates = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : userSnap) {
                        User u = doc.toObject(User.class);
                        if (!u.getUid().equals(currentUid) && !followingIds.contains(u.getUid())) {
                            candidates.add(u);
                        }
                    }
                    Collections.shuffle(candidates);

                    feedItems.clear();
                    int suggestIdx = 0;
                    for (int i = 0; i < posts.size(); i++) {
                        feedItems.add(posts.get(i));
                        // Every 5 posts, insert a suggested user card
                        if ((i + 1) % 5 == 0 && suggestIdx < candidates.size()) {
                            feedItems.add(candidates.get(suggestIdx++));
                        }
                    }

                    binding.progressBar.setVisibility(View.GONE);
                    if (feedItems.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvEmpty.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadSuggestedUsers(List<String> followingIds) {
        db.collection("users").limit(10).get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot doc : snap) {
                        User u = doc.toObject(User.class);
                        if (!u.getUid().equals(currentUid)) {
                            feedItems.add(u);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
