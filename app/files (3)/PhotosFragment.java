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

public class PhotosFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private String uid;
    private final List<Post> posts = new ArrayList<>();
    private ProfilePostsAdapter adapter;

    public static PhotosFragment newInstance(String uid) {
        PhotosFragment f = new PhotosFragment();
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
        adapter = new ProfilePostsAdapter(requireContext(), posts, uid, "photo");
        rv.setAdapter(adapter);
        loadPhotos();
        return rv;
    }

    private void loadPhotos() {
        FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo("authorUid", uid)
                .whereEqualTo("mediaType", "photo")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    posts.clear();
                    for (QueryDocumentSnapshot doc : snap) {
                        Post p = doc.toObject(Post.class);
                        p.setPostId(doc.getId());
                        posts.add(p);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
