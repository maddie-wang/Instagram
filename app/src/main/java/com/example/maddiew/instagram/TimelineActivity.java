package com.example.maddiew.instagram;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.maddiew.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 20;

    ArrayList<Post> posts;
    RecyclerView rvPosts;
    PostAdapter postAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        rvPosts = findViewById(R.id.rvPosts);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postAdapter);

        // set up refresh container
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                loadTopPosts();
                swipeContainer.setRefreshing(false);
            }
        });

        loadTopPosts();

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
