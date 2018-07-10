package com.example.maddiew.instagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.maddiew.instagram.model.Post;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static List<Post> mPosts;
    public static Context context;
    // pass in tweets array in constructor
    public PostAdapter(List<Post> posts) {
        mPosts = posts;
    }

    // for each row, inflate layout and cache references in viewholder
    // invoked when i need to make a new row
    // dont have to keep making rows
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }
    // bind values based on position of element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get data according to position
        Post post = mPosts.get(position);
        // populate views according to this data
        holder.tvHandle.setText(post.getUser().getString(MainActivity.KEY_HANDLE));
        holder.tvCaption.setText(post.getDescription());
        Glide.with(context)
                .load(post.getImage().getUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(75, 0)))
                .into(holder.ivPicture);
    }

    // crreate viewholder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPicture;
        public TextView tvHandle;
        public TextView tvCaption;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvCaption = itemView.findViewById(R.id.tvCaption);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}
