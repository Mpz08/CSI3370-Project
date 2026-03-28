package com.example.cookfeed.tabs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cookfeed.adapters.TrendingAdapter;
import com.example.cookfeed.adapters.SearchResultsAdapter;
import com.example.cookfeed.databinding.FragmentTrendingBinding;
import com.example.cookfeed.models.Post;
import com.example.cookfeed.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendingFragment extends Fragment {

    private FragmentTrendingBinding binding;
    private FirebaseFirestore db;
    private String currentUid;
    private TrendingAdapter trendingAdapter;
    private SearchResultsAdapter searchAdapter;
    private final List<Post> trendingPosts = new ArrayList<>();
    private final List<Object> searchResults = new ArrayList<>(); // User or Post

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTrendingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Trending recycler
        trendingAdapter = new TrendingAdapter(requireContext(), trendingPosts, currentUid);
        binding.recyclerTrending.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerTrending.setAdapter(trendingAdapter);

        // Search results recycler (hidden by default)
        searchAdapter = new SearchResultsAdapter(requireContext(), searchResults, currentUid);
        binding.recyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerSearch.setAdapter(searchAdapter);

        loadTrending();
        setupSearch();
    }

    private void loadTrending() {
        binding.progressBar.setVisibility(View.VISIBLE);
        // Load all posts and sort by engagement score client-side
        db.collection("posts")
                .orderBy("likesCount", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(snap -> {
                    trendingPosts.clear();
                    for (QueryDocumentSnapshot doc : snap) {
                        Post p = doc.toObject(Post.class);
                        p.setPostId(doc.getId());
                        trendingPosts.add(p);
                    }
                    // Sort by total engagement score
                    Collections.sort(trendingPosts,
                            (a, b) -> b.getEngagementScore() - a.getEngagementScore());
                    binding.progressBar.setVisibility(View.GONE);
                    trendingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> binding.progressBar.setVisibility(View.GONE));
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                if (query.isEmpty()) {
                    showTrending();
                } else {
                    performSearch(query);
                }
            }
        });
    }

    private void performSearch(String query) {
        showSearchResults();
        searchResults.clear();
        searchAdapter.notifyDataSetChanged();

        // Search users by username
        db.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(5)
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot doc : snap) {
                        User u = doc.toObject(User.class);
                        searchResults.add(u);
                    }
                    searchAdapter.notifyDataSetChanged();
                });

        // Search posts by description keywords
        db.collection("posts")
                .orderBy("description")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(10)
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot doc : snap) {
                        Post p = doc.toObject(Post.class);
                        p.setPostId(doc.getId());
                        searchResults.add(p);
                    }
                    searchAdapter.notifyDataSetChanged();
                });
    }

    private void showTrending() {
        binding.recyclerTrending.setVisibility(View.VISIBLE);
        binding.tvFeaturedLabel.setVisibility(View.VISIBLE);
        binding.recyclerSearch.setVisibility(View.GONE);
    }

    private void showSearchResults() {
        binding.recyclerTrending.setVisibility(View.GONE);
        binding.tvFeaturedLabel.setVisibility(View.GONE);
        binding.recyclerSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
