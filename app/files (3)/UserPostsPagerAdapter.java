package com.example.cookfeed.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.cookfeed.profile.tabs.AllPostsFragment;
import com.example.cookfeed.profile.tabs.PhotosFragment;
import com.example.cookfeed.profile.tabs.TaggedFragment;
import com.example.cookfeed.profile.tabs.VideosFragment;

public class UserPostsPagerAdapter extends FragmentStateAdapter {

    private final String uid;

    public UserPostsPagerAdapter(@NonNull Fragment fragment, String uid) {
        super(fragment);
        this.uid = uid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return AllPostsFragment.newInstance(uid);
            case 1: return PhotosFragment.newInstance(uid);
            case 2: return VideosFragment.newInstance(uid);
            case 3: return TaggedFragment.newInstance(uid);
            default: return AllPostsFragment.newInstance(uid);
        }
    }

    @Override
    public int getItemCount() { return 4; }
}
