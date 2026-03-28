package com.example.cookfeed.adapters;

import android.content.Context;
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
import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {

    private final Context context;
    private final List<Post> posts;
    private final String currentUid;

    public TrendingAdapter(Context context, List<Post> posts, String currentUid) {
        this.context    = context;
        this.posts      = posts;
        this.currentUid = currentUid;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trending_post, parent, false);
        return new TrendingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        holder.bind(posts.get(position), position);
    }

    @Override
    public int getItemCount() { return posts.size(); }

    class TrendingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedia, imgAvatar;
        TextView tvUsername, tvDescription, tvLikes, tvViews, tvComments;
        TextView tvFeaturedBadge, tvRank, tvVideoTag;

        TrendingViewHolder(View v) {
            super(v);
            imgMedia        = v.findViewById(R.id.imgMedia);
            imgAvatar       = v.findViewById(R.id.imgAvatar);
            tvUsername      = v.findViewById(R.id.tvUsername);
            tvDescription   = v.findViewById(R.id.tvDescription);
            tvLikes         = v.findViewById(R.id.tvLikes);
            tvViews         = v.findViewById(R.id.tvViews);
            tvComments      = v.findViewById(R.id.tvComments);
            tvFeaturedBadge = v.findViewById(R.id.tvFeaturedBadge);
            tvRank          = v.findViewById(R.id.tvRank);
            tvVideoTag      = v.findViewById(R.id.tvVideoTag);
        }

        void bind(Post post, int position) {
            tvUsername.setText("@" + post.getAuthorUsername());
            tvDescription.setText(post.getDescription());
            tvLikes.setText("❤️ " + post.getLikesCount());
            tvViews.setText("👁 " + post.getViewsCount());
            tvComments.setText("💬 " + post.getCommentsCount());
            tvVideoTag.setVisibility("video".equals(post.getMediaType()) ? View.VISIBLE : View.GONE);

            // Rank number
            tvRank.setText("#" + (position + 1));

            // Featured badge only on the #1 post
            tvFeaturedBadge.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

            Glide.with(context).load(post.getAuthorProfilePicUrl())
                    .circleCrop().placeholder(R.drawable.ic_default_avatar).into(imgAvatar);
            Glide.with(context).load(post.getMediaUrl())
                    .centerCrop().placeholder(R.color.divider).into(imgMedia);
        }
    }
}
