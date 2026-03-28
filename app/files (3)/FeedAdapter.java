package com.example.cookfeed.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cookfeed.R;
import com.example.cookfeed.models.Post;
import com.example.cookfeed.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_POST      = 0;
    private static final int TYPE_SUGGESTED = 1;

    private final Context context;
    private final List<Object> items;
    private final String currentUid;

    public FeedAdapter(Context context, List<Object> items, String currentUid) {
        this.context    = context;
        this.items      = items;
        this.currentUid = currentUid;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof Post ? TYPE_POST : TYPE_SUGGESTED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_POST) {
            View v = inflater.inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_suggested_user, parent, false);
            return new SuggestedUserViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).bind((Post) items.get(position));
        } else {
            ((SuggestedUserViewHolder) holder).bind((User) items.get(position));
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─── Post ViewHolder ───────────────────────────────────────────────────

    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgMedia;
        TextView tvUsername, tvDescription, tvLikes, tvComments, tvVideoTag, tvPinned;
        View btnLike;

        PostViewHolder(View v) {
            super(v);
            imgAvatar     = v.findViewById(R.id.imgAvatar);
            imgMedia      = v.findViewById(R.id.imgMedia);
            tvUsername    = v.findViewById(R.id.tvUsername);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvLikes       = v.findViewById(R.id.tvLikes);
            tvComments    = v.findViewById(R.id.tvComments);
            tvVideoTag    = v.findViewById(R.id.tvVideoTag);
            tvPinned      = v.findViewById(R.id.tvPinned);
            btnLike       = v.findViewById(R.id.btnLike);
        }

        void bind(Post post) {
            tvUsername.setText("@" + post.getAuthorUsername());
            tvDescription.setText(post.getDescription());
            tvLikes.setText(post.getLikesCount() + " likes");
            tvComments.setText(post.getCommentsCount() + " comments");
            tvPinned.setVisibility(post.isPinned() ? View.VISIBLE : View.GONE);
            tvVideoTag.setVisibility("video".equals(post.getMediaType()) ? View.VISIBLE : View.GONE);

            Glide.with(context).load(post.getAuthorProfilePicUrl())
                    .circleCrop().placeholder(R.drawable.ic_default_avatar).into(imgAvatar);

            Glide.with(context).load(post.getMediaUrl())
                    .centerCrop().placeholder(R.color.divider).into(imgMedia);

            btnLike.setOnClickListener(v -> toggleLike(post));
        }

        private void toggleLike(Post post) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String likeId = currentUid + "_" + post.getPostId();
            db.collection("likes").document(likeId).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            // Unlike
                            doc.getReference().delete();
                            db.collection("posts").document(post.getPostId())
                                    .update("likesCount",
                                            com.google.firebase.firestore.FieldValue.increment(-1));
                        } else {
                            // Like
                            Map<String, Object> like = new HashMap<>();
                            like.put("userId", currentUid);
                            like.put("postId", post.getPostId());
                            like.put("createdAt", com.google.firebase.Timestamp.now());
                            db.collection("likes").document(likeId).set(like);
                            db.collection("posts").document(post.getPostId())
                                    .update("likesCount",
                                            com.google.firebase.firestore.FieldValue.increment(1));
                        }
                    });
        }
    }

    // ─── Suggested User ViewHolder ─────────────────────────────────────────

    class SuggestedUserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUsername, tvDisplayName;
        View btnFollow;

        SuggestedUserViewHolder(View v) {
            super(v);
            imgAvatar     = v.findViewById(R.id.imgAvatar);
            tvUsername    = v.findViewById(R.id.tvUsername);
            tvDisplayName = v.findViewById(R.id.tvDisplayName);
            btnFollow     = v.findViewById(R.id.btnFollow);
        }

        void bind(User user) {
            tvUsername.setText("@" + user.getUsername());
            tvDisplayName.setText(user.getDisplayName());

            Glide.with(context).load(user.getProfilePicUrl())
                    .circleCrop().placeholder(R.drawable.ic_default_avatar).into(imgAvatar);

            btnFollow.setOnClickListener(v -> followUser(user));
        }

        private void followUser(User user) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String followId = currentUid + "_" + user.getUid();
            Map<String, Object> follow = new HashMap<>();
            follow.put("followerId", currentUid);
            follow.put("followeeId", user.getUid());
            follow.put("createdAt", com.google.firebase.Timestamp.now());
            db.collection("follows").document(followId).set(follow)
                    .addOnSuccessListener(unused -> {
                        // Increment counts
                        db.collection("users").document(user.getUid())
                                .update("followersCount",
                                        com.google.firebase.firestore.FieldValue.increment(1));
                        db.collection("users").document(currentUid)
                                .update("followingCount",
                                        com.google.firebase.firestore.FieldValue.increment(1));
                        Toast.makeText(context, "Following @" + user.getUsername(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
