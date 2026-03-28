package com.example.cookfeed.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.cookfeed.R;
import com.example.cookfeed.databinding.FragmentUserBinding;
import com.example.cookfeed.models.User;
import com.example.cookfeed.profile.EditProfileActivity;
import com.example.cookfeed.profile.CreatePostActivity;
import com.example.cookfeed.profile.FollowListActivity;
import com.example.cookfeed.profile.UserPostsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private FirebaseFirestore db;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadProfile();

        // Edit profile button
        binding.btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditProfileActivity.class)));

        // Create post FAB
        binding.fabCreatePost.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CreatePostActivity.class)));

        // Followers / Following click
        binding.tvFollowers.setOnClickListener(v -> openFollowList("followers"));
        binding.tvFollowing.setOnClickListener(v -> openFollowList("following"));

        // Setup 4-tab ViewPager for posts
        setupPostTabs();
    }

    private void loadProfile() {
        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user == null) return;

                        binding.tvDisplayName.setText(user.getDisplayName());
                        binding.tvUsername.setText("@" + user.getUsername());
                        binding.tvBio.setText(user.getBio());
                        binding.tvFollowers.setText(user.getFollowersCount() + "\nFollowers");
                        binding.tvFollowing.setText(user.getFollowingCount() + "\nFollowing");

                        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
                            Glide.with(this)
                                    .load(user.getProfilePicUrl())
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .into(binding.imgProfilePic);
                        }
                    }
                });
    }

    private void setupPostTabs() {
        UserPostsPagerAdapter pagerAdapter =
                new UserPostsPagerAdapter(this, currentUid);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("All");    break;
                case 1: tab.setText("Photos"); break;
                case 2: tab.setText("Videos"); break;
                case 3: tab.setText("Tagged"); break;
            }
        }).attach();
    }

    private void openFollowList(String type) {
        Intent intent = new Intent(requireContext(), FollowListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("uid", currentUid);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile(); // Refresh after editing
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
