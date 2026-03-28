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
import com.example.cookfeed.models.User;
import java.util.List;

public class TagUserAdapter extends RecyclerView.Adapter<TagUserAdapter.TagUserViewHolder> {

    public interface OnUserSelectedListener {
        void onUserSelected(User user);
    }

    private final Context context;
    private final List<User> users;
    private final OnUserSelectedListener listener;

    public TagUserAdapter(Context context, List<User> users, OnUserSelectedListener listener) {
        this.context  = context;
        this.users    = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_tag_user, parent, false);
        return new TagUserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TagUserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() { return users.size(); }

    class TagUserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvDisplayName, tvUsername;

        TagUserViewHolder(View v) {
            super(v);
            imgAvatar     = v.findViewById(R.id.imgAvatar);
            tvDisplayName = v.findViewById(R.id.tvDisplayName);
            tvUsername    = v.findViewById(R.id.tvUsername);
        }

        void bind(User user) {
            tvDisplayName.setText(user.getDisplayName());
            tvUsername.setText("@" + user.getUsername());
            Glide.with(context).load(user.getProfilePicUrl())
                    .circleCrop().placeholder(R.drawable.ic_default_avatar).into(imgAvatar);
            itemView.setOnClickListener(v -> listener.onUserSelected(user));
        }
    }
}
