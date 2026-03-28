package com.example.cookfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cookfeed.R;
import com.example.cookfeed.models.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.List;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.PostViewHolder> {

    private final Context context;
    private final List<Post> posts;
    private final String ownerUid;
    private final String tabType; // "all", "photo", "video", "tagged"

    public ProfilePostsAdapter(Context context, List<Post> posts, String ownerUid, String tabType) {
        this.context  = context;
        this.posts    = posts;
        this.ownerUid = ownerUid;
        this.tabType  = tabType;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() { return posts.size(); }

    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgMedia;
        TextView tvUsername, tvDescription, tvLikes, tvComments, tvVideoTag, tvPinned;
        View btnLike, btnMore;

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
            btnMore       = v.findViewById(R.id.btnMore);
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

            // Show ⋮ more menu only on own posts in non-tagged tab
            if (btnMore != null && !"tagged".equals(tabType)) {
                btnMore.setVisibility(View.VISIBLE);
                btnMore.setOnClickListener(v -> showPostMenu(v, post));
            } else if (btnMore != null) {
                btnMore.setVisibility(View.GONE);
            }
        }

        private void showPostMenu(View anchor, Post post) {
            PopupMenu menu = new PopupMenu(context, anchor);
            if (post.isPinned()) {
                menu.getMenu().add(0, 1, 0, "📌 Unpin post");
            } else {
                menu.getMenu().add(0, 2, 0, "📌 Pin post");
            }
            menu.getMenu().add(0, 3, 1, "🗑 Delete post");

            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1: unpinPost(post); return true;
                    case 2: pinPost(post);   return true;
                    case 3: deletePost(post); return true;
                }
                return false;
            });
            menu.show();
        }

        private void pinPost(Post post) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Check current pinned count
            db.collection("posts")
                    .whereEqualTo("authorUid", ownerUid)
                    .whereEqualTo("pinned", true)
                    .get()
                    .addOnSuccessListener(snap -> {
                        if (snap.size() >= 3) {
                            Toast.makeText(context, "You can only pin up to 3 posts.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        db.collection("posts").document(post.getPostId())
                                .update("pinned", true)
                                .addOnSuccessListener(unused -> {
                                    post.setPinned(true);
                                    notifyItemChanged(getAdapterPosition());
                                    Toast.makeText(context, "Post pinned 📌", Toast.LENGTH_SHORT).show();
                                });
                    });
        }

        private void unpinPost(Post post) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").document(post.getPostId())
                    .update("pinned", false)
                    .addOnSuccessListener(unused -> {
                        post.setPinned(false);
                        notifyItemChanged(getAdapterPosition());
                        Toast.makeText(context, "Post unpinned", Toast.LENGTH_SHORT).show();
                    });
        }

        private void deletePost(Post post) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").document(post.getPostId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        int pos = getAdapterPosition();
                        posts.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(context, "Post deleted.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
