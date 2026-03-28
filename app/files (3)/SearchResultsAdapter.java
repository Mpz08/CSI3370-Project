package com.example.cookfeed.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cookfeed.R;
import com.example.cookfeed.models.Post;
import com.example.cookfeed.models.User;
import com.example.cookfeed.profile.ViewProfileActivity;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_POST = 1;

    private final Context context;
    private final List<Object> items;
    private final String currentUid;

    public SearchResultsAdapter(Context context, List<Object> items, String currentUid) {
        this.context    = context;
        this.items      = items;
        this.currentUid = currentUid;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof User ? TYPE_USER : TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(context);
        if (viewType == TYPE_USER) {
            View v = inf.inflate(R.layout.item_follow_user, parent, false);
            return new UserViewHolder(v);
        } else {
            View v = inf.inflate(R.layout.item_trending_post, parent, false);
            return new PostViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind((User) items.get(position));
        } else {
            ((PostViewHolder) holder).bind((Post) items.get(position));
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ── User result ──────────────────────────────────────────────────────────
    class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvDisplayName, tvUsername, tvFollowers;

        UserViewHolder(View v) {
            super(v);
            imgAvatar    = v.findViewById(R.id.imgAvatar);
            tvDisplayName = v.findViewById(R.id.tvDisplayName);
            tvUsername   = v.findViewById(R.id.tvUsername);
            tvFollowers  = v.findViewById(R.id.tvFollowers);
        }

        void bind(User user) {
            tvDisplayName.setText(user.getDisplayName());
            tvUsername.setText("@" + user.getUsername());
            tvFollowers.setText(user.getFollowersCount() + " followers");
            Glide.with(context).load(user.getProfilePicUrl())
                    .circleCrop().placeholder(R.drawable.ic_default_avatar).into(imgAvatar);
            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, ViewProfileActivity.class);
                i.putExtra("uid", user.getUid());
                context.startActivity(i);
            });
        }
    }

    // ── Post result ──────────────────────────────────────────────────────────
    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedia;
        TextView tvAuthor, tvDescription, tvLikes, tvViews, tvComments,
                 tvFeaturedBadge, tvRank, tvVideoTag;

        PostViewHolder(View v) {
            super(v);
            imgMedia        = v.findViewById(R.id.imgMedia);
            tvAuthor        = v.findViewById(R.id.tvAuthor);
            tvDescription   = v.findViewById(R.id.tvDescription);
            tvLikes         = v.findViewById(R.id.tvLikes);
            tvViews         = v.findViewById(R.id.tvViews);
            tvComments      = v.findViewById(R.id.tvComments);
            tvFeaturedBadge = v.findViewById(R.id.tvFeaturedBadge);
            tvRank          = v.findViewById(R.id.tvRank);
            tvVideoTag      = v.findViewById(R.id.tvVideoTag);
        }

        void bind(Post post) {
            tvRank.setVisibility(View.GONE);
            tvFeaturedBadge.setVisibility(View.GONE);
            tvAuthor.setText(post.getAuthorDisplayName() + " @" + post.getAuthorUsername());
            tvDescription.setText(post.getDescription());
            tvLikes.setText("❤️ " + post.getLikesCount());
            tvViews.setText("👁 " + post.getViewsCount());
            tvComments.setText("💬 " + post.getCommentsCount());
            tvVideoTag.setVisibility("video".equals(post.getMediaType()) ? View.VISIBLE : View.GONE);
            Glide.with(context).load(post.getMediaUrl())
                    .centerCrop().placeholder(R.color.divider).into(imgMedia);
        }
    }
}
