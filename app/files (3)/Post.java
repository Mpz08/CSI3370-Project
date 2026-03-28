package com.example.cookfeed.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class Post {
    private String postId;
    private String authorUid;
    private String authorUsername;
    private String authorDisplayName;
    private String authorProfilePicUrl;
    private String mediaUrl;        // URL of the photo or video in Firebase Storage
    private String mediaType;       // "photo" or "video"
    private String description;
    private List<String> taggedUserIds;   // list of uid's that were tagged/mentioned
    private int likesCount;
    private int commentsCount;
    private int viewsCount;
    private boolean pinned;
    private Timestamp createdAt;

    public Post() {} // Required for Firestore

    // Getters
    public String getPostId() { return postId; }
    public String getAuthorUid() { return authorUid; }
    public String getAuthorUsername() { return authorUsername; }
    public String getAuthorDisplayName() { return authorDisplayName; }
    public String getAuthorProfilePicUrl() { return authorProfilePicUrl; }
    public String getMediaUrl() { return mediaUrl; }
    public String getMediaType() { return mediaType; }
    public String getDescription() { return description; }
    public List<String> getTaggedUserIds() { return taggedUserIds; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public int getViewsCount() { return viewsCount; }
    public boolean isPinned() { return pinned; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setPostId(String postId) { this.postId = postId; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setAuthorDisplayName(String authorDisplayName) { this.authorDisplayName = authorDisplayName; }
    public void setAuthorProfilePicUrl(String authorProfilePicUrl) { this.authorProfilePicUrl = authorProfilePicUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public void setDescription(String description) { this.description = description; }
    public void setTaggedUserIds(List<String> taggedUserIds) { this.taggedUserIds = taggedUserIds; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    public void setViewsCount(int viewsCount) { this.viewsCount = viewsCount; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** Engagement score used for trending ranking */
    public int getEngagementScore() {
        return likesCount * 3 + commentsCount * 2 + viewsCount;
    }
}
