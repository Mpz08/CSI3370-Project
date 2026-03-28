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
import com.example.cookfeed.models.User;
import com.example.cookfeed.profile.ViewProfileActivity;
import java.util.List;

public class FollowListAdapter extends RecyclerView.Adapter<FollowListAdapter.FollowViewHolder> {

    private final Context context;
    private final List<User> users;

    public FollowListAdapter(Context context, List<User> users) {
        this.context = context;
        this.users   = users;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_follow_user, parent, false);
        return new FollowViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() { return users.size(); }

    class FollowViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvDisplayName, tvUsername, tvFollowers;

        FollowViewHolder(View v) {
            super(v);
            imgAvatar     = v.findViewById(R.id.imgAvatar);
            tvDisplayName = v.findViewById(R.id.tvDisplayName);
            tvUsername    = v.findViewById(R.id.tvUsername);
            tvFollowers   = v.findViewById(R.id.tvFollowers);
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
}
