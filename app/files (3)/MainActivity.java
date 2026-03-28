package com.example.cookfeed.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cookfeed.R;
import com.example.cookfeed.databinding.ActivityMainBinding;
import com.example.cookfeed.tabs.FeedFragment;
import com.example.cookfeed.tabs.TrendingFragment;
import com.example.cookfeed.tabs.UserFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load Feed tab by default
        if (savedInstanceState == null) {
            loadFragment(new FeedFragment());
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_feed) {
                fragment = new FeedFragment();
            } else if (id == R.id.nav_trending) {
                fragment = new TrendingFragment();
            } else if (id == R.id.nav_user) {
                fragment = new UserFragment();
            } else {
                return false;
            }
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
