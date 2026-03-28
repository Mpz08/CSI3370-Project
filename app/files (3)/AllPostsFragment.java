package com.example.cookfeed.profile.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookfeed.adapters.ProfilePostsAdapter;
import com.example.cookfeed.models.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AllPostsFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private String uid;
    private final List<Post> posts = new ArrayList<>();
    private ProfilePostsAdapter adapter;

    public static AllPostsFragment newInstance(String uid) {
        AllPostsFragment f = new AllPostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) uid = getArguments().getString(ARG_UID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecyclerView rv = new RecyclerView(requireContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfilePostsAdapter(requireContext(), posts, uid, "all");
        rv.setAdapter(adapter);
        loadPosts();
        return rv;
    }

    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Load pinned posts first
        db.collection("posts")
                .whereEqualTo("authorUid", uid)
                .whereEqualTo("pinned", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(pinnedSnap -> {
                    posts.clear();
                    for (QueryDocumentSnapshot doc : pinnedSnap) {
                        Post p = doc.toObject(Post.class);
                        p.setPostId(doc.getId());
                        posts.add(p);
                    }

                    // Then load unpinned posts
                    db.collection("posts")
                            .whereEqualTo("authorUid", uid)
                            .whereEqualTo("pinned", false)
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(unpinnedSnap -> {
                                for (QueryDocumentSnapshot doc : unpinnedSnap) {
                                    Post p = doc.toObject(Post.class);
                                    p.setPostId(doc.getId());
                                    posts.add(p);
                                }
                                adapter.notifyDataSetChanged();
                            });
                });
    }
}
