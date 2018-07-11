package com.example.maddiew.instagram;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.maddiew.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 20;

    ArrayList<Post> posts;
    RecyclerView rvPosts;
    PostAdapter postAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rvPosts);

        rvPosts = rootView.findViewById(R.id.rvPosts);
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setAdapter(postAdapter);

        loadTopPosts();
        return rootView;

    }
    private void loadTopPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        postQuery.getTop().withUser();
        postQuery.orderByDescending("createdAt");
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.clear();
                    posts.addAll(objects);
                    postAdapter.notifyDataSetChanged();
                    //for (int i = 0; i < objects.size(); i++) {
                    //Log.d("Home", "Post id " + i + " = " +
                    //       objects.get(i).getDescription() + " Username "
                    //      + objects.get(i).getUser().getUsername());
                    //}
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                    e.printStackTrace();
                }
            }
        });
    }


}
