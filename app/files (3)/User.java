package com.example.cookfeed.models;

import com.google.firebase.Timestamp;

public class User {
    private String uid;
    private String email;
    private String username;
    private String displayName;
    private String profilePicUrl;
    private String bio;
    private int followersCount;
    private int followingCount;
    private Timestamp createdAt;

    public User() {} // Required for Firestore

    public User(String uid, String email, String username, String displayName,
                String profilePicUrl, String bio, int followersCount, int followingCount) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.bio = bio;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public String getBio() { return bio; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
    public void setBio(String bio) { this.bio = bio; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
